package com.example.cloudmusic.request.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Banner;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.entity.MusicList;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;


public class RequestRecommendFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Banner>> bannerRequestResult = new MutableLiveData<>();
    public MutableLiveData<String> bannerRequestState = new MutableLiveData<>();
    public MutableLiveData<String> mvRequestState = new MutableLiveData<>();
    public MutableLiveData<List<MusicList>> recommendMusicListResult = new MutableLiveData<>();
    public MutableLiveData<String> recommendMusicListRequestState = new MutableLiveData<>();
    public MutableLiveData<String> newSongListRequestState = new MutableLiveData<>();
    public MutableLiveData<Song> bannerSong = new MutableLiveData<>();
    public MutableLiveData<Song> newSong = new MutableLiveData<>();
    public MutableLiveData<List<Song>> newSongList = new MutableLiveData<>();
    public MutableLiveData<List<MV>> mvList = new MutableLiveData<>();

    public void requestBannerData() {
        HttpRequestManager.getInstance().getBannerData(bannerRequestResult, bannerRequestState);
    }

    public void requestRecommendMusicList() {
        HttpRequestManager.getInstance().getRecommendMusicList(recommendMusicListResult, recommendMusicListRequestState);
    }

    public void playBannerSong(Song s) {
        MediaManager.getInstance().play(null, bannerSong, s);
    }

    public void playNewSong(Song s) {
        MediaManager.getInstance().play(null, newSong, s);
    }

    public void requestNewSong() {
        HttpRequestManager.getInstance().newSongRecommend(newSongList, newSongListRequestState);
    }

    public void requestMV() {
        HttpRequestManager.getInstance().personalizedMv(mvList, mvRequestState);
    }
}
