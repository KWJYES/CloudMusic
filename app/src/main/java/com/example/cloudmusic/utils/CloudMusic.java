package com.example.cloudmusic.utils;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 一些配置信息
 */
public class CloudMusic {
    public static final String baseUrl="https://netease-cloud-music-api-beta-inky-62.vercel.app/";
    public static final String SUCCEED="succeed";
    public static final String FAILURE="failure";
    public static final String PHONE_OK="phone_ok";//手机号可用
    public static final String PHONE_SIGNED_UP="phone_signed_up";//手机号已注册
    public static final String INPUT_ERROR="input_error";//输入格式错误
    public static final String NICKNAME_OK="nickname_ok";//名称可用
    public static final String NICKNAME_REPEAT="nickname_repeat";//名称已存在
    public static final int Loop=0;
    public static final int Single_Loop =1;
    public static final int Random=2;

    public static boolean isGettingSongUrl = false;//防止操作过快
    public static boolean isStartPlayerActivity = false;//防止二次启动PlayerActivity
    public static boolean isStartMusicListDialog = false;//防止二次启动MusicListDialog

    @SuppressLint("StaticFieldLeak")
    public static Context mainActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context loginActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context signupActivityContext;
    @SuppressLint("StaticFieldLeak")
    public static Context startActivityContext;

    public static String phone;
    public static String userId;

    public static Context getContext(){
        if(mainActivityContext!=null) return mainActivityContext;
        else if(loginActivityContext!=null) return loginActivityContext;
        else if(signupActivityContext!=null) return signupActivityContext;
        else if(startActivityContext!=null) return startActivityContext;
        else return null;
    }

}
