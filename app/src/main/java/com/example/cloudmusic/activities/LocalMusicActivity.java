package com.example.cloudmusic.activities;

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
import com.example.cloudmusic.databinding.ActivityLocalMusicBinding;
import com.example.cloudmusic.databinding.ActivityLoginBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.activity.RequestLikeSongViewModel;
import com.example.cloudmusic.request.activity.RequestLocalMusicViewModel;
import com.example.cloudmusic.state.activity.StateLikeSongViewModel;
import com.example.cloudmusic.state.activity.StateLocalMusicViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.OneSongMoreOperateClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends BaseActivity {

    ActivityLocalMusicBinding binding;
    RequestLocalMusicViewModel rvm;
    StateLocalMusicViewModel svm;
    private List<Song> songList;
    private OneSongMoreOperateDialog dialog;


    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_local_music);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateLocalMusicViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestLocalMusicViewModel.class);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        songList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        binding.localSongNull.setVisibility(View.GONE);
        rvm.getLocalMusic(this);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.songList.observe(this, sl -> {
            if(sl.size()==0){
                binding.localSongNull.setVisibility(View.VISIBLE);
                return;
            }
            binding.localSongNull.setVisibility(View.GONE);
            songList.addAll(sl);
            setLocalSongRV(songList);
        });
        rvm.songMutableLiveData.observe(this, song -> {
            if(!CloudMusic.isStartPlayerActivity){
                CloudMusic.isStartPlayerActivity=true;
                startActivity(new Intent(LocalMusicActivity.this,PlayerActivity.class));
            }
        });
        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(LocalMusicActivity.this, "\n本地音乐\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            }
            if(dialog!=null&&dialog.isShowing())
                dialog.upDateLikeButton();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity=false;
    }

    private void setLocalSongRV(List<Song> songList) {
        binding.localSongRV.setLayoutManager(new LinearLayoutManager(this));
        OneSongAdapter adapter=new OneSongAdapter(songList);
        adapter.setClickCallback(song -> rvm.play(song));
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(LocalMusicActivity.this, song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(LocalMusicActivity.this, "已添加", Toast.LENGTH_SHORT).show();
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
        binding.localSongRV.setAdapter(adapter);
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }
    }
}