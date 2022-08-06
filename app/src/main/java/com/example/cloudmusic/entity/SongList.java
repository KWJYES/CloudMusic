package com.example.cloudmusic.entity;

import com.example.cloudmusic.base.BaseEntity;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐列表
 */
public class SongList extends BaseEntity {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private String name;
    private List<Song> songList=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
