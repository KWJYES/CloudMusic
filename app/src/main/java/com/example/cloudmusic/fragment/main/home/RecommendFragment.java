package com.example.cloudmusic.fragment.main.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.AgentWebActivity;
import com.example.cloudmusic.activities.MusicListActivity;
import com.example.cloudmusic.activities.PlayerActivity;
import com.example.cloudmusic.adapter.banner.RecommendBannerAdapter;
import com.example.cloudmusic.adapter.recyclerview.MusicListRecommendAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentRecommendBinding;
import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.fragment.main.home.RequestRecommendFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.home.StateRecommendFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.youth.banner.indicator.CircleIndicator;

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
            getRecommendMusicList();
        });
        binding.recommendBanner.setBannerGalleryMZ(15);
        binding.bannerLoading.show();
        binding.musicListRecommend.musicListLoading.show();
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
            }
            getRecommendMusicList();
        });
        rvm.bannerRequestResult.observe(this, this::setBanner);
        rvm.recommendMusicListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "获取推荐歌单失败", Toast.LENGTH_SHORT).show();
            }
            binding.smartRefreshLayout.finishRefresh();
            binding.musicListRecommend.musicListLoading.hide();
        });
        rvm.recommendMusicListResult.observe(this, musicLists -> {
            svm.musicListList.setValue(musicLists);
            setRecommendMusicListRV();
        });
        rvm.song.observe(this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), PlayerActivity.class));
            }
        });
    }

    private void setRecommendMusicListRV() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.musicListRecommend.recommendMusicListRV.setLayoutManager(layoutManager);
        MusicListRecommendAdapter adapter = new MusicListRecommendAdapter(svm.musicListList.getValue());
        adapter.setClickCallback(musicList -> {
            Intent intent = new Intent(getActivity(), MusicListActivity.class);
            intent.putExtra("musicList", musicList);
            startActivity(intent);
        });
        binding.musicListRecommend.recommendMusicListRV.setAdapter(adapter);
    }

    @Override
    protected void initSomeData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity=false;
    }

    @Override
    protected void getInternetData() {
        getBannerData();
    }

    /**
     * 攻取推荐歌单
     */
    private void getRecommendMusicList() {
        if (svm.musicListList.getValue() == null || svm.musicListList.getValue().size() == 0) {
            rvm.requestRecommendMusicList();
        }
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
            Log.d("TAG","banner url--->"+banner.getUrl());
            Log.d("TAG","banner song--->"+banner.getSong().getSongId()+" "+banner.getSong().getName());
            if (banner.getUrl() != null&&!banner.getUrl().equals("")&&!banner.getUrl().equals("null")) {
                Intent intent = new Intent(getActivity(), AgentWebActivity.class);
                intent.putExtra("banner", banner);
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }else if (banner.getSong()!=null&&banner.getSong().getSongId()!=null&&!banner.getSong().getSongId().equals("null")&&banner.getSong().getName()!=null&&!banner.getSong().getName().equals("null")){
                if(CloudMusic.isStartPlayerActivity) return;
                CloudMusic.isStartPlayerActivity=true;
                rvm.play(banner.getSong());
            }
        });
        binding.recommendBanner.addBannerLifecycleObserver(getActivity()).setAdapter(bannerAdapter)
                .setIndicator(new CircleIndicator(getActivity()));
    }

}