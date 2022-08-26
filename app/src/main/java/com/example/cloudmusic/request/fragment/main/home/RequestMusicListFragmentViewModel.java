package com.example.cloudmusic.request.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMusicListFragmentViewModel extends ViewModel {
    public MutableLiveData<List<PlayList>> playLists=new MutableLiveData<>();
    public MutableLiveData<String> playListRequestState=new MutableLiveData<>();

    public void getPlayList(String cat,int offset){
        HttpRequestManager.getInstance().getPlayList(cat,offset,playLists,playListRequestState);
    }
}
