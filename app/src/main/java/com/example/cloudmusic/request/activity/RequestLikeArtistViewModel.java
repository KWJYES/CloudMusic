package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestLikeArtistViewModel extends ViewModel {
    public MutableLiveData<List<Artist>> artistList=new MutableLiveData<>();
    public MutableLiveData<String> artistListRequestState=new MutableLiveData<>();

    public void getLikeArtistList(){
        HttpRequestManager.getInstance().getLikeArtist(artistList,artistListRequestState);
    }
}
