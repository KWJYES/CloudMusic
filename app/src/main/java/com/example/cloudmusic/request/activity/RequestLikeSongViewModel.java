package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestLikeSongViewModel extends ViewModel {
    public MutableLiveData<List<String>> likeIdList = new MutableLiveData<>();
    public MutableLiveData<String> likeIdListRequestState = new MutableLiveData<>();
    public MutableLiveData<Song> song = new MutableLiveData<>();
    public MutableLiveData<String> songRequestState = new MutableLiveData<>();
    public MutableLiveData<List<Song>> songList = new MutableLiveData<>();
    public MutableLiveData<String> songDetailRequestState = new MutableLiveData<>();
    public MutableLiveData<String> likeState = new MutableLiveData<>();


    public void like(boolean isLike, String songId) {
        HttpRequestManager.getInstance().likeSong(isLike, songId, likeState);
    }

    public void addSongToPlayList(Song song) {
        LitePalManager.getInstance().addSongToPlayList(song);
    }

    public void getLikeIdList(String uid) {
        HttpRequestManager.getInstance().getLikeSongIdList(uid, likeIdList, likeIdListRequestState);
    }

    public void getLikeSong(List<String> songIds) {
        HttpRequestManager.getInstance().getSongDetail(songIds, songList, songDetailRequestState);
    }

    public void play(Song s) {
        MediaManager.getInstance().play(null, song, s);
    }
}
