package com.example.cloudmusic.fragment.Main;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.HomeViewPager2Adapter;
import com.example.cloudmusic.databinding.FragmentHomeBinding;
import com.example.cloudmusic.fragment.Home.RankingListFragment;
import com.example.cloudmusic.fragment.Home.RecommendFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private ViewPager2 vp2;

    public HomeFragment(ViewPager2 viewPager2){
        vp2=viewPager2;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","HomeFragment onCreate...");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG","HomeFragment onCreateView...");
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container, false);
        initViewPager2();
        return binding.getRoot();
    }

    /**
     * 设置viewPager并与TableLayout对接
     */
    private void initViewPager2() {
        List<Fragment> fragmentList=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        titles.add("推荐");
        titles.add("排行榜");
        fragmentList.add(new RecommendFragment());
        fragmentList.add(new RankingListFragment());
        HomeViewPager2Adapter adapter=new HomeViewPager2Adapter(Objects.requireNonNull(getActivity()),fragmentList);
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