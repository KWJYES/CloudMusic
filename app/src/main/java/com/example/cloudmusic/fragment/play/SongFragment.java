package com.example.cloudmusic.fragment.play;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.utils.callback.FragmentMsgCallback;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.databinding.FragmentSongBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.request.fragment.play.RequestSongFragmentViewModel;
import com.example.cloudmusic.state.fragment.play.StateSongFragmentViewModel;
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
    private FragmentMsgCallback fragmentMsgCallback;
    private Toast likingToast;

    public SongFragment(FragmentMsgCallback fragmentMsgCallback) {
        this.fragmentMsgCallback = fragmentMsgCallback;
    }

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
        likingToast = Toast.makeText(getContext(), "\n正在拼命响应中\n", Toast.LENGTH_SHORT);
        likingToast.setGravity(Gravity.CENTER, 0, 0);
        binding.songTittle.setSelected(true);
        binding.songAr.setSelected(true);
        svm.currentPosition.setValue("00:00");
        svm.duration.setValue("00:00");
        svm.songAr.setValue("歌手未知");
        svm.songName.setValue("暂无播放");
        svm.songLyc.setValue("暂无歌词，请您欣赏纯音乐");
        svm.songId.setValue("0");
        initSeekBar();
    }

    @Override
    protected void initSomeData() {
        Log.d("TAG", "SongFragment initSomeData()...");
        rvm.updatePlayBtn();
        CloudMusic.isSongFragmentEventBusRegister =true;
        EventBus.getDefault().register(this);
        rvm.getInitDuration();
        rvm.getInitCurrentTime();
        rvm.getCurrentSong();
        rvm.updatePlayModeBtn();
        upDateLikeButton();
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
        rvm.playMode.observe(this, playMode -> {
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
        });
        rvm.isPlaying.observe(this, isPlaying -> {
            if (isPlaying) {
                binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
            } else {
                binding.play.setBackgroundResource(R.drawable.btn_play_selector);
            }
            MyEvent myEvent = new MyEvent();
            myEvent.setPlaying(isPlaying);
            myEvent.setRemoveSong(false);
            fragmentMsgCallback.transferData(myEvent, 0);
        });
        rvm.song.observe(this, song -> {
            svm.songId.setValue(song.getSongId());
            svm.song.setValue(song);
            svm.songName.setValue(song.getName());
            svm.songAr.setValue(song.getArtist());
            Glide.with(Objects.requireNonNull(getActivity())).load(song.getPicUrl()).placeholder(R.drawable.pic_cd).centerCrop().into(binding.icCd);
            MyEvent myEvent = new MyEvent();
            myEvent.setMsg(5);
            myEvent.setSong(song);
            EventBus.getDefault().post(myEvent);
            if (song.getName().startsWith("暂无播放")) {
                svm.currentPosition.setValue("00:00");
                svm.duration.setValue("00:00");
                Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.pic_cd).placeholder(R.drawable.pic_cd).centerCrop().into(binding.icCd);
                MyEvent myEvent2 = new MyEvent();
                myEvent2.setPlaying(false);
                myEvent2.setRemoveSong(true);
                fragmentMsgCallback.transferData(myEvent2, 0);
            }
            upDateLikeButton();
        });
        rvm.duration.observe(this, s -> {
            if (!Objects.equals(svm.duration.getValue(), s))
                svm.duration.setValue(s);
        });
        rvm.currentPosition.observe(this, s -> svm.currentPosition.setValue(s));
        rvm.durationLD.observe(this, duration -> {
            binding.seekBar.setMax(duration);
            rvm.formatDuration(duration);
        });
        rvm.currentPositionLD.observe(this, currentPosition -> {
            binding.seekBar.setProgress(currentPosition);
            rvm.formatCurrentTime(currentPosition);
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
            upDateLikeButton();
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // 旋转动画
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.ic_cd_rote);
        binding.icCd.startAnimation(rotate);
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
                binding.seekBar.setMax(duration);
                rvm.formatDuration(duration);
                rvm.saveDuration(myEvent.getDuration());
                break;
            case 1://当前播放进度
                int currentPosition = myEvent.getCurrentPosition();
                if (Objects.requireNonNull(svm.songName.getValue()).startsWith("暂无播放")) break;
                rvm.formatDuration(myEvent.getDuration());
                binding.seekBar.setProgress(currentPosition);
                rvm.formatCurrentTime(currentPosition);
                rvm.saveCurrentTime(myEvent.getCurrentPosition());
                break;
            case 2://播放结束
                rvm.updatePlayBtn();
                rvm.nextSong();
                break;
            case 3://开始播放
                rvm.updatePlayBtn();
                if (myEvent.getSong() != null)
                    rvm.song.setValue(myEvent.getSong());
                Log.d("TAG","开始播放SongFragment");
                break;
            case 10://通知栏改变播放状态
                rvm.isPlaying.setValue(myEvent.isPlaying());
        }
    }

    public void transferData(boolean playing) {
        if (binding == null) return;
        if (playing) {
            binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
        } else {
            binding.play.setBackgroundResource(R.drawable.btn_play_selector);
        }
    }


    public void upDateLikeButton() {
        for (String id : CloudMusic.likeSongIdSet) {
            if (id.equals(svm.songId.getValue())) {
                binding.likeBtn.setLike(true);
                return;
            }
        }
        binding.likeBtn.setLike(false);
    }

    public class ClickClass {
        /**
         * 播放与暂停
         *
         * @param view
         */
        public void play(View view) {
            if (Objects.requireNonNull(svm.songName.getValue()).startsWith("暂无播放")) return;
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
            if (CloudMusic.isStartMusicListDialog) return;
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

        public void likeSong(View view) {
            if(Objects.requireNonNull(svm.songId.getValue()).startsWith("000")){
                Toast toast = Toast.makeText(getContext(), "\n本地音乐!\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if (CloudMusic.isLiking) {
                likingToast.show();
                return;
            }
            if (Objects.equals(svm.songId.getValue(), "0")) return;
            binding.likeBtn.setLike(!binding.likeBtn.isLike());
            rvm.like(binding.likeBtn.isLike(), svm.songId.getValue());
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        CloudMusic.isSongFragmentEventBusRegister =false;
        Log.d("TAG","--SongFragment onDestroy--");
        super.onDestroy();
    }
}