package com.example.cloudmusic.fragment.play;

import android.os.Bundle;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentLyricBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongLrc;
import com.example.cloudmusic.request.fragment.play.RequestLyricFragmentViewModel;
import com.example.cloudmusic.request.fragment.play.RequestSongFragmentViewModel;
import com.example.cloudmusic.state.fragment.play.StateLyricFragmentViewModel;
import com.example.cloudmusic.state.fragment.play.StateSongFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.FragmentMsgCallback;
import com.example.cloudmusic.utils.callback.LrcClickCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;


public class LyricFragment extends BaseFragment {

    FragmentLyricBinding binding;
    RequestLyricFragmentViewModel rvm;
    StateLyricFragmentViewModel svm;
    private FragmentMsgCallback fragmentMsgCallback;

    public LyricFragment(FragmentMsgCallback fragmentMsgCallback) {
        this.fragmentMsgCallback = fragmentMsgCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lyric, container, false);
        svm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(StateLyricFragmentViewModel.class);
        rvm = new ViewModelProvider(Objects.requireNonNull(getActivity()), new ViewModelProvider.NewInstanceFactory()).get(RequestLyricFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        svm.lrc.setValue("[00:00.170] \n[00:00.260] \n[00:00.360] \n[00:00.460]暂无歌词，请您欣赏纯音乐\n");
        binding.textView17.setSelected(true);
        initLrcView();
    }

    @Override
    protected void initSomeData() {
        rvm.getCurrentSong();
        rvm.updatePlayModeBtn();
        EventBus.getDefault().register(this);
    }

    private void initLrcView() {
        binding.myLrcView.setLrcClickCallback(time -> {
            binding.myLrcView.updateCurrentLrc(time);
            rvm.seekTo(time);
        });
        binding.myLrcView.setLrc(svm.lrc.getValue());
    }

    public class ClickClass {
        public void play(View view) {
            rvm.startPause();
        }
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.isPlaying.observe(this, isPlaying -> {
            if (isPlaying) {
                binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
            } else {
                binding.play.setBackgroundResource(R.drawable.btn_play_selector);
            }
            MyEvent myEvent = new MyEvent();
            myEvent.setPlaying(isPlaying);
            fragmentMsgCallback.transferData(myEvent, 1);
        });
        rvm.song.observe(this, song -> {
            svm.song.setValue(song);
            if (song.getName().equals("暂无播放")) {
                svm.lrc.setValue("[00:00.170] \n[00:00.260] \n[00:00.360] \n[00:00.460]暂无歌词，请您欣赏纯音乐\n");
                binding.myLrcView.setLrc(svm.lrc.getValue());
                Glide.with(Objects.requireNonNull(getContext())).load(R.drawable.pic_cd).into(binding.lrcSongPg);
                binding.textView17.setText((song.getName()+" - "+song.getArtist()));
            } else{
                Glide.with(Objects.requireNonNull(getContext())).load(song.getPicUrl()).into(binding.lrcSongPg);
                binding.textView17.setText((song.getName()+" - "+song.getArtist()));
                rvm.getSongLrc(song);
            }
        });
        rvm.songLrc.observe(this, songLrc -> {
            if(songLrc == null||songLrc.getLrc()==null||songLrc.getLrc().equals("")||!songLrc.getLrc().startsWith("[00"))return;
            svm.lrc.setValue(songLrc.getLrc());
            rvm.getInitCurrentTime();
            binding.myLrcView.setLrc(svm.lrc.getValue());
        });
        rvm.currentPositionLD.observe(this, integer -> binding.myLrcView.updateCurrentLrc(integer));
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
            case 0://准备播放完成
                int duration = myEvent.getDuration();
                break;
            case 1://当前播放进度
                int currentPosition = myEvent.getCurrentPosition();
                if (!Objects.equals(svm.lrc.getValue(), "[00:00.170] \n[00:00.260] \n[00:00.360] \n[00:00.460]暂无歌词，请您欣赏纯音乐\n"))
                    binding.myLrcView.updateCurrentLrc(currentPosition + CloudMusic.lrcOffset);
                break;
            case 2://播放结束
                binding.myLrcView.setLrc("[00:00.170] \n[00:00.260] \n[00:00.360] \n[00:00.460]暂无歌词，请您欣赏纯音乐\n");
                break;
            case 4://play
                Song song = myEvent.getSong();
                svm.song.setValue(song);
                Glide.with(Objects.requireNonNull(getContext())).load(song.getPicUrl()).into(binding.lrcSongPg);
                binding.textView17.setText((song.getName()+" - "+song.getArtist()));
                rvm.getSongLrc(song);
                break;
        }
    }

    public void setPlayBtn(boolean isPlaying, boolean isRemoveSong) {
        if (binding == null) return;
        if (isRemoveSong) {
            binding.myLrcView.setLrc("[00:00.170] \n[00:00.260] \n[00:00.360] \n[00:00.460]暂无歌词，请您欣赏纯音乐\n");
        }
        if (isPlaying) {
            binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
        } else {
            binding.play.setBackgroundResource(R.drawable.btn_play_selector);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}