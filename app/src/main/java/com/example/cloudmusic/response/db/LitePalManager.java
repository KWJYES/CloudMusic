package com.example.cloudmusic.response.db;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.base.BaseEntity;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongList;
import com.example.cloudmusic.utils.enums.EntityType;

import org.litepal.LitePal;

import java.util.List;

public class LitePalManager implements ILitePalRequest {

    private LitePalManager() {
    }

    private static LitePalManager litePalManager;

    public static LitePalManager getInstance() {
        if (litePalManager == null) {
            synchronized (LitePalManager.class) {
                if (null == litePalManager) {
                    litePalManager = new LitePalManager();
                }
            }
        }
        return litePalManager;
    }

    @Override
    public <T extends BaseEntity> void save(T entity, EntityType type) {
        switch (type) {
            case Song:
                Song t = (Song) entity;
                List<Song> song = LitePal.where("songId = ? and name = ?", t.getSongId(), t.getName()).find(Song.class, true);
                if (song.size() == 0) {
                    t.save();
                }
                break;
        }
    }

    @Override
    public void getSongList(String listName, MutableLiveData<SongList> songListLD) {
        SongList songList = LitePal.where("name = ?", listName).findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName("playList");
            songList.save();
        }
        songListLD.setValue(songList);
    }

    @Override
    public void addSongsToPlayList(List<Song> songs) {
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName("playList");
            mark:for (Song song : songs) {
                List<Song> t = LitePal.where("songId = ? and name = ?", song.getSongId(), song.getName()).find(Song.class, true);
                if(t.size()!=0) {
                    song = t.get(0);
                    for (SongList songlist:song.getSongListList()){
                        if(songlist.getName().equals("playList")) continue mark;
                    }
                }else {
                    song.save();
                }
                songList.getSongList().add(song);
            }
            songList.save();
        } else {
            for (Song song : songs) {
                if (!songList.getSongList().contains(song)) {
                    List<Song> t = LitePal.where("songId = ? and name = ?", song.getSongId(), song.getName()).find(Song.class, true);
                    if(t.size()!=0) {
                        song = t.get(0);
                    }else {
                        song.save();
                    }
                    songList.getSongList().add(song);
                }
            }
            songList.save();
        }
    }

    @Override
    public void addSongsToPlayList(Song song) {
        List<Song> t = LitePal.where("songId = ? and name = ?", song.getSongId(), song.getName()).find(Song.class, true);
        if(t.size()!=0) {
            song=t.get(0);
            for (SongList songList:song.getSongListList()){
                if(songList.getName().equals("playList")) return;
            }
        }else {
            song.save();
        }
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName("playList");
            songList.getSongList().add(song);
            songList.save();
        } else {
            if (!songList.getSongList().contains(song))
                songList.getSongList().add(song);
            songList.save();
        }
    }

    @Override
    public void removePlayList(Song song) {
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        songList.getSongList().remove(song);
        songList.save();
        // TODO:暂时测试用
        LitePal.deleteAll(Song.class, "songId = ? and name = ?", song.getSongId(), song.getName());
    }

    @Override
    public List<Song> getSongList(String listName) {
        SongList songList = LitePal.where("name = ?", listName).findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName("playList");
            songList.save();
        }
        return songList.getSongList();
    }
}
