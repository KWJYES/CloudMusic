package com.example.cloudmusic.request;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;

import java.util.List;

public class RequestMainViewModel extends ViewModel {
    public MutableLiveData<List<Song>> songListLD=new MutableLiveData<>();
    public MutableLiveData<Boolean> isPlaying=new MutableLiveData<>();

    /**
     * 从数据库中拿到播放列表
     */
    public void getSongList(){

    }

    /**
     * 保存播放列表到数据库
     * @param songList
     */
    public void saveSongList(List<Song> songList){

    }
}
