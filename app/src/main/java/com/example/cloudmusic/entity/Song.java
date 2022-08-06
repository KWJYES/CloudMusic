package com.example.cloudmusic.entity;

import com.example.cloudmusic.base.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song extends BaseEntity implements Serializable {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private String songId;
    private String name;//歌名
    private String url;//歌曲播放url
    private String picUrl;//歌曲图片
    private String type;//文件格式
    private String level;//音质
    private String artist;//作者
    private String artistId;//作者ID
    private String alias;//歌曲别名，备注
    private String album;//专辑
    private String albumId;//专辑ID

    private List<SongList> songListList=new ArrayList<>();

    public List<SongList> getSongListList() {
        return songListList;
    }

    public void setSongListList(List<SongList> songListList) {
        this.songListList = songListList;
    }

    public Song() {
    }

    public Song(String songId, String name, String artist) {
        this.songId = songId;
        this.name = name;
        this.artist = artist;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
