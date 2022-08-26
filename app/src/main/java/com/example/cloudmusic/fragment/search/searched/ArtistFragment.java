package com.example.cloudmusic.fragment.search.searched;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.ArtistActivity;
import com.example.cloudmusic.adapter.recyclerview.ArtistAllAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentArtistBinding;
import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.request.fragment.search.searched.RequestArtistFragmentViewModel;
import com.example.cloudmusic.request.fragment.search.searched.RequestOneSongViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.ArtistItemClickCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends BaseFragment {
    FragmentArtistBinding binding;
    RequestArtistFragmentViewModel rvm;

    private String keyword;
    private List<Artist> mArtistList;
    private ArtistAllAdapter adapter;

    public ArtistFragment(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist, container, false);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestArtistFragmentViewModel.class);
        binding.setLifecycleOwner(this);
        mArtistList = new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    protected void getInternetData() {
        rvm.searchArtist(keyword);
    }

    @Override
    protected void initView() {
        binding.Loading.show();
        binding.dataFalse.setVisibility(View.GONE);
        binding.oneSongSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.Loading.show();
            binding.dataFalse.setVisibility(View.GONE);
            mArtistList.clear();
            rvm.searchArtist(keyword);
        });
        binding.oneSongSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.loadMoreArtist(keyword, mArtistList.size()));
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.artistList.observe(this, artists -> {
            if (mArtistList.size() == 0) {
                setRV(artists);
            } else {
                loadMoreArtist(artists);
            }
        });
        rvm.requestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                if (mArtistList.size() == 0)
                    binding.dataFalse.setVisibility(View.VISIBLE);
            } else {
                binding.dataFalse.setVisibility(View.GONE);
            }
            binding.oneSongSmartRefreshLayout.finishRefresh();
            binding.oneSongSmartRefreshLayout.finishLoadMore();
            binding.Loading.hide();
        });
    }

    private void setRV(List<Artist> artists) {
        mArtistList.addAll(artists);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        binding.RV.setLayoutManager(layoutManager);
        adapter = new ArtistAllAdapter(mArtistList);
        adapter.setClickCallback(artist -> {
            Intent intent = new Intent(getActivity(), ArtistActivity.class);
            intent.putExtra("ar", artist);
            startActivity(intent);
        });
        binding.RV.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMoreArtist(List<Artist> artists) {
        mArtistList.addAll(artists);
        adapter.notifyDataSetChanged();
    }
}