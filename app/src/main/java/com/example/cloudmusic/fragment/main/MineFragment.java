package com.example.cloudmusic.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.HistoryPlayActivity;
import com.example.cloudmusic.activities.LikeArtistActivity;
import com.example.cloudmusic.activities.LikeSongActivity;
import com.example.cloudmusic.activities.LocalMusicActivity;
import com.example.cloudmusic.activities.LoginActivity;
import com.example.cloudmusic.adapter.recyclerview.NewSongRecommendAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentMineBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.fragment.main.mine.RequestMineFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.mine.StateMineFragmentViewModel;
import com.example.cloudmusic.utils.ActivityCollector;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.LoginOutDialogClickCallback;
import com.example.cloudmusic.views.LoginOutDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MineFragment extends BaseFragment {

    FragmentMineBinding binding;
    StateMineFragmentViewModel svm;
    RequestMineFragmentViewModel rvm;
    private LoginOutDialog dialog;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMineFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMineFragmentViewModel.class);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.myL3.signInProgressBar.setVisibility(View.GONE);
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            rvm.getRecentSongs();
        });
        svm.userName.setValue(CloudMusic.nickname);
        svm.userLevel.setValue(CloudMusic.level);
        if (CloudMusic.avatarUrl != null) {
            svm.userHearUrl.setValue(CloudMusic.avatarUrl);
            Glide.with(this).load(CloudMusic.avatarUrl).transform(new CenterCrop()).placeholder(R.drawable.ic_head).into(binding.myL1.circleImageView);
        }
    }

    private void updateRecentRV(List<Song> songs) {
        List<Song> songList = new ArrayList<>();
        for (int i = songs.size() - 1; i >= 0; i--) {
            Song song = songs.get(i);
            if (song.getAlias() == null || song.getAlias().equals("01") || song.getAlias().equals(""))
                song.setAlias("最近听过");
            songList.add(song);//倒序
            if (songList.size() >= 5) break;
        }
        binding.myL3.recentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        NewSongRecommendAdapter adapter = new NewSongRecommendAdapter(songList);
        adapter.setClickCallback(song -> rvm.playRecentSong(song));
        binding.myL3.recentRv.setAdapter(adapter);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.songs.observe(this, songList -> {
            updateRecentRV(songList.getSongList());
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.recentSong.observe(this, song -> {
            MyEvent myEvent = new MyEvent();
            myEvent.setMsg(6);
            myEvent.setSong(song);
            EventBus.getDefault().post(myEvent);
        });
        rvm.signInState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(getContext(), "\n签到失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast falseToast = Toast.makeText(getContext(), "\n签到成功!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            }
            binding.myL3.signInProgressBar.setVisibility(View.GONE);
        });
        rvm.loginOutState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast toast = Toast.makeText(getContext(), "\n网络异常\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_START);
                startActivity(intent);
                if (dialog.isShowing())
                    dialog.cancel();
                ActivityCollector.loginOutFinish();
            }
        });
    }

    @Override
    protected void initSomeData() {
        rvm.getRecentSongs();
    }

    public class ClickClass {

        public void toLogin(View view) {
            if (CloudMusic.userId.equals("0")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_INSIDE);
                startActivity(intent);
            }
        }

        public void toLikeList(View view) {
            if (CloudMusic.userId.equals("0")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_INSIDE);
                startActivity(intent);
                return;
            }
            startActivity(new Intent(getActivity(), LikeSongActivity.class));
        }

        /**
         * 最近播放
         *
         * @param view
         */
        public void recentPlay(View view) {
            startActivity(new Intent(getActivity(), HistoryPlayActivity.class));
        }

        public void myArtistList(View view) {
            if (CloudMusic.userId.equals("0")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_INSIDE);
                startActivity(intent);
                return;
            }
            startActivity(new Intent(getActivity(), LikeArtistActivity.class));
        }

        public void localMusic(View view) {
            startActivity(new Intent(getActivity(), LocalMusicActivity.class));
        }

        /**
         * 今日签到
         *
         * @param view
         */
        public void todaySign(View view) {
            if (CloudMusic.userId.equals("0")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_INSIDE);
                startActivity(intent);
                return;
            }
            binding.myL3.signInProgressBar.setVisibility(View.VISIBLE);
            rvm.signIn();
        }

        public void loginOut(View view) {
            dialog = new LoginOutDialog(Objects.requireNonNull(getContext()), ActivityCollector::finishAll, () -> rvm.loginOut());
            dialog.show();
        }
    }
}