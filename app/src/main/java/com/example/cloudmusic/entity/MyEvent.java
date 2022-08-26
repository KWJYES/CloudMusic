package com.example.cloudmusic.entity;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

public class MyEvent {
    private int currentPosition;
    private String currentPositionS;
    private int duration;
    private String durationS;
    private int msg;
    private Song song;
    private boolean isPlaying;
    private boolean isRemoveSong;

    public boolean isRemoveSong() {
        return isRemoveSong;
    }

    public void setRemoveSong(boolean removeSong) {
        isRemoveSong = removeSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getCurrentPositionS() {
        return currentPositionS;
    }

    public String getDurationS() {
        return durationS;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.durationS=formatTime(duration);
        this.duration = duration;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPositionS=formatTime(currentPosition);
        this.currentPosition = currentPosition;
    }

    public String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}
