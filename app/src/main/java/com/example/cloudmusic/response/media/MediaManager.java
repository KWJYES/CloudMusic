package com.example.cloudmusic.response.media;


import com.example.cloudmusic.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaManager {
    /**
     * 使用单例模式
     */
    private MediaManager() {}
    private static MediaManager mediaManager;
    public static MediaManager getInstance(){
        if(mediaManager==null){
            synchronized (MediaManager.class){
                if(null==mediaManager){
                    mediaManager=new MediaManager();
                }
            }
        }
        return mediaManager;
    }

    /**
     * 对放列表使用单列模式
     */
    private static List<Song> songList;
    public List<Song> getSongList(){
        if(songList==null){
            synchronized (Song.class){
                if(null==songList){
                    songList=new ArrayList<>();
                }
            }
        }
        return songList;
    }

    /**
     * 添加多首音乐
     * @param songs
     */
    public void addSongs(List<Song> songs){
        if(songList==null) return;
        songList.addAll(songs);
    }

    /**
     * 添加一首音乐
     * @param song
     */
    public void addSong(Song song){
        if(songList==null) return;
        songList.add(song);
    }
}
