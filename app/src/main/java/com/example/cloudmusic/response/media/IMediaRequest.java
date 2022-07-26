package com.example.cloudmusic.response.media;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.sevices.PlayerService;

import java.util.List;

public interface IMediaRequest {
    void addSongs(List<Song> songs);
    void addSong(Song song);
    void removeSong(Song song,MutableLiveData<Boolean> isPlaying,MutableLiveData<Song> songLD);
    void play(MutableLiveData<Boolean> isPlaying,MutableLiveData<Song> songLD,Song song);//播放
    void pause(MutableLiveData<Boolean> isPlaying);//暂停
    void start(MutableLiveData<Boolean> isPlaying);//继续
    void seekToPosition(int position);//跳到相应位置
    void isPlaying(MutableLiveData<Boolean> isPlaying);
    boolean isPlaying();
    void getLocalMusicData(Context context, MutableLiveData<List<Song>> songList);
    void formatTime(int time, MutableLiveData<String> timeLD);
    void getCurrentSong(MutableLiveData<Song> songLD);
    void setCurrentSong(Song song,MutableLiveData<Song> songLD);
    Song getCurrentSong();
    void getDuration(MutableLiveData<Integer> durationLD);
    void setDuration(int duration);
    void getCurrentTime(MutableLiveData<Integer> currentTimeLD);
    void setCurrentTime(int currentTime);
    void setPlayerBinder(PlayerService.PlayerBinder playerBinder);
    void nextSong(MutableLiveData<Boolean> isPlaying,MutableLiveData<Song> songLD);
    void lastSong(MutableLiveData<Boolean> isPlaying,MutableLiveData<Song> songLD);
    void playMode(MutableLiveData<Boolean> isPlaying, MutableLiveData<Integer> playModeLD);
    void getPlayMode(MutableLiveData<Integer> playModeLD);
}
