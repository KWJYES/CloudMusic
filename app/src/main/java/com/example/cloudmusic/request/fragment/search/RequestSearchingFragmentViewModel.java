package com.example.cloudmusic.request.fragment.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;

import java.util.List;

public class RequestSearchingFragmentViewModel extends ViewModel {
    public MutableLiveData<List<String>> searchHistory=new MutableLiveData<>();
    public MutableLiveData<List<String>> hotSearch=new MutableLiveData<>();

    /**
     * 播放
     * @param
     */
    public void playHotList(List<SearchWord> searchWordList){

    }
}
