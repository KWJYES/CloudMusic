package com.example.cloudmusic.fragment.main.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.AgentWebActivity;
import com.example.cloudmusic.activities.MVActivity;
import com.example.cloudmusic.activities.MusicListActivity;
import com.example.cloudmusic.activities.PlayerActivity;
import com.example.cloudmusic.adapter.banner.RecommendBannerAdapter;
import com.example.cloudmusic.adapter.recyclerview.MVAdapter;
import com.example.cloudmusic.adapter.recyclerview.MusicListRecommendAdapter;
import com.example.cloudmusic.adapter.recyclerview.NewSongRecommendAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentRecommendBinding;
import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.fragment.main.home.RequestRecommendFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.home.StateRecommendFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.MVItemClickCallback;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecommendFragment extends BaseFragment {

    FragmentRecommendBinding binding;
    StateRecommendFragmentViewModel svm;
    RequestRecommendFragmentViewModel rvm;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommend, container, false);
        svm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(StateRecommendFragmentViewModel.class);
        rvm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(RequestRecommendFragmentViewModel.class);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        //下拉刷新
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getBannerData();
        });
        binding.recommendBanner.setBannerGalleryMZ(15);
        binding.bannerLoading.show();
        binding.newMusicRecommend.newSongLoading.show();
        binding.musicListRecommend.musicListLoading.show();
        binding.mvRecommend.mvLoading.show();
    }

    /**
     * 监测数据变化
     */
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.bannerRequestState.observe(this, s -> {
            binding.bannerLoading.hide();
            if (s.equals(CloudMusic.FAILURE)) {
                Banner banner = new Banner();
                banner.setPic("isFail");
                banner.setTypeTitle("加载失败");
                List<Banner> bannerList = new ArrayList<>();
                bannerList.add(banner);
                setBanner(bannerList);
                binding.smartRefreshLayout.finishRefresh();
            } else {
                Log.d("TAG", "Banner数据请求成功");
                rvm.requestRecommendMusicList();//musicList
            }
        });
        rvm.bannerRequestResult.observe(this, this::setBanner);
        rvm.recommendMusicListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "获取推荐歌单失败", Toast.LENGTH_SHORT).show();
                binding.smartRefreshLayout.finishRefresh();
            } else {
                rvm.requestNewSong();//newSong
            }
            binding.musicListRecommend.musicListLoading.hide();
        });
        rvm.recommendMusicListResult.observe(this, musicLists -> {
            svm.musicListList.setValue(musicLists);
            setRecommendMusicListRV();
        });
        rvm.bannerSong.observe(this, song -> Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), PlayerActivity.class)));
        rvm.newSongListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "获取新歌推荐失败", Toast.LENGTH_SHORT).show();
                binding.smartRefreshLayout.finishRefresh();
            } else {
                binding.newMusicRecommend.newSongLoading.hide();
                rvm.requestMV();//MV
            }
        });
        rvm.newSongList.observe(this, songs -> {
            svm.newSongList.setValue(songs);
            setNewSongRV();
        });
        rvm.newSong.observe(this, song -> {
            MyEvent myEvent = new MyEvent();
            myEvent.setMsg(6);
            myEvent.setSong(song);
            EventBus.getDefault().post(myEvent);
        });
        rvm.mvRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "获取MV推荐失败", Toast.LENGTH_SHORT).show();
            } else {
                binding.mvRecommend.mvLoading.hide();
            }
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.mvList.observe(this, this::setMVRecyclerView);
    }

    private void setMVRecyclerView(List<MV> mvs) {
        List<MV> mvList = new ArrayList<>(mvs);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.mvRecommend.myVRecyclerView.setLayoutManager(layoutManager);
        MVAdapter adapter = new MVAdapter(mvList);
        adapter.setItemClickCallback(mv -> {
            Intent intent = new Intent(getActivity(), MVActivity.class);
            intent.putExtra("mvId", mv.getMvId());
            intent.putExtra("mvTittle", mv.getName());
            startActivity(intent);
        });
        binding.mvRecommend.myVRecyclerView.setAdapter(adapter);
    }

    private void setNewSongRV() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        binding.newMusicRecommend.newSongRecyclerView.setLayoutManager(layoutManager);
        NewSongRecommendAdapter adapter = new NewSongRecommendAdapter(svm.newSongList.getValue());
        adapter.setClickCallback(song -> {
            if (CloudMusic.isGettingSongUrl) return;
            rvm.playNewSong(song);
        });
        binding.newMusicRecommend.newSongRecyclerView.setAdapter(adapter);
    }

    private void setRecommendMusicListRV() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.musicListRecommend.recommendMusicListRV.setLayoutManager(layoutManager);
        MusicListRecommendAdapter adapter = new MusicListRecommendAdapter(svm.musicListList.getValue());
        adapter.setClickCallback(musicList -> {
            Intent intent = new Intent(getActivity(), MusicListActivity.class);
            intent.putExtra("type", "musicList");
            intent.putExtra("playlist", musicList);
            startActivity(intent);
        });
        binding.musicListRecommend.recommendMusicListRV.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity = false;
    }

    @Override
    protected void getInternetData() {
        getBannerData();
    }

    /**
     * 播放加载动画与背景
     * 获取banner数据
     */
    private void getBannerData() {
        if (svm.bannerDataList.getValue() == null || svm.bannerDataList.getValue().size() == 0) {
            Banner banner = new Banner();
            banner.setPic("isLoading");
            banner.setTypeTitle("加载中...");
            List<Banner> bannerList = new ArrayList<>();
            bannerList.add(banner);
            setBanner(bannerList);
            rvm.requestBannerData();
        }
    }

    /**
     * 加载banner
     */
    private void setBanner(List<Banner> bannerData) {
        List<Banner> bannerList = new ArrayList<>(bannerData);
        RecommendBannerAdapter bannerAdapter = new RecommendBannerAdapter(bannerList);
        bannerAdapter.setClickCallback(banner -> {
            Log.d("TAG", "banner url--->" + banner.getUrl());
            Log.d("TAG", "banner song--->" + banner.getSong().getSongId() + " " + banner.getSong().getName());
            if (banner.getUrl() != null && !banner.getUrl().equals("") && !banner.getUrl().equals("null")) {
                Intent intent = new Intent(getActivity(), AgentWebActivity.class);
                intent.putExtra("banner", banner);
                Objects.requireNonNull(getActivity()).startActivity(intent);
            } else if (banner.getSong() != null && banner.getSong().getSongId() != null && !banner.getSong().getSongId().equals("null") && banner.getSong().getName() != null && !banner.getSong().getName().equals("null")) {
                if (CloudMusic.isStartPlayerActivity) return;
                CloudMusic.isStartPlayerActivity = true;
                rvm.playBannerSong(banner.getSong());
            }
        });
        binding.recommendBanner.addBannerLifecycleObserver(getActivity()).setAdapter(bannerAdapter)
                .setIndicator(new CircleIndicator(getActivity()));
    }

}