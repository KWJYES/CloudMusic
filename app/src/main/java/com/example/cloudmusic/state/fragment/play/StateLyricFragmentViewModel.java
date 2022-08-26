package com.example.cloudmusic.state.fragment.play;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

public class StateLyricFragmentViewModel extends ViewModel {
    public MutableLiveData<String> lrc=new MutableLiveData<>();
    public MutableLiveData<String> duration =new MutableLiveData<>();
    public MutableLiveData<String> currentTime=new MutableLiveData<>();
    public MutableLiveData<Song> song=new MutableLiveData<>();

}
