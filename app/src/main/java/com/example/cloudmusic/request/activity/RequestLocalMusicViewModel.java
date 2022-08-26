package com.example.cloudmusic.request.activity;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestLocalMusicViewModel extends ViewModel {
    public MutableLiveData<List<Song>> songList=new MutableLiveData<>();
    public MutableLiveData<Song> songMutableLiveData=new MutableLiveData<>();
    public MutableLiveData<String> likeState = new MutableLiveData<>();


    public void like(boolean isLike, String songId) {
        HttpRequestManager.getInstance().likeSong(isLike, songId, likeState);
    }

    public void addSongToPlayList(Song song) {
        LitePalManager.getInstance().addSongToPlayList(song);
    }
    public void getLocalMusic(Context context){
        MediaManager.getInstance().getLocalMusicData(context,songList);
    }

    public void play(Song song) {
        MediaManager.getInstance().play(null,songMutableLiveData,song);
    }
}
