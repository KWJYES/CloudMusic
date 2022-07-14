package com.example.cloudmusic.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Banner;

import java.util.List;

public class StateRecommendFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Banner>> bannerDataList=new MutableLiveData<>();
}
