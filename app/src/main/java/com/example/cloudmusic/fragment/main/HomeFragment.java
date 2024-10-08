package com.example.cloudmusic.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.SearchActivity;
import com.example.cloudmusic.adapter.viewpager2.ViewPager2Adapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentHomeBinding;
import com.example.cloudmusic.fragment.main.home.ArtistFragment;
import com.example.cloudmusic.fragment.main.home.MusicListFragment;
import com.example.cloudmusic.fragment.main.home.RecommendFragment;
import com.example.cloudmusic.request.fragment.main.RequestHomeFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.StateHomeFragmentViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;
    StateHomeFragmentViewModel svm;
    RequestHomeFragmentViewModel rvm;


    @Override
    public void onPause() {
        Log.d("TAG", "omeFragment onPause");
        binding.homeViewPager2.setUserInputEnabled(true);
        super.onPause();
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateHomeFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestHomeFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    protected void initView() {
        initViewPager2();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.defaultSearchWord.observe(this, s -> svm.defaultSearchWord.setValue(s + " 最近很火"));
    }

    @Override
    protected void initSomeData() {

    }

    @Override
    protected void getInternetData() {
        if (svm.defaultSearchWord.getValue() == null || svm.defaultSearchWord.getValue().equals("")) {
            rvm.requestDefaultSearchWord();
        }
    }

    /**
     * 设置viewPager并与TableLayout对接
     */
    private void initViewPager2() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("推荐");
        titles.add("歌手");
        titles.add("歌单");
        fragmentList.add(new RecommendFragment());
        fragmentList.add(new ArtistFragment());
        fragmentList.add(new MusicListFragment());
        ViewPager2Adapter adapter = new ViewPager2Adapter(Objects.requireNonNull(getActivity()), fragmentList);
        binding.homeViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.homeViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        //TableLayout对接
        new TabLayoutMediator(binding.homeTablelayout, binding.homeViewPager2, (tab, position) -> tab.setText(titles.get(position))).attach();
    }


    public class ClickClass {
        public void search(View view) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra("defaultSearchWord", svm.defaultSearchWord.getValue());
            startActivity(intent);
        }
    }
}