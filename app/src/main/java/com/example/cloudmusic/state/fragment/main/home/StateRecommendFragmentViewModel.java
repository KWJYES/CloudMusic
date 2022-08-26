package com.example.cloudmusic.state.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.Song;

import java.util.List;

public class StateRecommendFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Banner>> bannerDataList=new MutableLiveData<>();
    public MutableLiveData<List<MusicList>> musicListList=new MutableLiveData<>();
    public MutableLiveData<List<Song>> newSongList=new MutableLiveData<>();
}
