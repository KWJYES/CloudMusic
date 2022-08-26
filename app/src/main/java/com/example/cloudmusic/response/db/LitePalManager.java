package com.example.cloudmusic.response.db;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.base.BaseEntity;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongList;
import com.example.cloudmusic.entity.SongLrc;
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
                Song s = (Song) entity;
                List<Song> songs = LitePal.where("songId = ? and name = ?", s.getSongId(), s.getName()).find(Song.class, true);
                if (songs.size() == 0) {
                    s.save();
                }
                break;
            case HistorySearchWords:
                HistorySearch hs = (HistorySearch) entity;
                List<HistorySearch> historySearches = LitePal.where("keywords = ?", hs.getKeywords()).find(HistorySearch.class);
                if (historySearches.size() == 0) {
                    hs.save();
                }
                break;
        }
    }

    @Override
    public void getSongList(String listName, MutableLiveData<SongList> songListLD) {
        SongList songList = LitePal.where("name = ?", listName).findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName(listName);
            songList.save();
        }
        songListLD.setValue(songList);
    }

    @Override
    public void addSongsToPlayList(List<Song> songs) {
        for (Song s : songs) {
            addSongToPlayList(s);
        }
    }

    @Override
    public void addSongsToHistoryList(List<Song> songs) {
        for(Song song:songs)
            addSongToHistoryList(song);
    }

    @Override
    public void addSongToPlayList(Song song) {
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName("playList");
        } else {
            for(Song s:songList.getSongList()){
                if(s.getSongId().equals(song.getSongId())){
                    if (!s.getSongId().startsWith("000")) {//网络
                        if (!song.getUrlStartTime().equals(s.getUrlStartTime())) {
                            song.updateAll("songId = ? and name = ?", song.getSongId(), song.getName());
                            Log.d("TAG","《"+song.getName()+"》url已更新："+song.getUrlStartTime());
                        }
                    }  //本地0
                    return;
                }
            }
        }
        song.save();
        songList.getSongList().add(song);
        songList.save();
    }

    @Override
    public void addSongToHistoryList(Song song) {
        SongList songList=LitePal.where("name = ?","historyList").findFirst(SongList.class,true);
        if(songList==null){
            songList=new SongList();
            songList.setName("historyList");
        }else{
            for(Song s:songList.getSongList()){
                if(s.getSongId().equals(song.getSongId())){
                    songList.getSongList().removeIf(s1 -> s1.getSongId().equals(song.getSongId()));
                    LitePal.deleteAll(Song.class,"songId = ? and songlist_id = ?",s.getSongId(),songList.getId()+"");
                    break;
                }
            }
        }
        Song song1=new Song();
        song1.setSongId(song.getSongId());
        song1.setAlias(song.getAlias());
        song1.setArtist(song.getArtist());
        song1.setName(song.getName());
        song1.setArtistId(song.getArtistId());
        song1.setUrl(song.getUrl());
        song1.setPicUrl(song.getPicUrl());
        song1.setUrlStartTime(song.getUrlStartTime());
        song1.setAlbum(song.getAlbum());
        song1.setAlbumId(song.getAlbumId());
        song1.save();
        songList.getSongList().add(song1);
        songList.save();
    }

    @Override
    public void removePlayList(Song song) {
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        for (Song song1:songList.getSongList()){
            if(song1.getSongId().equals(song.getSongId()))
                LitePal.deleteAll(Song.class,"songId = ? and songlist_id = ?",song1.getSongId(),songList.getId()+"");
        }
        songList.getSongList().removeIf(s -> s.getSongId().equals(song.getSongId()));
        songList.save();
        //LitePal.deleteAll(Song.class, "songId = ? and name = ?", song.getSongId(), song.getName());
    }

    @Override
    public void removeAllPlayList() {
        SongList songList = LitePal.where("name = ?", "playList").findFirst(SongList.class, true);
        for(Song song:songList.getSongList()){
            removePlayList(song);
        }
    }

    @Override
    public List<Song> getSongList(String listName) {
        SongList songList = LitePal.where("name = ?", listName).findFirst(SongList.class, true);
        if (songList == null) {
            songList = new SongList();
            songList.setName(listName);
            songList.save();
        }
        return songList.getSongList();
    }

    @Override
    public void getHistorySearch(MutableLiveData<List<HistorySearch>> hsList) {
        List<HistorySearch> historySearches = LitePal.findAll(HistorySearch.class);
        hsList.setValue(historySearches);
    }

    @Override
    public void clearHistorySearch(MutableLiveData<List<HistorySearch>> hsList) {
        LitePal.deleteAll(HistorySearch.class);
        List<HistorySearch> historySearches = LitePal.findAll(HistorySearch.class);
        hsList.setValue(historySearches);
    }

    @Override
    public void addHistorySearch(HistorySearch historySearch) {
        List<HistorySearch> historySearches = LitePal.where("keywords = ?", historySearch.getKeywords()).find(HistorySearch.class);
        if (historySearches.size() != 0) {
            LitePal.deleteAll(HistorySearch.class, "keywords = ?", historySearch.getKeywords());
        }
        historySearch.save();
    }

    @Override
    public List<SongLrc> searchSongLrc(String songId) {
        return LitePal.where("songId = ?",songId).find(SongLrc.class);
    }
}
