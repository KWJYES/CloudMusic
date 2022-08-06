package com.example.cloudmusic.state.fragment.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class StateSearchingFragmentViewModel extends ViewModel {
    public MutableLiveData<List<String>> searchHistory=new MutableLiveData<>();
    public MutableLiveData<List<String>> hotSearch=new MutableLiveData<>();
}
