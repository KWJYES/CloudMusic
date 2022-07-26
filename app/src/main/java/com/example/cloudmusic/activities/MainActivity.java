package com.example.cloudmusic.activities;

import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.MainViewPager2Adapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.callback.MediaPlayerViewOnClickCallback;
import com.example.cloudmusic.callback.MusicListOnClickCallback;
import com.example.cloudmusic.callback.PlayOnClickCallback;
import com.example.cloudmusic.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.databinding.ActivityMainBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.fragment.main.DiscussionFragment;
import com.example.cloudmusic.fragment.main.HomeFragment;
import com.example.cloudmusic.fragment.main.MineFragment;
import com.example.cloudmusic.request.RequestMainViewModel;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.sevices.PlayerService;
import com.example.cloudmusic.state.StateMainViewModel;
import com.example.cloudmusic.views.MusicListDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private StateMainViewModel svm;
    private RequestMainViewModel rvm;
    @SuppressLint("StaticFieldLeak")
    public static Context mainActivityContext;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaManager.getInstance().setPlayerBinder((PlayerService.PlayerBinder) iBinder);
            Log.d("TAG", "PlayerService 服务绑定成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "PlayerService 服务解绑");
        }
    };
    private boolean isBind;

    @Override
    protected void initActivity() {
        LitePal.getDatabase();//初始化数据库
        mainActivityContext = this;
        setTransparentStatusBar(true);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMainViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMainViewModel.class);
        EventHandler eventHandler = new EventHandler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setEvent(eventHandler);
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);//数据绑定
    }

    /**
     * 打开播放服务
     */
    private void bindPlayerService() {
        Intent intent = new Intent(this, PlayerService.class);
        isBind = bindService(intent, connection, BIND_AUTO_CREATE);
    }


    @Override
    protected void observerDataStateUpdateAction() {
        rvm.isPlaying.observe(this, aBoolean -> {
            svm.playing.setValue(aBoolean);
            Log.d("TAG", "isPlaying--->" + aBoolean);
        });
        rvm.song.observe(this, song -> {
            svm.songName.setValue(song.getName() + " - " + song.getArtist());
            svm.mediaPlayerViewBg.setValue(new Random().nextInt(7)+1);
        });
    }

    @Override
    protected void initView() {
        initViewPager2();
        initBottomNav();
        svm.songName.setValue("暂无播放-歌手未知");
        svm.mediaPlayerViewBg.setValue(new Random().nextInt(7)+1);
        svm.duration.setValue(100);
        svm.currentTime.setValue(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "MainActivity onStart()...");
        //注册监听
        EventBus.getDefault().register(this);
        rvm.updatePlayBtn();
        rvm.getCurrentSong();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void initSomeData() {
        //动态申请权限
        rvm.requestPermission(this, this);
        notificationEnable();
        //绑定服务
        bindPlayerService();
        //临时测试：获取本地音乐列表
        rvm.getLocalMusic(this);
    }

    /**
     * 引导打开通知权限
     */
    private void notificationEnable() {
        boolean isEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!isEnabled) {
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请在“通知”中打开通知权限,方便您在以状态栏中实现播放控制")
                    .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("去设置", (dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent();
                        //Android 8.0以上
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", MainActivity.this.getPackageName());
                        startActivity(intent);
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }

    /**
     * 加载ViewPager2
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager2() {
        List<Fragment> fragmentLis = new ArrayList<>();
        fragmentLis.add(new HomeFragment());
        fragmentLis.add(new DiscussionFragment());
        fragmentLis.add(new MineFragment());
        MainViewPager2Adapter adapter = new MainViewPager2Adapter(this, fragmentLis);
        binding.mainViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.mainViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        binding.mainViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNavigation.setSelectedItemId(binding.bottomNavigation.getMenu().getItem(position).getItemId());
            }
        });
    }

    /**
     * 初始化BottomNav与viewPager联动
     * <p>
     * 按住松手后进入选中
     * <p>
     * 拦截item长按事件，去掉文字吐司
     */
    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    private void initBottomNav() {
        //初始化BottomNav与viewPager联动
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_home:
                    binding.mainViewPager2.setCurrentItem(0);
                    return true;
                case R.id.main_discussion:
                    binding.mainViewPager2.setCurrentItem(1);
                    return true;
                case R.id.main_my:
                    binding.mainViewPager2.setCurrentItem(2);
                    return true;
            }
            return false;
        });
        //按住松手后进入选中
        binding.bottomNavigation.findViewById(R.id.main_home).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                binding.bottomNavigation.setSelectedItemId(v.getId());
            return false;
        });
        binding.bottomNavigation.findViewById(R.id.main_discussion).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                binding.bottomNavigation.setSelectedItemId(v.getId());
            return false;
        });
        binding.bottomNavigation.findViewById(R.id.main_my).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                binding.bottomNavigation.setSelectedItemId(v.getId());
            return false;
        });
        //拦截item长按事件，去掉文字吐司
        binding.bottomNavigation.findViewById(R.id.main_home).setOnLongClickListener(view -> true);
        binding.bottomNavigation.findViewById(R.id.main_discussion).setOnLongClickListener(view -> true);
        binding.bottomNavigation.findViewById(R.id.main_my).setOnLongClickListener(view -> true);
    }

    long startTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - startTime < 2000) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            } else {
                //记录时间
                startTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 各种事件监听
     */
    public class EventHandler {
        /**
         * PlayerView播放与暂停
         */
        public PlayOnClickCallback playOnClickCallback = new PlayOnClickCallback() {
            @Override
            public void onClick(boolean isPlaying) {
                if (Objects.requireNonNull(svm.songName.getValue()).startsWith("暂无播放")) return;
                if (isPlaying) {
                    rvm.pause();
                } else {
                    rvm.start();
                }
            }
        };

        /**
         * 播放列表点击事件
         */
        public MusicListOnClickCallback musicListOnClickCallback = new MusicListOnClickCallback() {
            @Override
            public void onClick() {
                SongListItemOnClickCallback songListItemOnClickCallback = song -> rvm.play(song);
                SongListItemRemoveCallback removeCallback = song -> rvm.remove(song);
                MusicListDialog dialog = new MusicListDialog(MainActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog, songListItemOnClickCallback, removeCallback);
                dialog.show();
            }
        };

        /**
         * 点击MediaPlayerView跳转PlayerActivity
         */
        public MediaPlayerViewOnClickCallback mediaPlayerViewOnClickCallback = () -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            startActivity(intent);
        };
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
                rvm.saveDuration(myEvent.getDuration());
                break;
            case 1://当前播放进度
                Log.d("currentPosition", "" + myEvent.getCurrentPosition());
                rvm.saveCurrentTime(myEvent.getCurrentPosition());
                break;
            case 2://播放完成
                Log.d("TAG", "MainActivity 播放完成...");
                rvm.updatePlayBtn();
                rvm.nextSong();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (isBind) {
            unbindService(connection);
        }
        Log.d("TAG", "MainActivity onDestroy...");
        super.onDestroy();
    }
}