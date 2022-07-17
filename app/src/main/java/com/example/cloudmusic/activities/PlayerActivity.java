package com.example.cloudmusic.activities;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.ViewPager2Adapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityPlayerBinding;
import com.example.cloudmusic.fragment.home.RankingListFragment;
import com.example.cloudmusic.fragment.home.RecommendFragment;
import com.example.cloudmusic.fragment.play.LyricFragment;
import com.example.cloudmusic.fragment.play.SongFragment;
import com.example.cloudmusic.request.RequestPlayViewModel;
import com.example.cloudmusic.state.StatePlayerViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends BaseActivity {

    private ActivityPlayerBinding binding;
    private StatePlayerViewModel svm;
    private RequestPlayViewModel rvm;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(true);
        svm=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(StatePlayerViewModel.class);
        rvm=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(RequestPlayViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);//数据绑定
    }

    @Override
    protected void initView() {
        initViewPager2();
    }

    private void initViewPager2() {
        List<Fragment> fragmentList=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        titles.add("歌曲");
        titles.add("歌词");
        fragmentList.add(new SongFragment());
        fragmentList.add(new LyricFragment());
        ViewPager2Adapter adapter=new ViewPager2Adapter(PlayerActivity.this,fragmentList);
        binding.playViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.playViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        //TableLayout对接
        new TabLayoutMediator(binding.playTablelayout, binding.playViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();
    }

    public class ClickClass {

        /**
         * 返回
         * @param view
         */
        public void back(View view){
            finish();
        }


    }
}
