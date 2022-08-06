package com.example.cloudmusic.fragment.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.HotListAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.utils.callback.SearchWordsItemClickCallback;
import com.example.cloudmusic.databinding.FragmentSearchingBinding;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.request.fragment.search.RequestSearchingFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchingFragment extends BaseFragment {

    FragmentSearchingBinding binding;
    RequestSearchingFragmentViewModel rvm;

    private List<SearchWord> searchWordList;
    private SearchWordsItemClickCallback searchWordsItemClickCallback;

    public SearchingFragment() {
    }

    public SearchingFragment(List<SearchWord> searchWordList, SearchWordsItemClickCallback searchWordsItemClickCallback) {
        this.searchWordList = searchWordList;
        this.searchWordsItemClickCallback = searchWordsItemClickCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSearchingFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_searching, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        updateHotList(searchWordList);
    }

    /**
     * 更新热搜榜
     *
     * @param searchWordList 热搜榜
     */
    private void updateHotList(List<SearchWord> searchWordList) {
        if (searchWordList == null) return;
        binding.hotListLoading.hide();
        binding.hotListLoadingTV.setVisibility(View.INVISIBLE);
        binding.hotListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HotListAdapter adapter = new HotListAdapter(new ArrayList<>(searchWordList));
        adapter.setItemClickCallback(searchWordsItemClickCallback);
        binding.hotListRecyclerView.setAdapter(adapter);
    }

    public class ClickClass {
        /**
         * 清除历史记录
         *
         * @param view
         */
        public void clearHistorySearch(View view) {

        }

        /**
         * 播放热搜榜相关音乐
         *
         * @param view
         */
        public void playHot(View view) {
            rvm.playHotList(searchWordList);
        }
    }
}