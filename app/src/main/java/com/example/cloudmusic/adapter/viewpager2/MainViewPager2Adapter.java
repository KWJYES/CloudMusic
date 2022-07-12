package com.example.cloudmusic.adapter.viewpager2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class MainViewPager2Adapter extends FragmentStateAdapter {

    public List<Fragment> mFragmentList;

    public MainViewPager2Adapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> fragmentList) {
        super(fragmentActivity);
        mFragmentList=fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList==null?null:mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList==null?0:mFragmentList.size();
    }
}
