package com.example.cloudmusic.request.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;


public class RequestRecommendFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Banner>> bannerRequestResult=new MutableLiveData<>();
    public MutableLiveData<String> bannerRequestState=new MutableLiveData<>();
    public MutableLiveData<List<MusicList>> recommendMusicListResult=new MutableLiveData<>();
    public MutableLiveData<String> recommendMusicListRequestState=new MutableLiveData<>();

    public void requestBannerData(){
        HttpRequestManager.getInstance().getBannerData(bannerRequestResult,bannerRequestState);
    }

    public void requestRecommendMusicList(){
        HttpRequestManager.getInstance().getRecommendMusicList(recommendMusicListResult,recommendMusicListRequestState);
    }

}
