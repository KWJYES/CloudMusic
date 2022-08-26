package com.example.cloudmusic.request.fragment.search.searched;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.entity.PlayList;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMusicListViewModel extends ViewModel {
    public MutableLiveData<String> requestState=new MutableLiveData<>();
    public MutableLiveData<List<PlayList>> playList=new MutableLiveData<>();

    public void search(String keyword){
        HttpRequestManager.getInstance().searchMusicList(keyword,playList,requestState);
    }

    public void loadMore(String keyword, int offset){
        HttpRequestManager.getInstance().loadMoreMusicList(keyword,offset,playList,requestState);
    }
}
