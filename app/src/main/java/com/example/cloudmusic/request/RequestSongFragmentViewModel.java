package com.example.cloudmusic.request;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;

public class RequestSongFragmentViewModel extends ViewModel {
    public MutableLiveData<Boolean> isPlaying =new MutableLiveData<>();
    public MutableLiveData<String> duration =new MutableLiveData<>();
    public MutableLiveData<String> currentPosition =new MutableLiveData<>();
    public MutableLiveData<Integer> durationLD =new MutableLiveData<>();
    public MutableLiveData<Integer> currentPositionLD =new MutableLiveData<>();
    public MutableLiveData<Integer> playMode =new MutableLiveData<>();
    public MutableLiveData<Song> song=new MutableLiveData<>();



    public void saveDuration(int duration){
        MediaManager.getInstance().setDuration(duration);
    }

    public void saveCurrentTime(int currentTime){
        MediaManager.getInstance().setCurrentTime(currentTime);
    }

    public void remove(Song s){
        MediaManager.getInstance().removeSong(s,isPlaying,song);
    }

    /**
     * 初始化开始暂停按键UI
     */
    public void updatePlayBtn(){
        MediaManager.getInstance().isPlaying(isPlaying);
    }

    public void updatePlayModeBtn(){
        MediaManager.getInstance().getPlayMode(playMode);
    }

    public void play(Song s){
        MediaManager.getInstance().play(isPlaying,song,s);
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

    /**
     * 下一曲
     */
    public void nextSong(){
        MediaManager.getInstance().nextSong(isPlaying,song);
    }

    /**
     * 上一曲
     */
    public void lastSong(){
        MediaManager.getInstance().lastSong(isPlaying,song);
    }

    /**
     * 切换播放模式
     *  列表循环
     *  随机播放
     *  单曲循环
     */
    public void playModel(){
        MediaManager.getInstance().playMode(isPlaying,playMode);
    }

    public void seekTo(int position){
        MediaManager.getInstance().seekToPosition(position);
    }


    public void formatDuration(int time){
        MediaManager.getInstance().formatTime(time,duration);
    }

    public void formatCurrentTime(int time){
        MediaManager.getInstance().formatTime(time,currentPosition);
    }

    public void getInitDuration(){
        MediaManager.getInstance().getDuration(durationLD);
    }

    public void getInitCurrentTime(){
        MediaManager.getInstance().getCurrentTime(currentPositionLD);
    }

    public void getCurrentSong(){
        MediaManager.getInstance().getCurrentSong(song);
    }
}
