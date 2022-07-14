package com.example.cloudmusic.response.network;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Banner;

import java.util.List;

public interface INetworkRequest {
    void getBannerData(MutableLiveData<List<Banner>> bannerRequestResult, MutableLiveData<String> bannerRequestState);
}
