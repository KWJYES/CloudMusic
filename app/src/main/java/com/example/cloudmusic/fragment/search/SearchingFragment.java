package com.example.cloudmusic.fragment.search;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.HistorySearchAdapter;
import com.example.cloudmusic.adapter.recyclerview.HotListAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.state.activity.StateSearchViewModel;
import com.example.cloudmusic.state.fragment.search.StateSearchingFragmentViewModel;
import com.example.cloudmusic.utils.callback.HistorySearchItemClickCallback;
import com.example.cloudmusic.utils.callback.SearchWordsItemClickCallback;
import com.example.cloudmusic.databinding.FragmentSearchingBinding;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.request.fragment.search.RequestSearchingFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchingFragment extends BaseFragment {

    FragmentSearchingBinding binding;
    //StateSearchingFragmentViewModel svm;
    RequestSearchingFragmentViewModel rvm;

    private List<SearchWord> searchWordList;
    private SearchWordsItemClickCallback searchWordsItemClickCallback;
    private HistorySearchItemClickCallback historySearchItemClickCallback;

    public SearchingFragment() {
    }

    public SearchingFragment(List<SearchWord> searchWordList, SearchWordsItemClickCallback searchWordsItemClickCallback, HistorySearchItemClickCallback historySearchItemClickCallback) {
        this.searchWordList = searchWordList;
        this.searchWordsItemClickCallback = searchWordsItemClickCallback;
        this.historySearchItemClickCallback = historySearchItemClickCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSearchingFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_searching, container, false);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        updateHotList(searchWordList);
    }

    @Override
    protected void initSomeData() {
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.historySearch.observe(this, this::upDateHistorySearch);
    }

    @Override
    public void onResume() {
        super.onResume();
        rvm.getHistorySearch();
    }

    /**
     * 更新历史记录
     */
    private void upDateHistorySearch(List<HistorySearch> list) {
        List<HistorySearch> historySearches=new ArrayList<>();
        for(int i= list.size()-1;i>=0;i--){
            historySearches.add(list.get(i));
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL);
        binding.historySearchRV.setLayoutManager(layoutManager);
        HistorySearchAdapter adapter = new HistorySearchAdapter(historySearches);
        adapter.setItemClickCallback(historySearchItemClickCallback);
        binding.historySearchRV.setAdapter(adapter);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder (Objects.requireNonNull(getContext()));
            dialog.setTitle("确定要清除历史记录吗？" );
            dialog.setCancelable(true);
            dialog.setPositiveButton("确定", (dialogInterface, i) -> rvm.clearHistorySearch());
            dialog.setNegativeButton("取消", (dialogInterface, i) -> {});
            dialog.show();
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