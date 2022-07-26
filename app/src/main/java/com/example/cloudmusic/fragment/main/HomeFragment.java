package com.example.cloudmusic.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.ViewPager2Adapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentHomeBinding;
import com.example.cloudmusic.fragment.home.MusicRoomFragment;
import com.example.cloudmusic.fragment.home.RecommendFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container, false);
        return binding.getRoot();
    }


    @Override
    protected void initView() {
        initViewPager2();
    }

    /**
     * 设置viewPager并与TableLayout对接
     */
    private void initViewPager2() {
        List<Fragment> fragmentList=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        titles.add("推荐");
        titles.add("音乐馆");
        fragmentList.add(new RecommendFragment());
        fragmentList.add(new MusicRoomFragment());
        ViewPager2Adapter adapter=new ViewPager2Adapter(Objects.requireNonNull(getActivity()),fragmentList);
        binding.homeViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.homeViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        //TableLayout对接
        new TabLayoutMediator(binding.homeTablelayout, binding.homeViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();
    }
}