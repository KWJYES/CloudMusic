package com.example.cloudmusic.fragment.search.searched;

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
import com.example.cloudmusic.databinding.FragmentMusicListBinding;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.request.fragment.search.searched.RequestMusicListViewModel;
import com.example.cloudmusic.CloudMusic;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends BaseFragment {

    FragmentMusicListBinding binding;
    RequestMusicListViewModel rvm;
    private String keyword;
    private List<PlayList> playLists;
    private PlayListAdapter adapter;

    public MusicListFragment(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_music_list,container,false);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMusicListViewModel.class);
        binding.setLifecycleOwner(this);
        playLists=new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    protected void getInternetData() {
        rvm.search(keyword);
    }

    @Override
    protected void initView() {
        binding.Loading.show();
        binding.dataFalse.setVisibility(View.GONE);
        binding.oneSongSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.Loading.show();
            binding.dataFalse.setVisibility(View.GONE);
            playLists.clear();
            rvm.search(keyword);
        });
        binding.oneSongSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.loadMore(keyword, playLists.size()));
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.playList.observe(this, pls -> {
            if (playLists.size() == 0) {
                setRV(pls);
            } else {
                loadMoreArtist(pls);
            }
        });
        rvm.requestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                if (playLists.size() == 0)
                    binding.dataFalse.setVisibility(View.VISIBLE);
            } else {
                binding.dataFalse.setVisibility(View.GONE);
            }
            binding.oneSongSmartRefreshLayout.finishRefresh();
            binding.oneSongSmartRefreshLayout.finishLoadMore();
            binding.Loading.hide();
        });
    }

    private void setRV(List<PlayList> pls) {
        playLists.addAll(pls);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.RV.setLayoutManager(layoutManager);
        adapter = new PlayListAdapter(playLists);
        adapter.setClickCallback(playList -> {
            Intent intent=new Intent(getActivity(), MusicListActivity.class);
            intent.putExtra("type","playlist");
            intent.putExtra("playlist",playList);
            startActivity(intent);
        });
        binding.RV.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMoreArtist(List<PlayList> pls) {
        playLists.addAll(pls);
        adapter.notifyDataSetChanged();
    }
}