package com.example.cloudmusic.request.fragment.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;

import java.util.List;

public class RequestSearchingFragmentViewModel extends ViewModel {
    public MutableLiveData<List<HistorySearch>> historySearch=new MutableLiveData<>();
    public MutableLiveData<List<String>> hotSearch=new MutableLiveData<>();

    /**
     * 播放
     * @param
     */
    public void playHotList(List<SearchWord> searchWordList){

    }

    public void getHistorySearch(){
        LitePalManager.getInstance().getHistorySearch(historySearch);
    }

    public void clearHistorySearch(){
        LitePalManager.getInstance().clearHistorySearch(historySearch);
    }
}
