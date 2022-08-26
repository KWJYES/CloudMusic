package com.example.cloudmusic.request.fragment.search.searched;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestArtistFragmentViewModel extends ViewModel {
    public MutableLiveData<String> requestState=new MutableLiveData<>();
    public MutableLiveData<List<Artist>> artistList=new MutableLiveData<>();

    public void searchArtist(String keyword){
        HttpRequestManager.getInstance().searchArtist(keyword,artistList,requestState);
    }

    public void loadMoreArtist(String keyword,int offset){
        HttpRequestManager.getInstance().loadMoreSearchArtist(keyword,offset,artistList,requestState);
    }
}
