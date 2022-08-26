package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMusicListViewModel extends ViewModel {
    public MutableLiveData<String> dec = new MutableLiveData<>();
    public MutableLiveData<String> songListRequestState = new MutableLiveData<>();
    public MutableLiveData<List<Song>> songList = new MutableLiveData<>();
    public MutableLiveData<Song> song = new MutableLiveData<>();
    public MutableLiveData<String> likeState = new MutableLiveData<>();


    public void like(boolean isLike, String songId) {
        HttpRequestManager.getInstance().likeSong(isLike, songId, likeState);
    }

    public void addSongToPlayList(Song song) {
        LitePalManager.getInstance().addSongToPlayList(song);
    }

    public void getMusicListDec(String musicListId) {
        HttpRequestManager.getInstance().getPlayListDec(musicListId, dec);
    }

    public void getSongs(String musicListId) {
        HttpRequestManager.getInstance().getPlayListSongs(musicListId, songListRequestState, songList);
    }

    public void playSong(Song s) {
        MediaManager.getInstance().play(null, song, s);
    }
}
