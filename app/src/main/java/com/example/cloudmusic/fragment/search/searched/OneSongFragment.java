package com.example.cloudmusic.fragment.search.searched;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.PlayerActivity;
import com.example.cloudmusic.adapter.recyclerview.searched.OneSongAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentOneSongBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.fragment.search.searched.RequestOneSongViewModel;
import com.example.cloudmusic.state.fragment.search.searched.StateOneSongViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.OneSongMoreOperateClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class OneSongFragment extends BaseFragment {

    FragmentOneSongBinding binding;
    StateOneSongViewModel svm;
    RequestOneSongViewModel rvm;
    private OneSongAdapter adapter;
    private final String keywords;
    private final List<Song> songList = new ArrayList<>();
    private boolean isGettingSongUrl = false;//防止操作过快
    private boolean isStartPlayerActivity = false;//防止二次启动PlayerActivity
    private Toast getUrlToast;

    public OneSongFragment(String keywords) {
        this.keywords = keywords;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateOneSongViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestOneSongViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_one_song, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    protected void initView() {
        svm.keywords.setValue(keywords);
        binding.oneSongLoading.show();
        binding.oneSongSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            songList.clear();
            rvm.requestSearchOneSong(svm.keywords.getValue());
        });
        binding.oneSongSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (songList.size() == 0) {
                binding.oneSongSmartRefreshLayout.finishLoadMore();
                return;
            }
            rvm.loadMore(svm.keywords.getValue(), songList.size());
        });
        getUrlToast = Toast.makeText(getContext(), "\n正在获取音乐\n", Toast.LENGTH_SHORT);
        getUrlToast.setGravity(Gravity.CENTER, 0, 0);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.songLD.observe(this, song -> {//获取Url后
            isGettingSongUrl = false;
            if (song.getUrl() == null || song.getUrl().equals("")) {
                Toast toast = Toast.makeText(getContext(), "\n获取音乐失败~\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                isStartPlayerActivity = false;
                return;
            }
            rvm.playSong(song);
            getUrlToast.cancel();
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), PlayerActivity.class));
        });
        rvm.loadMoreRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
            }
            binding.oneSongSmartRefreshLayout.finishLoadMore();
        });
        rvm.oneSongListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(getActivity(), "网络不给力~", Toast.LENGTH_SHORT).show();
            }
            binding.oneSongLoading.hide();
            binding.oneSongSmartRefreshLayout.finishRefresh();
        });

        rvm.oneSongList.observe(this, songs -> {
            svm.oneSongList.setValue(songs);
            setOneSongRV();
        });
        rvm.loadMoreList.observe(this, songs -> {
            for (Song song : songs) {
                songList.add(song);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isStartPlayerActivity=false;
    }

    @Override
    protected void getInternetData() {
        if (svm.oneSongList.getValue() == null || svm.oneSongList.getValue().size() == 0) {
            rvm.requestSearchOneSong(svm.keywords.getValue());
        }
    }

    /**
     * 加载完成，进行展示
     */
    private void setOneSongRV() {
        songList.addAll(Objects.requireNonNull(svm.oneSongList.getValue()));
        adapter = new OneSongAdapter(songList);
        adapter.setMoreOperateClickCallback(song -> {
            OneSongMoreOperateDialog dialog=new OneSongMoreOperateDialog(Objects.requireNonNull(getContext()));
            dialog.show();
        });
        adapter.setClickCallback(song -> {
            getUrlToast.show();
            isGettingSongUrl = true;
            if (isStartPlayerActivity) return;
            isStartPlayerActivity = true;
            rvm.getSongUrl(song);
        });
        binding.oneSongRV.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.oneSongRV.setAdapter(adapter);
    }
}