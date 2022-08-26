package com.example.cloudmusic.request.fragment.play;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongLrc;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestLyricFragmentViewModel extends ViewModel {
    public MutableLiveData<Boolean> isPlaying =new MutableLiveData<>();
    public MutableLiveData<Song> song =new MutableLiveData<>();
    public MutableLiveData<Integer> currentPositionLD=new MutableLiveData<>();
    public MutableLiveData<SongLrc> songLrc=new MutableLiveData<>();


    public void getSongLrc(Song song){
        HttpRequestManager.getInstance().lyric(song.getSongId(),songLrc);
    }
    public void getInitCurrentTime(){
        MediaManager.getInstance().getCurrentTime(currentPositionLD);
    }

    public void seekTo(int position){
        MediaManager.getInstance().seekToPosition(position);
    }

    /**
     * 播放与暂停
     */
    public void startPause() {
        if(MediaManager.getInstance().isPlaying()){
            MediaManager.getInstance().pause(isPlaying);
        }else {
            MediaManager.getInstance().start(isPlaying);
        }
    }

    public void updatePlayModeBtn() {
        MediaManager.getInstance().isPlaying(isPlaying);
    }

    public void getCurrentSong() {
        MediaManager.getInstance().getCurrentSong(song);
    }
}
