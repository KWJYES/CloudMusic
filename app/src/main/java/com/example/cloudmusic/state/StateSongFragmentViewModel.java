package com.example.cloudmusic.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateSongFragmentViewModel extends ViewModel {
    public MutableLiveData<String> time=new MutableLiveData<>();//歌曲时长
    public MutableLiveData<String> currentTime=new MutableLiveData<>();//当前时间
    public MutableLiveData<String> songAr=new MutableLiveData<>();//歌手
    public MutableLiveData<String> songName=new MutableLiveData<>();//歌名
    public MutableLiveData<String> songLyc=new MutableLiveData<>();//歌词
}
