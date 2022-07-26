package com.example.cloudmusic.sevices;

import com.example.cloudmusic.entity.Song;

public interface IPlay {
    void play(Song song);//播放
    void pause();//暂停
    void start();//继续
    void seekToPosition(int position);//跳到相应位置
    boolean isPlaying();
    void reset();
}
