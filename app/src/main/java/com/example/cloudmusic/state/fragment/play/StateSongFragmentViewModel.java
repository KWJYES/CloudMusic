package com.example.cloudmusic.state.fragment.play;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

public class StateSongFragmentViewModel extends ViewModel {

    public MutableLiveData<String> duration =new MutableLiveData<>();//歌曲时长
    public MutableLiveData<String> currentPosition =new MutableLiveData<>();//当前时间
    public MutableLiveData<String> songAr=new MutableLiveData<>();//歌手
    public MutableLiveData<String> songName=new MutableLiveData<>();//歌名
    public MutableLiveData<String> songLyc=new MutableLiveData<>();//歌词
    public MutableLiveData<Song> song=new MutableLiveData<>();
}
