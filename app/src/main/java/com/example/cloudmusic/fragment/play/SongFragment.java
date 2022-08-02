package com.example.cloudmusic.fragment.play;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.databinding.FragmentSongBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.RequestSongFragmentViewModel;
import com.example.cloudmusic.state.StateSongFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.views.MusicListDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;


public class SongFragment extends BaseFragment {

    FragmentSongBinding binding;
    StateSongFragmentViewModel svm;
    RequestSongFragmentViewModel rvm;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_song, container, false);
        svm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(StateSongFragmentViewModel.class);
        rvm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(RequestSongFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.songTittle.setSelected(true);
        binding.songAr.setSelected(true);
        svm.currentPosition.setValue("00:00");
        svm.duration.setValue("00:00");
        svm.songAr.setValue("歌手未知");
        svm.songName.setValue("暂无播放");
        svm.songLyc.setValue("（暂无歌词，请您欣赏音乐）");
        initSeekBar();
    }

    @Override
    protected void initSomeData() {
        Log.d("TAG", "SongFragment initSomeData()...");
        rvm.updatePlayBtn();
        EventBus.getDefault().register(this);
        rvm.getInitDuration();
        rvm.getInitCurrentTime();
        rvm.getCurrentSong();
        rvm.updatePlayModeBtn();
    }

    private void initSeekBar() {
        binding.seekBar.setMax(100);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int position = seekBar.getProgress();
                rvm.seekTo(position);
            }
        });
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.playMode.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer playMode) {
                switch (playMode) {
                    case CloudMusic.Loop:
                        binding.playModel.setBackgroundResource(R.drawable.ic_loop2);
                        break;
                    case CloudMusic.Single_Loop:
                        binding.playModel.setBackgroundResource(R.drawable.ic_single_loop);
                        break;
                    case CloudMusic.Random:
                        binding.playModel.setBackgroundResource(R.drawable.ic_random);
                        break;
                }
            }
        });
        rvm.isPlaying.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {
                if (isPlaying) {
                    binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
                } else {
                    binding.play.setBackgroundResource(R.drawable.btn_play_selector);
                }
                Log.d("TAG", "isPlaying--->" + isPlaying);
            }
        });
        rvm.song.observe(this, new Observer<Song>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Song song) {
                svm.songName.setValue(song.getName());
                svm.songAr.setValue(song.getArtist());
                if(song.getName().startsWith("暂无播放")){
                    svm.currentPosition.setValue("00:00");
                    svm.duration.setValue("00:00");
                }
            }
        });
        rvm.duration.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                svm.duration.setValue(s);
            }
        });
        rvm.currentPosition.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                svm.currentPosition.setValue(s);
            }
        });
        rvm.durationLD.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer duration) {
                binding.seekBar.setMax(duration);
                rvm.formatDuration(duration);
            }
        });
        rvm.currentPositionLD.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentPosition) {
                binding.seekBar.setProgress(currentPosition);
                rvm.formatCurrentTime(currentPosition);
            }
        });
    }


    /**
     * 接收EventBus数据
     *
     * @param myEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyEvent myEvent) {
        int msg = myEvent.getMsg();
        switch (msg) {
            case 0://准备播放
                Log.d("TAG", "EventBus 准备播放...");
                int duration = myEvent.getDuration();
                binding.seekBar.setMax(duration);
                rvm.formatDuration(duration);
                rvm.saveDuration(myEvent.getDuration());
                break;
            case 1://当前播放进度
                int currentPosition = myEvent.getCurrentPosition();
                Log.d("currentPosition", "" + currentPosition);
                Log.d("seekBar","Progress-->"+binding.seekBar.getProgress());
                Log.d("seekBar","Max-->"+binding.seekBar.getMax());
                if(Objects.requireNonNull(svm.songName.getValue()).startsWith("暂无播放")) break;
                binding.seekBar.setProgress(currentPosition);
                rvm.formatCurrentTime(currentPosition);
                rvm.saveCurrentTime(myEvent.getCurrentPosition());
                break;
            case 2://播放完成
                Log.d("TAG", "SongFragment 播放完成...");
                rvm.updatePlayBtn();
                rvm.nextSong();
                break;
        }
    }

    public class ClickClass {
        /**
         * 播放与暂停
         *
         * @param view
         */
        public void play(View view) {
            if(Objects.requireNonNull(svm.songName.getValue()).startsWith("暂无播放")) return;
            rvm.startPause();
        }

        /**
         * 下一曲
         *
         * @param view
         */
        public void nextSong(View view) {
            rvm.nextSong();
        }

        /**
         * 上一曲
         *
         * @param view
         */
        public void lastSong(View view) {
            rvm.lastSong();
        }

        /**
         * 播放列表
         *
         * @param view
         */
        public void list(View view) {
            SongListItemOnClickCallback songListItemOnClickCallback = song -> rvm.play(song);
            SongListItemRemoveCallback removeCallback = song -> rvm.remove(song);
            MusicListDialog dialog = new MusicListDialog(Objects.requireNonNull(getActivity()), R.style.Base_ThemeOverlay_AppCompat_Dialog, songListItemOnClickCallback, removeCallback);
            dialog.show();
        }

        /**
         * 切换播放模式
         * 列表循环
         * 随机播放
         * 单曲循环
         *
         * @param view
         */
        public void playModel(View view) {
            rvm.playModel();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}