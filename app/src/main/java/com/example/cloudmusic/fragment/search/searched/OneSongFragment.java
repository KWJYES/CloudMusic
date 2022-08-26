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
import com.example.cloudmusic.utils.callback.OneSongMoreDialogClickCallback;
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

    private Toast getUrlToast;
    private OneSongMoreOperateDialog dialog;

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


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initView() {
        binding.oneSongLoading.show();
        binding.dataFalse.setVisibility(View.GONE);
        binding.oneSongSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            songList.clear();
            adapter.notifyDataSetChanged();
            binding.oneSongLoading.show();
            binding.dataFalse.setVisibility(View.GONE);
            rvm.requestSearchOneSong(keywords);
        });
        binding.oneSongSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (songList.size() == 0) {
                binding.oneSongSmartRefreshLayout.finishLoadMore();
                return;
            }
            rvm.loadMore(keywords, songList.size());
        });
        getUrlToast = Toast.makeText(getContext(), "\n正在获取音乐\n", Toast.LENGTH_SHORT);
        getUrlToast.setGravity(Gravity.CENTER, 0, 0);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.songLD.observe(this, song -> {
            if (song.getUrl() == null || song.getUrl().equals("")) {
                Toast toast = Toast.makeText(getContext(), "\n获取音乐失败~\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                CloudMusic.isStartPlayerActivity = false;
                return;
            }
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
                if (songList.size() == 0)
                    binding.dataFalse.setVisibility(View.VISIBLE);
            } else {
                binding.dataFalse.setVisibility(View.GONE);
            }
            binding.oneSongLoading.hide();
            binding.oneSongSmartRefreshLayout.finishRefresh();
        });

        rvm.oneSongList.observe(this, songs -> {
            songList.addAll(songs);
            setOneSongRV();
        });

        rvm.loadMoreList.observe(this, songs -> {
            for (Song song : songs) {
                songList.add(song);
                adapter.notifyDataSetChanged();
            }
        });

        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(getContext(), "\n操作失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast toast = Toast.makeText(getContext(), "\n操作成功!\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (dialog != null && dialog.isShowing())
                dialog.upDateLikeButton();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity = false;
    }

    @Override
    protected void getInternetData() {
        if (songList.size() == 0) {
            rvm.requestSearchOneSong(keywords);
        }
    }

    /**
     * 加载完成，进行展示
     */
    private void setOneSongRV() {
        adapter = new OneSongAdapter(songList);
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(Objects.requireNonNull(getContext()), song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(getContext(), "已添加", Toast.LENGTH_SHORT).show();
            }, song1 -> {
                boolean isLike = true;
                for (String id : CloudMusic.likeSongIdSet) {
                    if (id.equals(song1.getSongId())) {
                        isLike = false;
                        break;
                    }
                }
                rvm.like(isLike, song1.getSongId());
            });
            dialog.show();
        });
        adapter.setClickCallback(song -> {
            getUrlToast.show();
            if (CloudMusic.isStartPlayerActivity) return;
            CloudMusic.isStartPlayerActivity = true;
            rvm.playSong(song);
        });
        binding.oneSongRV.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.oneSongRV.setAdapter(adapter);
    }
}