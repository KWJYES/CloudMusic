package com.example.cloudmusic.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.searched.OneSongAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityLikeSongBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.activity.RequestArtisViewModel;
import com.example.cloudmusic.request.activity.RequestLikeSongViewModel;
import com.example.cloudmusic.state.activity.StateArtisViewModel;
import com.example.cloudmusic.state.activity.StateLikeSongViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class LikeSongActivity extends BaseActivity {

    ActivityLikeSongBinding binding;
    StateLikeSongViewModel svm;
    RequestLikeSongViewModel rvm;
    private List<Song> likeList;
    private OneSongAdapter adapter;
    private OneSongMoreOperateDialog dialog;


    @Override
    protected void initActivity() {
        setTransparentStatusBar(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like_song);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateLikeSongViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestLikeSongViewModel.class);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
    }


    @Override
    protected void getInternetData() {
        getLikeList();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.likeIdListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(LikeSongActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
                if (CloudMusic.likeSongIdSet.size() == 0)
                    binding.likeSongNull.setVisibility(View.VISIBLE);
                binding.smartRefreshLayout.finishRefresh();
                binding.likeListLoading.hide();
            }
        });
        rvm.likeIdList.observe(this, strings -> {
            CloudMusic.likeSongIdSet.addAll(strings);
            getLikeList();
        });
        rvm.songList.observe(this, songList -> {
            likeList.addAll(songList);
            setLikeListRV(likeList);
        });
        rvm.songDetailRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                if (likeList.size() == 0) binding.likeSongNull.setVisibility(View.VISIBLE);
                Toast.makeText(LikeSongActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
            }
            binding.smartRefreshLayout.finishRefresh();
            binding.likeListLoading.hide();
        });
        rvm.song.observe(this, song -> {
            if (!CloudMusic.isStartPlayerActivity) {
                CloudMusic.isStartPlayerActivity = true;
                startActivity(new Intent(LikeSongActivity.this, PlayerActivity.class));
            }
        });
        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(LikeSongActivity.this, "\n操作失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast toast = Toast.makeText(LikeSongActivity.this, "\n操作成功!\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(dialog!=null&&dialog.isShowing())
                dialog.upDateLikeButton();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity = false;
    }

    private void setLikeListRV(List<Song> likeList) {
        binding.likeSongRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OneSongAdapter(likeList);
        adapter.setClickCallback(song -> {
            if (!CloudMusic.isGettingSongUrl)
                rvm.play(song);
        });
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(LikeSongActivity.this, song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(LikeSongActivity.this, "已添加", Toast.LENGTH_SHORT).show();
            }, song1 -> {
                boolean isLike = true;
                for (String id : CloudMusic.likeSongIdSet) {
                    if (id.equals(song1.getSongId())) {
                        isLike=false;
                        break;
                    }
                }
                rvm.like(isLike, song1.getSongId());
            });
            dialog.show();
        });
        binding.likeSongRV.setAdapter(adapter);
    }

    private void getLikeList() {
        if (CloudMusic.likeSongIdSet.size() == 0) {
            binding.likeSongNull.setVisibility(View.VISIBLE);
            binding.smartRefreshLayout.finishRefresh();
            binding.likeListLoading.hide();
            return;
        }
        likeList.clear();
        rvm.getLikeSong(new ArrayList<>(CloudMusic.likeSongIdSet));
    }

    @Override
    protected void initView() {
        likeList = new ArrayList<>();
        binding.likeSongNull.setVisibility(View.GONE);
        binding.likeListLoading.show();
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.likeSongNull.setVisibility(View.GONE);
            binding.likeListLoading.show();
            rvm.getLikeIdList(CloudMusic.userId);
        });
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }
    }
}