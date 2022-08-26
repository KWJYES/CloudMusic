package com.example.cloudmusic.request.fragment.main.mine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongList;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMineFragmentViewModel extends ViewModel {
    public MutableLiveData<Song> recentSong=new MutableLiveData<>();
    public MutableLiveData<SongList> songs=new MutableLiveData<>();
    public MutableLiveData<String> signInState=new MutableLiveData<>();
    public MutableLiveData<String> loginOutState=new MutableLiveData<>();

    public void playRecentSong(Song s) {
        MediaManager.getInstance().play(null, recentSong, s);
    }

    public void getRecentSongs() {
        LitePalManager.getInstance().getSongList("historyList",songs);
    }

    public void signIn(){
        HttpRequestManager.getInstance().signIn(signInState);
    }

    public void loginOut(){
        HttpRequestManager.getInstance().loginOut(loginOutState);
    }
}
