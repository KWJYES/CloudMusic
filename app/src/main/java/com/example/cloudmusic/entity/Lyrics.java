package com.example.cloudmusic.entity;

/**
 * 一首歌的歌词
 */
public class Lyrics {
    private String lyrId;
    private String name;
    private String lyrics;
    private String picUrl;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLyrId() {
        return lyrId;
    }

    public void setLyrId(String lyrId) {
        this.lyrId = lyrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
