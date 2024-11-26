package com.example.cloudmusic.fragment.search.searched;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.LrcSearchedAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentLrcBinding;
import com.example.cloudmusic.entity.Lyrics;
import com.example.cloudmusic.request.fragment.search.searched.RequestLrcFragmentViewModel;
import com.example.cloudmusic.CloudMusic;

import java.util.ArrayList;
import java.util.List;


public class LrcFragment extends BaseFragment {

    FragmentLrcBinding binding;
    RequestLrcFragmentViewModel rvm;
    private final String keyword;
    private List<Lyrics> mLyricsList;
    private LrcSearchedAdapter lrcSearchedAdapter;

    public LrcFragment(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_lrc,container,false);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestLrcFragmentViewModel.class);
        binding.setLifecycleOwner(this);
        mLyricsList=new ArrayList<>();
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
            mLyricsList.clear();
            rvm.search(keyword);
        });
        binding.oneSongSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.loadMore(keyword, mLyricsList.size()));
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.lrcList.observe(this, lyrics -> {
            if (mLyricsList.size() == 0) {
                setRV(lyrics);
            } else {
                loadMoreArtist(lyrics);
            }
        });
        rvm.requestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                if (mLyricsList.size() == 0)
                    binding.dataFalse.setVisibility(View.VISIBLE);
            } else {
                binding.dataFalse.setVisibility(View.GONE);
            }
            binding.oneSongSmartRefreshLayout.finishRefresh();
            binding.oneSongSmartRefreshLayout.finishLoadMore();
            binding.Loading.hide();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMoreArtist(List<Lyrics> lyrics) {
        mLyricsList.addAll(lyrics);
        lrcSearchedAdapter.notifyDataSetChanged();
    }

    private void setRV(List<Lyrics> lyrics) {
        mLyricsList.addAll(lyrics);
        binding.RV.setLayoutManager(new LinearLayoutManager(getContext()));
        lrcSearchedAdapter = new LrcSearchedAdapter(mLyricsList);
        binding.RV.setAdapter(lrcSearchedAdapter);
    }
}