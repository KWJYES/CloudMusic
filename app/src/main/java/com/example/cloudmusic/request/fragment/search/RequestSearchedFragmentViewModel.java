package com.example.cloudmusic.request.fragment.search;

import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.response.db.LitePalManager;

public class RequestSearchedFragmentViewModel extends ViewModel {
    public void addHistorySearch(HistorySearch historySearch){
        LitePalManager.getInstance().addHistorySearch(historySearch);
    }
}
