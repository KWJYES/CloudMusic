package com.example.cloudmusic.state;

import android.graphics.Color;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

public class StateMainViewModel extends ViewModel {
    public MutableLiveData<String> songName=new MutableLiveData<>();
    public MutableLiveData<String> songPic =new MutableLiveData<>();
    public MutableLiveData<Boolean> playing= new MutableLiveData<>();
    public MutableLiveData<Integer> mediaPlayerViewBg= new MutableLiveData<>();
    public MutableLiveData<Integer> duration= new MutableLiveData<>();
    public MutableLiveData<Integer> currentTime= new MutableLiveData<>();
    public MutableLiveData<Song> song=new MutableLiveData<>();
}
