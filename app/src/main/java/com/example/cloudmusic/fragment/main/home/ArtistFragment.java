package com.example.cloudmusic.fragment.main.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.ArtistActivity;
import com.example.cloudmusic.adapter.recyclerview.ArtistAllAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentArtistListBinding;
import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.request.activity.RequestArtisViewModel;
import com.example.cloudmusic.request.fragment.main.home.RequestArtistAllFragmentViewModel;
import com.example.cloudmusic.state.activity.StateArtisViewModel;
import com.example.cloudmusic.state.fragment.main.home.StateArtistAllFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.ArtistItemClickCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


public class ArtistFragment extends BaseFragment implements View.OnClickListener {

    FragmentArtistListBinding binding;
    RequestArtistAllFragmentViewModel rvm;
    StateArtistAllFragmentViewModel svm;
    private List<Artist> mArtistList;
    private ArtistAllAdapter adapter;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_list, container, false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateArtistAllFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestArtistAllFragmentViewModel.class);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        mArtistList = new ArrayList<>();
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.artistListLoadFalse.setVisibility(View.GONE);
        binding.artistListLoading.show();
        svm.firstSelect.setValue("全部地区");
        svm.secondSelect.setValue("全部类型");
        setSelectButtonListener();
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.artistListLoadFalse.setVisibility(View.GONE);
            binding.artistListLoading.show();
            mArtistList.clear();
            rvm.getArtistList(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mArtistList.size());
        });
        binding.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.getArtistList(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mArtistList.size()));
    }

    @Override
    protected void getInternetData() {
        rvm.getArtistList(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mArtistList.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.artistListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.artistListLoadFalse.setVisibility(View.VISIBLE);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                binding.artistListLoadFalse.setVisibility(View.GONE);
            }
            binding.artistListLoading.hide();
            binding.smartRefreshLayout.finishRefresh();
            binding.smartRefreshLayout.finishLoadMore();
        });
        rvm.artistList.observe(this, artists -> {
            if (mArtistList.size() == 0)
                setArtistRV(artists);
            else moreArtistLoad(artists);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void moreArtistLoad(List<Artist> artists) {
        mArtistList.addAll(artists);
        adapter.notifyDataSetChanged();
    }

    private void setArtistRV(List<Artist> artists) {
        mArtistList.addAll(artists);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        binding.artistAllRV.setLayoutManager(layoutManager);
        adapter = new ArtistAllAdapter(mArtistList);
        adapter.setClickCallback(artist -> {
            Intent intent = new Intent(getActivity(), ArtistActivity.class);
            intent.putExtra("ar", artist);
            startActivity(intent);
        });
        binding.artistAllRV.setAdapter(adapter);
    }

    private void setSelectButtonListener() {
        binding.sb00.setOnClickListener(this);
        binding.sb01.setOnClickListener(this);
        binding.sb02.setOnClickListener(this);
        binding.sb03.setOnClickListener(this);
        binding.sb04.setOnClickListener(this);
        binding.sb05.setOnClickListener(this);
        binding.sb10.setOnClickListener(this);
        binding.sb11.setOnClickListener(this);
        binding.sb12.setOnClickListener(this);
        binding.sb13.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sb00:
                svm.firstSelect.setValue("全部地区");
                break;
            case R.id.sb01:
                svm.firstSelect.setValue("华语");
                break;
            case R.id.sb02:
                svm.firstSelect.setValue("欧美");
                break;
            case R.id.sb03:
                svm.firstSelect.setValue("日本");
                break;
            case R.id.sb04:
                svm.firstSelect.setValue("韩国");
                break;
            case R.id.sb05:
                svm.firstSelect.setValue("其他");
                break;
            case R.id.sb10:
                svm.secondSelect.setValue("全部类型");
                break;
            case R.id.sb11:
                svm.secondSelect.setValue("男歌手");
                break;
            case R.id.sb12:
                svm.secondSelect.setValue("女歌手");
                break;
            case R.id.sb13:
                svm.secondSelect.setValue("乐队");
                break;
        }
        Log.d("TAG", "歌手分类：" + svm.firstSelect.getValue() + " " + svm.secondSelect.getValue());
        mArtistList.clear();
        binding.artistListLoadFalse.setVisibility(View.GONE);
        binding.artistListLoading.show();
        rvm.getArtistList(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mArtistList.size());
    }

    public class ClickClass {

    }
}