package com.example.cloudmusic.request;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;


public class RequestSearchViewModel extends ViewModel {
    public MutableLiveData<List<SearchWord>> hotList=new MutableLiveData<>();
    public MutableLiveData<String> hotListState=new MutableLiveData<>();


    public void requestHotList() {
        HttpRequestManager.getInstance().getHotList(hotList,hotListState);
    }
}
