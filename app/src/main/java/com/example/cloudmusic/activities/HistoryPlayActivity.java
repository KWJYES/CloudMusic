package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.searched.OneSongAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityHistoryPlayBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.activity.RequestHistoryPlayViewModel;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;

import java.util.ArrayList;
import java.util.List;

public class HistoryPlayActivity extends BaseActivity {


    ActivityHistoryPlayBinding binding;
    RequestHistoryPlayViewModel rvm;
    private List<Song> mSongList;
    private OneSongAdapter adapter;
    private OneSongMoreOperateDialog dialog;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_play);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestHistoryPlayViewModel.class);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        mSongList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        binding.recentSongNull.setVisibility(View.GONE);
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            binding.recentSongNull.setVisibility(View.GONE);
            rvm.getHistoryPlay();
        });
    }

    @Override
    protected void initSomeData() {
        rvm.getHistoryPlay();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.songList.observe(this, songList -> {
            mSongList.addAll(songList.getSongList());
            if (mSongList.size() == 0) {
                binding.recentSongNull.setVisibility(View.VISIBLE);
            } else {
                binding.recentSongNull.setVisibility(View.GONE);
                setRV(mSongList);
            }
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(HistoryPlayActivity.this, "\n操作失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast toast = Toast.makeText(HistoryPlayActivity.this, "\n操作成功!\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(dialog!=null&&dialog.isShowing())
                dialog.upDateLikeButton();
        });
    }

    private void setRV(List<Song> mSongList) {
        binding.recentSongRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OneSongAdapter(mSongList);
        adapter.setClickCallback(song -> {
            if (!CloudMusic.isStartPlayerActivity) {
                CloudMusic.isStartPlayerActivity = true;
                startActivity(new Intent(HistoryPlayActivity.this, PlayerActivity.class));
            }
        });
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(HistoryPlayActivity.this, song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(HistoryPlayActivity.this, "已添加", Toast.LENGTH_SHORT).show();
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
        binding.recentSongRV.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity = false;
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }
    }
}