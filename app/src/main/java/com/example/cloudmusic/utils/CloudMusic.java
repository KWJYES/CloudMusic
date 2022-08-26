package com.example.cloudmusic.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.cloudmusic.entity.Artist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 一些配置信息
 */
public class CloudMusic {
    //public static final String baseUrl="https://netease-cloud-music-api-beta-inky-62.vercel.app/";
    public static final String baseUrl="http://cloud-music.pl-fe.cn/";
    public static final String SUCCEED="succeed";
    public static final String FAILURE="failure";
    public static final String LOGIN_TYPE="nickname_repeat";//从哪启动的登陆面
    public static final String LOGIN_INSIDE="LOGIN_INSIDE";//从哪启动的登陆面
    public static final String LOGIN_START="LOGIN_START";//从哪启动的登陆面
    public static final int Loop=0;
    public static final int Single_Loop =1;
    public static final int Random=2;

    public static boolean isGettingSongUrl = false;//防止操作过快
    public static boolean isStartPlayerActivity = false;//防止二次启动PlayerActivity
    public static boolean isStartMusicListDialog = false;//防止二次启动MusicListDialog
    public static boolean isReset = false;//是否正在释放MediaPlayer
    public static boolean isLiking = false;//是否正在喜欢/取消喜欢
    public static boolean isLogin = true;//是否已登陆
    public static boolean isSongFragmentEventBusRegister = false;//是否打开PlayerActivity

    @SuppressLint("StaticFieldLeak")
    public static Context mainActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context loginActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context signupActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context startActivityContext;

    public static Set<String> likeSongIdSet =new HashSet<>();
    public static Set<String> likeArtistIdSet =new HashSet<>();
    public static Set<Artist> likeArtistSet =new HashSet<>();

    public static String phone;
    public static String userId="0";
    public static String nickname="立即登陆";
    public static String level="1";
    public static int lrcOffset=0;//歌词偏移
    public static int requestUrlTime=0;//网络重试次数
    public static String avatarUrl;

    public static Context getContext(){
        if(mainActivityContext!=null) return mainActivityContext;
        else if(loginActivityContext!=null) return loginActivityContext;
        else if(signupActivityContext!=null) return signupActivityContext;
        else if(startActivityContext!=null) return startActivityContext;
        else return null;
    }

}
