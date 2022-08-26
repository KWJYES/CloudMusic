package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;

public class RequestPlayViewModel extends ViewModel {
    public MutableLiveData<Song> song=new MutableLiveData<>();
    public void getCurrentSong() {
        MediaManager.getInstance().getCurrentSong(song);
    }
}
