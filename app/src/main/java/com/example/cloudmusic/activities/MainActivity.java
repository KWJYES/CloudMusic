package com.example.cloudmusic.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.MainViewPager2Adapter;
import com.example.cloudmusic.adapter.recyclerview.MusicAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.callback.MusicListOnClickCallback;
import com.example.cloudmusic.callback.StartOrPauseOnClickCallback;
import com.example.cloudmusic.databinding.ActivityMainBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.fragment.Main.DiscussionFragment;
import com.example.cloudmusic.fragment.Main.HomeFragment;
import com.example.cloudmusic.fragment.Main.MineFragment;
import com.example.cloudmusic.state.StateMainViewModel;
import com.example.cloudmusic.views.MusicListDialog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private StateMainViewModel stateMainViewModel;

    @Override
    protected void initActivity() {
        LitePal.getDatabase();//初始化数据库
        setTransparentStatusBar(true);
        stateMainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMainViewModel.class);
        EventHandler eventHandler = new EventHandler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setEvent(eventHandler);
        binding.setSvm(stateMainViewModel);
        stateMainViewModel.songName.setValue("海阔天空-Beyond    海阔天空-Beyond");
        stateMainViewModel.songPng.setValue("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fnimg.ws.126.net%2F%3Furl%3Dhttp%253A%252F%252Fdingyue.ws.126.net%252F2021%252F1109%252F1b2cbf6fj00r2b2pp000rc000hs00cjc.jpg%26thumbnail%3D650x2147483647%26quality%3D80%26type%3Djpg&refer=http%3A%2F%2Fnimg.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659362579&t=2f5717119c70f033498d81f5812a86e9");
        stateMainViewModel.mediaPlayerViewBg.setValue(6);
        initViewPager2();
        initBottomNav();
    }


    /**
     * 加载ViewPager2
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager2() {
        List<Fragment> fragmentLis = new ArrayList<>();
        fragmentLis.add(new HomeFragment(binding.mainViewPager2));
        fragmentLis.add(new DiscussionFragment());
        fragmentLis.add(new MineFragment());
        MainViewPager2Adapter adapter = new MainViewPager2Adapter(this, fragmentLis);
        binding.mainViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.mainViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
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
     *
     * 按住松手后进入选中
     *
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

    /**
     * 各种事件监听
     */
    public class EventHandler {
        public StartOrPauseOnClickCallback startOrPauseOnClickCallback = new StartOrPauseOnClickCallback() {
            @Override
            public void onClick(boolean isPlaying) {

            }
        };

        public MusicListOnClickCallback musicListOnClickCallback = new MusicListOnClickCallback() {
            @Override
            public void onClick() {
                MusicListDialog dialog=new MusicListDialog(MainActivity.this,R.style.Base_ThemeOverlay_AppCompat_Dialog);
                dialog.show();
            }
        };

    }

}