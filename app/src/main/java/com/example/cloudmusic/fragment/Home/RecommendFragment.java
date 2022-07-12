package com.example.cloudmusic.fragment.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.banner.RecommendBannerAdapter;
import com.example.cloudmusic.databinding.FragmentRecommendBinding;
import com.example.cloudmusic.entity.BannerData;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;


public class RecommendFragment extends Fragment {

    FragmentRecommendBinding binding;

    public RecommendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_recommend,container, false);
        initBanner();
        return binding.getRoot();
    }

    /**
     * 加载banner
     */
    private void initBanner() {
        List<BannerData> bannerDataList=new ArrayList<>();
        BannerData bannerData=new BannerData();
        bannerData.setUrl("https://img2.baidu.com/it/u=1792249350,650626052&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=675");
        bannerDataList.add(bannerData);
        BannerData bannerData2=new BannerData();
        bannerData2.setUrl("https://img0.baidu.com/it/u=1546227440,2897989905&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500");
        bannerDataList.add(bannerData2);
        BannerData bannerData3=new BannerData();
        bannerData3.setUrl("https://img0.baidu.com/it/u=985192759,2265250910&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333");
        bannerDataList.add(bannerData3);
        binding.recommendBanner.addBannerLifecycleObserver(getActivity())
                .setAdapter(new RecommendBannerAdapter(bannerDataList))
                .setIndicator(new CircleIndicator(getActivity()));

    }
}