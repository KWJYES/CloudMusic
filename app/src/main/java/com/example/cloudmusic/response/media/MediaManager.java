package com.example.cloudmusic.response.media;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.db.LitePalManager;
import com.example.cloudmusic.response.network.HttpRequestManager;
import com.example.cloudmusic.sevices.PlayerService;
import com.example.cloudmusic.CloudMusic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MediaManager implements IMediaRequest {

    private static PlayerService.PlayerBinder playerBinder;
    private Song song = new Song("0", "暂无播放", "歌手未知");
    private int duration = 0;
    private int currentTime = 0;
    /**
     * 0：列表循环
     * 1：单曲循环
     * 2：随机播放
     */
    private int playMode = CloudMusic.Loop;

    @Override
    public Song getCurrentSong() {
        return this.song;
    }

    @Override
    public void getDuration(MutableLiveData<Integer> durationLD) {
        durationLD.setValue(duration);
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void getCurrentTime(MutableLiveData<Integer> currentTimeLD) {
        currentTimeLD.setValue(currentTime);
    }

    @Override
    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public void setPlayerBinder(PlayerService.PlayerBinder playerBinder) {
        MediaManager.playerBinder = playerBinder;
    }

    @Override
    public void nextSong(MutableLiveData<Boolean> isPlaying, MutableLiveData<Song> songLD) {
        if (CloudMusic.isGettingSongUrl) return;
        List<Song> songList = getSongList();
        int position = -1;
        for (int i = 0; i < songList.size(); i++) {
            if (this.song.getSongId().equals(songList.get(i).getSongId())) {
                position = i;
                break;
            }
        }
        if (position == -1) return;
        switch (playMode) {
            case CloudMusic.Loop:
                if (position == songList.size() - 1) position = 0;
                else position = position + 1;
                break;
            case CloudMusic.Single_Loop:
                break;
            case CloudMusic.Random:
                Random random = new Random();
                position = random.nextInt(songList.size());
                break;
        }
        Song song = songList.get(position);
        HttpRequestManager.getInstance().getSongUrl(song, song1 -> {
            if(song1==null)return;
            playerBinder.play(song1);
            LitePalManager.getInstance().addSongToPlayList(song1);
            LitePalManager.getInstance().addSongToHistoryList(song1);
            this.song = song1;
            if (isPlaying != null)
                isPlaying.setValue(true);
            if (songLD != null)
                songLD.setValue(this.song);
        });
    }

    @Override
    public void lastSong(MutableLiveData<Boolean> isPlaying, MutableLiveData<Song> songLD) {
        if (CloudMusic.isGettingSongUrl) return;
        List<Song> songList = getSongList();
        int position = -1;
        for (int i = 0; i < songList.size(); i++) {
            if (this.song.getSongId().equals(songList.get(i).getSongId())) {
                position = i;
                break;
            }
        }
        if (position == -1) return;
        switch (playMode) {
            case CloudMusic.Loop:
                if (position == 0) position = songList.size() - 1;
                else position = position - 1;
                break;
            case CloudMusic.Single_Loop:
                break;
            case CloudMusic.Random:
                Random random = new Random();
                position = random.nextInt(songList.size());
                break;
        }
        Song song = songList.get(position);
        HttpRequestManager.getInstance().getSongUrl(song, song1 -> {
            if (song1==null)return;
            playerBinder.play(song1);
            this.song = song1;
            LitePalManager.getInstance().addSongToPlayList(song1);
            LitePalManager.getInstance().addSongToHistoryList(song1);
            if (isPlaying != null)
                isPlaying.setValue(true);
            if (songLD != null)
                songLD.setValue(this.song);
        });
    }

    @Override
    public void playMode(MutableLiveData<Boolean> isPlaying, MutableLiveData<Integer> playModeLD) {
        switch (playMode) {
            case CloudMusic.Loop:
                playMode = CloudMusic.Single_Loop;
                break;
            case CloudMusic.Single_Loop:
                playMode = CloudMusic.Random;
                break;
            case CloudMusic.Random:
                playMode = CloudMusic.Loop;
                break;
        }
        playModeLD.setValue(playMode);
    }

    @Override
    public void getPlayMode(MutableLiveData<Integer> playModeLD) {
        playModeLD.setValue(playMode);
    }

    /**
     * 使用单例模式
     */
    private MediaManager() {
    }

    private static MediaManager mediaManager;

    public static MediaManager getInstance() {
        if (mediaManager == null) {
            synchronized (MediaManager.class) {
                if (null == mediaManager) {
                    mediaManager = new MediaManager();
                }
            }
        }
        return mediaManager;
    }

    public List<Song> getSongList() {
        return LitePalManager.getInstance().getSongList("playList");
    }

    @Override
    public void removeSong(Song song, MutableLiveData<Boolean> isPlaying, MutableLiveData<Song> songLD) {
        if (song.getSongId().equals(this.song.getSongId())) {
            if (isPlaying != null)
                isPlaying.setValue(false);
            this.song = new Song("0", "暂无播放", "歌手未知");
            if (songLD != null)
                songLD.setValue(this.song);
            playerBinder.reset();
        }
        LitePalManager.getInstance().removePlayList(song);
    }

    @Override
    public void play(MutableLiveData<Boolean> isPlaying, MutableLiveData<Song> songLD, Song song) {
        if (playerBinder == null) return;
        if (this.song.getSongId().equals(song.getSongId())) {
            if (songLD != null)
                songLD.setValue(this.song);
            return;
        }
        HttpRequestManager.getInstance().getSongUrl(song, song1 -> {
            if (song1==null)return;
            playerBinder.play(song1);
            this.song = song1;
            LitePalManager.getInstance().addSongToPlayList(song1);
            LitePalManager.getInstance().addSongToHistoryList(song1);
            if (isPlaying != null)
                isPlaying.setValue(true);
            if (songLD != null)
                songLD.setValue(this.song);
        });
    }

    @Override
    public void pause(MutableLiveData<Boolean> isPlaying) {
        if (playerBinder == null) return;
        playerBinder.pause();
        if (isPlaying != null)
            isPlaying.setValue(false);
    }

    @Override
    public void start(MutableLiveData<Boolean> isPlaying) {
        if (playerBinder == null) return;
        playerBinder.start();
        if (isPlaying != null)
            isPlaying.setValue(true);
    }

    @Override
    public void seekToPosition(int position) {
        if (playerBinder == null) return;
        playerBinder.seekToPosition(position);
    }

    @Override
    public void isPlaying(MutableLiveData<Boolean> isPlaying) {
        if (playerBinder == null) isPlaying.setValue(false);
        else isPlaying.setValue(playerBinder.isPlaying());
    }

    @Override
    public boolean isPlaying() {
        if (playerBinder == null) return false;
        return playerBinder.isPlaying();
    }

    @Override
    public void getLocalMusicData(Context context, MutableLiveData<List<Song>> songListLD) {
        int count = 1;
        List<Song> list = new ArrayList<>();
        // 媒体库查询语句
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setSongId("000" + count);
                count = count + 1;
                song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));//歌曲
                song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))); //歌手
                song.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));//路径
                //int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));//时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getName().contains("-")) {
                        String[] str = song.getName().split("-");
                        song.setArtist(str[0].trim());
                        song.setName(str[1].trim());
                    }
                    if (!song.getName().endsWith(".awb")) {//去录音
                        song.setName(song.getName().substring(0, song.getName().length() - 4));
                        if (!song.getName().contains("[mqms2]"))//去铃声
                            list.add(song);
                    }
                }
            }
            cursor.close();// 释放资源
        }
        if (songListLD != null)
            songListLD.setValue(list);
    }

    @Override
    public void formatTime(int time, MutableLiveData<String> timeLD) {
        String min = time / 1000 / 60 + "";
        if (min.length() > 3) return;
        if (time / 1000 % 60 < 10) {
            timeLD.setValue(min + ":0" + time / 1000 % 60);
        } else {
            timeLD.setValue(min + ":" + time / 1000 % 60);
        }
    }

    @Override
    public void getCurrentSong(MutableLiveData<Song> song) {
        song.setValue(this.song);
    }


    @Override
    public void setCurrentSong(Song song, MutableLiveData<Song> songLD) {
        this.song = song;
    }


}
