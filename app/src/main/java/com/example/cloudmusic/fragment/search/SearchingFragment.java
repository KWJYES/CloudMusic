package com.example.cloudmusic.fragment.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.HotListAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.callback.HotListItemClickCallback;
import com.example.cloudmusic.databinding.FragmentSearchingBinding;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.RequestLoginViewModel;
import com.example.cloudmusic.request.RequestSearchingFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchingFragment extends BaseFragment {

    FragmentSearchingBinding binding;
    RequestSearchingFragmentViewModel rvm;

    public SearchingFragment(List<SearchWord> searchWordList) {
        this.searchWordList = searchWordList;
    }

    private List<SearchWord> searchWordList;



    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvm=new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSearchingFragmentViewModel.class);
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_searching,container,false);
        binding.setLifecycleOwner(this);
        updateHotList(searchWordList);
        return binding.getRoot();
    }

    /**
     * 更新热搜榜
     * @param searchWordList 热搜榜
     */
    private void updateHotList(List<SearchWord> searchWordList){
        if (searchWordList==null) return;
        binding.hotListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HotListAdapter adapter=new HotListAdapter(new ArrayList<>(searchWordList));
        adapter.setItemClickCallback(searchWord -> rvm.search(searchWord));
        binding.hotListRecyclerView.setAdapter(adapter);
    }
}