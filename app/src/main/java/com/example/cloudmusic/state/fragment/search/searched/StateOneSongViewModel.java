package com.example.cloudmusic.state.fragment.search.searched;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

import java.util.List;

public class StateOneSongViewModel extends ViewModel {
    public MutableLiveData<List<Song>> oneSongList=new MutableLiveData<>();
    public MutableLiveData<String> keywords=new MutableLiveData<>();
}