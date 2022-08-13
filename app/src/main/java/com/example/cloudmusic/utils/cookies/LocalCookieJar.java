package com.example.cloudmusic.utils.cookies;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class LocalCookieJar implements CookieJar {
    List<Cookie> cookies;

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        if (cookies != null) {
            Log.d("TAG","loadForRequest url--->"+url.toString());
            Log.d("TAG","loadForRequest Cookie--->"+cookies.toString());
            return cookies;
        }
        return new ArrayList<Cookie>();
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        this.cookies = cookies;
        Log.d("TAG","saveFromResponse url--->"+url.toString());
        Log.d("TAG","saveFromResponse Cookie--->"+cookies.toString());
    }
}
