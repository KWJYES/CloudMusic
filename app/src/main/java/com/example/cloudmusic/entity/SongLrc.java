package com.example.cloudmusic.entity;

import com.example.cloudmusic.base.BaseEntity;

/**
 * 一个歌词文件
 */
public class SongLrc extends BaseEntity {
    private String songId;
    private int id;
    private String lrc;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}
