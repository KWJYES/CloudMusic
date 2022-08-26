package com.example.cloudmusic.state.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

public class StatePlayerViewModel extends ViewModel {
    public MutableLiveData<Song> song=new MutableLiveData<>();

}
