package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongList;
import com.example.cloudmusic.entity.SongLrc;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestHistoryPlayViewModel extends ViewModel {
    public MutableLiveData<SongList> songList = new MutableLiveData<>();
    public MutableLiveData<String> likeState = new MutableLiveData<>();


    public void like(boolean isLike, String songId) {
        HttpRequestManager.getInstance().likeSong(isLike, songId, likeState);
    }

    public void addSongToPlayList(Song song) {
        LitePalManager.getInstance().addSongToPlayList(song);
    }

    public void getHistoryPlay() {
        LitePalManager.getInstance().getSongList("historyList", songList);
    }
}
