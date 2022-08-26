package com.example.cloudmusic.fragment.search;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.ViewPager2Adapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentSearchedBinding;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.fragment.search.searched.ArtistFragment;
import com.example.cloudmusic.fragment.search.searched.LrcFragment;
import com.example.cloudmusic.fragment.search.searched.MusicListFragment;
import com.example.cloudmusic.fragment.search.searched.OneSongFragment;
import com.example.cloudmusic.request.fragment.search.RequestSearchedFragmentViewModel;
import com.example.cloudmusic.state.fragment.search.StateSearchedFragmentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchedFragment extends BaseFragment {

    FragmentSearchedBinding binding;
    RequestSearchedFragmentViewModel rvm;
    StateSearchedFragmentViewModel svm;

    private String searedWord;

    public SearchedFragment() {
    }

    public SearchedFragment(String searedWord) {
        this.searedWord = searedWord;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvm=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(RequestSearchedFragmentViewModel.class);
        svm=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(StateSearchedFragmentViewModel.class);
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_searched,container,false);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initSomeData() {
        HistorySearch historySearch=new HistorySearch();
        historySearch.setKeywords(searedWord);
        rvm.addHistorySearch(historySearch);
    }

    @Override
    protected void initView() {
        svm.keywords.setValue(searedWord);
        initViewPager2();
    }

    public class ClickClass{

    }

    /**
     * 设置viewPager并与TableLayout对接
     */
    private void initViewPager2(){
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("单曲");
        titles.add("歌单");
        titles.add("歌手");
        titles.add("歌词");
        fragmentList.add(new OneSongFragment(svm.keywords.getValue()));
        fragmentList.add(new MusicListFragment(svm.keywords.getValue()));
        fragmentList.add(new ArtistFragment(svm.keywords.getValue()));
        fragmentList.add(new LrcFragment(svm.keywords.getValue()));
        ViewPager2Adapter adapter=new ViewPager2Adapter(Objects.requireNonNull(getActivity()), fragmentList);
        binding.searchedVP2.setAdapter(adapter);
        View childAt=binding.searchedVP2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        //TableLayout对接
        new TabLayoutMediator(binding.searchTablelayout, binding.searchedVP2, (tab, position) -> tab.setText(titles.get(position))).attach();
        binding.searchedVP2.setOffscreenPageLimit(5);
    }
}