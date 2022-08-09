package com.example.cloudmusic.response.db;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.base.BaseEntity;
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.entity.SongList;
import com.example.cloudmusic.utils.enums.EntityType;

import java.util.List;

public interface ILitePalRequest {
    <T extends BaseEntity> void save(T entity, EntityType type);

    void getSongList(String listName, MutableLiveData<SongList> songListLD);

    void addSongsToPlayList(List<Song> songs);

    void addSongToPlayList(Song song);

    void removePlayList(Song song);

    List<Song> getSongList(String listName);

    void getHistorySearch(MutableLiveData<List<HistorySearch>> hsList);

    void clearHistorySearch(MutableLiveData<List<HistorySearch>> hsList);

    void addHistorySearch(HistorySearch historySearch);
}
