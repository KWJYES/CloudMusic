package com.example.cloudmusic.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.banner.RecommendBannerAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentRecommendBinding;
import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.request.RequestRecommendFragmentViewModel;
import com.example.cloudmusic.state.StateRecommendFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecommendFragment extends BaseFragment {

    FragmentRecommendBinding binding;
    StateRecommendFragmentViewModel svm;
    RequestRecommendFragmentViewModel rvm;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_recommend,container, false);
        svm=new ViewModelProvider(Objects.requireNonNull(getActivity()),new ViewModelProvider.NewInstanceFactory()).get(StateRecommendFragmentViewModel.class);
        rvm=new ViewModelProvider(Objects.requireNonNull(getActivity()),new ViewModelProvider.NewInstanceFactory()).get(RequestRecommendFragmentViewModel.class);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        //下拉刷新
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getBannerData();

        });
        //上拉加载
        binding.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            binding.smartRefreshLayout.finishLoadMore(1500);
        });
    }

    /**
     * 监测数据变化
     */
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.bannerRequestState.observe(this, s -> {
            binding.bannerLoading.hide();
            if(s.equals(CloudMusic.FAILURE)){
                Banner banner=new Banner();
                banner.setPic("isFail");
                banner.setTypeTitle("加载失败");
                List<Banner> bannerList = new ArrayList<>();
                bannerList.add(banner);
                setBanner(bannerList);

            }
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.bannerRequestResult.observe(this, this::setBanner);
    }

    @Override
    protected void initSomeData() {
        getBannerData();
    }

    /**
     * 播放加载动画与背景
     * 获取banner数据
     */
    private void getBannerData() {
        binding.bannerLoading.show();
        Banner banner=new Banner();
        banner.setPic("isLoading");
        banner.setTypeTitle("加载中...");
        List<Banner> bannerList = new ArrayList<>();
        bannerList.add(banner);
        setBanner(bannerList);
        rvm.requestBannerData();
    }

    /**
     * 加载banner
     */
    private void setBanner(List<Banner> bannerData) {
        List<Banner> bannerList = new ArrayList<>(bannerData);
        binding.recommendBanner.addBannerLifecycleObserver(getActivity()).setAdapter(new RecommendBannerAdapter(bannerList))
                .setIndicator(new CircleIndicator(getActivity()));

    }
}