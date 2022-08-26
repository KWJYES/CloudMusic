package com.example.cloudmusic.request.fragment.search.searched;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Lyrics;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestLrcFragmentViewModel extends ViewModel {
    public MutableLiveData<String> requestState=new MutableLiveData<>();
    public MutableLiveData<List<Lyrics>> lrcList=new MutableLiveData<>();

    public void search(String keyword){
        HttpRequestManager.getInstance().searchLyrics(keyword,lrcList,requestState);
    }

    public void loadMore(String keyword,int offset){
        HttpRequestManager.getInstance().loadMoreLyrics(keyword,offset,lrcList,requestState);
    }
}
