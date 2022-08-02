package com.example.cloudmusic.response.network;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;

import java.util.List;

public interface INetworkRequest {
    void getBannerData(MutableLiveData<List<Banner>> bannerRequestResult, MutableLiveData<String> bannerRequestState);
    void getLoginState(MutableLiveData<Boolean> loginState);
    void getRecommendMusicList(MutableLiveData<List<MusicList>> recommendMusicListResult,MutableLiveData<String> recommendMusicListRequestState);
    void getDefaultSearchWord(MutableLiveData<String> defaultSearchWord,MutableLiveData<String> defaultSearchWordState);
    void getHotList(MutableLiveData<List<SearchWord>> hotList, MutableLiveData<String> hotListState);
}
