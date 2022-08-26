package com.example.cloudmusic.request.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.response.network.HttpRequestManager;
import com.example.cloudmusic.response.permission.PermissionManager;

import java.util.List;

public class RequestMainViewModel extends ViewModel {
    public MutableLiveData<List<Song>> songListLD=new MutableLiveData<>();
    public MutableLiveData<Boolean> isPlaying=new MutableLiveData<>();
    public MutableLiveData<Song> song=new MutableLiveData<>();
    public MutableLiveData<List<String>> likeIdList=new MutableLiveData<>();
    public MutableLiveData<String> likeIdListRequestState=new MutableLiveData<>();
    public MutableLiveData<List<Artist>> artistList=new MutableLiveData<>();
    public MutableLiveData<String> artistListRequestState=new MutableLiveData<>();


    public void getLikeArtistList(){
        HttpRequestManager.getInstance().getLikeArtist(artistList,artistListRequestState);
    }

    public void getLikeIdList(String uid){
        HttpRequestManager.getInstance().getLikeSongIdList(uid,likeIdList,likeIdListRequestState);
    }

    public void getLevel(){
        HttpRequestManager.getInstance().getUserLevel();
    }

    /**
     * 下一曲
     */
    public void nextSong(){
        MediaManager.getInstance().nextSong(isPlaying,song);
    }

    /**
     * 上一曲
     */
    public void lastSong(){
        MediaManager.getInstance().lastSong(isPlaying,song);
    }


    public void remove(Song s){
        MediaManager.getInstance().removeSong(s,isPlaying,song);
    }


    public void getCurrentSong(){
        MediaManager.getInstance().getCurrentSong(song);
    }

    /**
     * 初始化开始暂停按键UI
     */
    public void updatePlayBtn(){
        MediaManager.getInstance().isPlaying(isPlaying);
    }

    public void play(Song s){
        MediaManager.getInstance().play(isPlaying,song,s);
    }

    public void start(){
        MediaManager.getInstance().start(isPlaying);
    }

    public void pause(){
        MediaManager.getInstance().pause(isPlaying);
    }

    /**
     * 从数据库中拿到播放列表
     */
    public void getSongList(){

    }

    /**
     * 保存播放列表到数据库
     * @param songList
     */
    public void saveSongList(List<Song> songList){

    }

    public void requestPermission(Activity activity, Context context){
        PermissionManager.getInstance().checkPermission(activity,context, Manifest.permission.WRITE_EXTERNAL_STORAGE,0);
        PermissionManager.getInstance().checkPermission(activity,context, Manifest.permission.READ_EXTERNAL_STORAGE,1);
    }

    /**
     * 查找本地音乐
     */
    public void getLocalMusic(Context context) {
        MediaManager.getInstance().getLocalMusicData(context,null);
    }


    public void saveDuration(int duration){
        MediaManager.getInstance().setDuration(duration);
    }

    public void saveCurrentTime(int currentTime){
        MediaManager.getInstance().setCurrentTime(currentTime);
    }

//    public void getSongUrl(Song s){
//        HttpRequestManager.getInstance().getSongUrl(s,songPlay);
//    }
}
