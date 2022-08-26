package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestArtisViewModel extends ViewModel {
    public MutableLiveData<List<Song>> songList = new MutableLiveData<>();
    public MutableLiveData<String> arDec = new MutableLiveData<>();
    public MutableLiveData<String> requestState = new MutableLiveData<>();
    public MutableLiveData<Song> song = new MutableLiveData<>();
    public MutableLiveData<String> arLikeState = new MutableLiveData<>();
    public MutableLiveData<String> likeState = new MutableLiveData<>();


    public void like(boolean isLike, String songId) {
        HttpRequestManager.getInstance().likeSong(isLike, songId, likeState);
    }

    public void addSongToPlayList(Song song) {
        LitePalManager.getInstance().addSongToPlayList(song);
    }

    public void getArtistDetail(String id) {
        HttpRequestManager.getInstance().artists(id, arDec, songList, requestState);
    }

    public void like(String id, boolean isLike) {
        HttpRequestManager.getInstance().likeArtist(id, isLike, arLikeState);
    }

    public void play(Song s) {
        MediaManager.getInstance().play(null, song, s);
    }


}
