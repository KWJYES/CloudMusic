package com.example.cloudmusic.request.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestArtistAllFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Artist>> artistList = new MutableLiveData<>();
    public MutableLiveData<String> artistListRequestState = new MutableLiveData<>();

    public void getArtistList(String area, String type,int offset) {
        HttpRequestManager.getInstance().artistList(type, area,offset, artistListRequestState, artistList);
    }
}
