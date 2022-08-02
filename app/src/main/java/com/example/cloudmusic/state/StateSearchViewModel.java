package com.example.cloudmusic.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.entity.Song;

import java.util.List;

public class StateSearchViewModel extends ViewModel {
    public MutableLiveData<String> defaultSearchWord=new MutableLiveData<>();
    public MutableLiveData<String> searchWord=new MutableLiveData<>();
    public MutableLiveData<List<SearchWord>> hotList=new MutableLiveData<>();
}
