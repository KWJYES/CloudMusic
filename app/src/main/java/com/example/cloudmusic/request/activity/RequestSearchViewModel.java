package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;


public class RequestSearchViewModel extends ViewModel {
    public MutableLiveData<List<SearchWord>> hotList=new MutableLiveData<>();
    public MutableLiveData<String> hotListState=new MutableLiveData<>();
    public MutableLiveData<Song> songPlay=new MutableLiveData<>();


    public void requestHotList() {
        HttpRequestManager.getInstance().getHotList(hotList,hotListState);
    }

    public void remove(Song s){
        MediaManager.getInstance().removeSong(s,null,null);
    }
    public void play(Song s){
        MediaManager.getInstance().play(null,null,s);
    }
//    public void getSongUrl(Song s){
//        HttpRequestManager.getInstance().getSongUrl(s,songPlay);
//    }
}
