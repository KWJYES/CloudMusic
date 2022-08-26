package com.example.cloudmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.searched.OneSongAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityMusicListBinding;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.activity.RequestMusicListViewModel;
import com.example.cloudmusic.request.activity.RequestPlayViewModel;
import com.example.cloudmusic.state.activity.StateMusicListViewModel;
import com.example.cloudmusic.state.activity.StatePlayerViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicListActivity extends BaseActivity {

    ActivityMusicListBinding binding;
    private PlayList playList;
    private List<Song> songList;
    StateMusicListViewModel svm;
    RequestMusicListViewModel rvm;
    private Toast getUrlToast;
    private OneSongAdapter adapter;
    private OneSongMoreOperateDialog dialog;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_list);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMusicListViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMusicListViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        initPlayList();
        songList = new ArrayList<>();
    }

    private void initPlayList() {
        playList = new PlayList();
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        playList.setDescription("暂无描述");
        if (type.equals("musicList")) {
            MusicList musicList = (MusicList) intent.getSerializableExtra("playlist");
            playList.setId(musicList.getId() + "");
            playList.setCoverImgUrl(musicList.getPicUrl());
            playList.setName(musicList.getName());
            rvm.getMusicListDec(playList.getId());
        } else {
            PlayList playList1 = (PlayList) intent.getSerializableExtra("playlist");
            playList.setId(playList1.getId());
            playList.setCoverImgUrl(playList1.getCoverImgUrl());
            playList.setName(playList1.getName());
            if (playList1.getDescription() != null && !playList1.getDescription().equals(""))
                playList.setDescription(playList1.getDescription());
            rvm.getSongs(playList.getId());
        }


    }

    @Override
    protected void initView() {
        binding.musilistDecTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.playlistLoadFalse.setVisibility(View.GONE);
        binding.playlistLoading.show();
        svm.dec.setValue(playList.getDescription());
        svm.name.setValue(playList.getName());
        Glide.with(MusicListActivity.this)
                .load(playList.getCoverImgUrl())
                // 设置高斯模糊
                // "25"	模糊度，取值范围1~25
                // "3"	图片缩放3倍后再进行模糊，一般缩放3-5倍
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new CenterCrop(), new BlurTransformation(25, 3))
                // 将得到的模糊图像替换原来的背景
                .into(binding.musiclistActivityBg);
        Glide.with(MusicListActivity.this).load(playList.getCoverImgUrl()).transform(new CenterCrop(), new RoundedCorners(10)).diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.imageView8);
        getUrlToast = Toast.makeText(MusicListActivity.this, "\n正在获取音乐\n", Toast.LENGTH_SHORT);
        getUrlToast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.dec.observe(this, s -> {
            svm.dec.setValue(s);
            rvm.getSongs(playList.getId());
        });
        rvm.songListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.playlistLoadFalse.setVisibility(View.VISIBLE);
            }
            binding.playlistLoading.hide();
        });
        rvm.songList.observe(this, this::setSongListRV);
        rvm.song.observe(this, song -> {
            getUrlToast.cancel();
            if (CloudMusic.isStartPlayerActivity) return;
            CloudMusic.isStartPlayerActivity = true;
            startActivity(new Intent(MusicListActivity.this, PlayerActivity.class));
        });
        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(MusicListActivity.this, "\n操作失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast toast = Toast.makeText(MusicListActivity.this, "\n操作成功!\n", Toast.LENGTH_SHORT);
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

    private void setSongListRV(List<Song> sl) {
        songList.clear();
        songList.addAll(sl);
        binding.musiclistRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OneSongAdapter(songList);
        adapter.setClickCallback(song -> {
            if (CloudMusic.isGettingSongUrl) return;
            getUrlToast.show();
            rvm.playSong(song);
        });
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(MusicListActivity.this, song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(MusicListActivity.this, "已添加", Toast.LENGTH_SHORT).show();
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
        binding.musiclistRv.setAdapter(adapter);
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }

        public void refresh(View view) {
            binding.playlistLoadFalse.setVisibility(View.GONE);
            binding.playlistLoading.show();
            rvm.getMusicListDec(playList.getId());
        }
    }
}