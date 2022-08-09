package com.example.cloudmusic.response.db;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.base.BaseEntity;
import com.example.cloudmusic.entity.HistorySearch;
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
            songList.setName("playList");
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
    public void addSongToPlayList(Song song) {
        List<Song> t = LitePal.where("songId = ? and name = ?", song.getSongId(), song.getName()).find(Song.class, true);
        if (t.size() != 0) {
            for(int i=0;i<t.size();i++){
                if (t.get(i).getSongId().startsWith("000")) {//本地0
                    song = t.get(i);
                    if (song.getSongList().getName().equals("playList")) return;
                } else {//网络
                    if (song.getUrlStartTime().equals(t.get(i).getUrlStartTime())) {
                        song = t.get(i);
                        if (song.getSongList().getName().equals("playList")) return;
                    } else {
                        song.updateAll("songId = ? and name = ?", song.getSongId(), song.getName());
                        List<Song> t2 = LitePal.where("songId = ? and name = ?", song.getSongId(), song.getName()).find(Song.class, true);
                        song = t2.get(i);
                        if (song.getSongList().getName().equals("playList")) return;
                    }
                }
            }
        } else {
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
        for (Song song1:songList.getSongList()){
            if(song1.getSongId().equals(song.getSongId()))
                LitePal.delete(Song.class,song1.getId());
        }
        songList.getSongList().removeIf(s -> s.getSongId().equals(song.getSongId()));
        songList.save();
        //LitePal.deleteAll(Song.class, "songId = ? and name = ?", song.getSongId(), song.getName());
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
        if (historySearches.size() == 0) {
            historySearch.save();
        }

    }
}
