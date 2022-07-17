package com.example.cloudmusic.fragment.play;

import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.PlayerActivity;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentSongBinding;
import com.example.cloudmusic.request.RequestSongFragmentViewModel;
import com.example.cloudmusic.state.StateSongFragmentViewModel;
import com.example.cloudmusic.views.MusicListDialog;

import java.util.Objects;




public class SongFragment extends BaseFragment {

    FragmentSongBinding binding;
    StateSongFragmentViewModel svm;
    RequestSongFragmentViewModel rvm;


    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_song,container,false);
        svm=new ViewModelProvider(Objects.requireNonNull(getActivity()),new ViewModelProvider.NewInstanceFactory()).get(StateSongFragmentViewModel.class);
        rvm=new ViewModelProvider(Objects.requireNonNull(getActivity()),new ViewModelProvider.NewInstanceFactory()).get(RequestSongFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.songTittle.setSelected(true);
        binding.songAr.setSelected(true);
        svm.currentTime.setValue("00:00");
        svm.time.setValue("00:00");
        svm.songAr.setValue("歌手：未知");
        svm.songName.setValue("歌曲：未知");
        svm.songLyc.setValue("（暂无歌词，请您欣赏音乐）");

    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.isPlay.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPlaying) {
                if (isPlaying) {
                    binding.play.setBackgroundResource(R.drawable.btn_play_selector);
                } else {
                    binding.play.setBackgroundResource(R.drawable.btn_puase_selector);
                }
            }
        });
    }


    public class ClickClass {
        /**
         * 播放与暂停
         * @param view
         */
        public void play(View view) {

        }

        /**
         * 下一曲
         * @param view
         */
        public void nextSong(View view){

        }

        /**
         * 上一曲
         * @param view
         */
        public void lastSong(View view){

        }

        /**
         * 播放列表
         * @param view
         */
        public void list(View view){
            MusicListDialog dialog=new MusicListDialog(Objects.requireNonNull(getActivity()),R.style.Base_ThemeOverlay_AppCompat_Dialog);
            dialog.show();
        }

        /**
         * 切换播放模式
         *  列表循环
         *  随机播放
         *  单曲循环
         * @param view
         */
        public void playModel(View view){

        }
    }
}