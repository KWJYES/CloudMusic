package com.example.cloudmusic.fragment.main.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.MusicListActivity;
import com.example.cloudmusic.adapter.recyclerview.PlayListAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentMusicList2Binding;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.request.fragment.main.home.RequestMusicListFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.home.StateMusicListFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;

import java.util.ArrayList;
import java.util.List;


public class MusicListFragment extends BaseFragment implements View.OnClickListener{

    FragmentMusicList2Binding binding;
    StateMusicListFragmentViewModel svm;
    RequestMusicListFragmentViewModel rvm;
    private List<PlayList> playLists;
    private PlayListAdapter adapter;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_music_list2,container,false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMusicListFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMusicListFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        playLists=new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    protected void getInternetData() {
        rvm.getPlayList(svm.firstSelect.getValue(),playLists.size());
    }

    @Override
    protected void initView() {
        binding.playlistLoadFalse.setVisibility(View.GONE);
        binding.playListLoading.show();
        svm.firstSelect.setValue("全部歌单");
        setSelectButtonListener();
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.playListLoading.show();
            playLists.clear();
            rvm.getPlayList(svm.firstSelect.getValue(), playLists.size());
        });
        binding.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.getPlayList(svm.firstSelect.getValue(), playLists.size()));
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.playListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.playlistLoadFalse.setVisibility(View.VISIBLE);
            }
            binding.playListLoading.hide();
            binding.smartRefreshLayout.finishRefresh();
            binding.smartRefreshLayout.finishLoadMore();
        });
        rvm.playLists.observe(this, pls -> {
            if(playLists.size()==0){
                setPlayListRV(pls);
            }else {
                morePlayList(pls);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void morePlayList(List<PlayList> pls) {
        playLists.addAll(pls);
        adapter.notifyDataSetChanged();
    }

    private void setPlayListRV(List<PlayList> pls) {
        playLists.addAll(pls);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.playlistRv.setLayoutManager(layoutManager);
        adapter = new PlayListAdapter(playLists);
        adapter.setClickCallback(playList -> {
            Intent intent=new Intent(getActivity(), MusicListActivity.class);
            intent.putExtra("type","playlist");
            intent.putExtra("playlist",playList);
            startActivity(intent);
        });
        binding.playlistRv.setAdapter(adapter);
    }

    private void setSelectButtonListener() {
        binding.sb0.setOnClickListener(this);
        binding.sb1.setOnClickListener(this);
        binding.sb2.setOnClickListener(this);
        binding.sb3.setOnClickListener(this);
        binding.sb4.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sb0:svm.firstSelect.setValue("全部歌单");break;
            case R.id.sb1:svm.firstSelect.setValue("华语");break;
            case R.id.sb2:svm.firstSelect.setValue("古风");break;
            case R.id.sb3:svm.firstSelect.setValue("欧美");break;
            case R.id.sb4:svm.firstSelect.setValue("流行");break;
        }
        binding.playListLoading.show();
        playLists.clear();
        rvm.getPlayList(svm.firstSelect.getValue(),playLists.size());
    }
}