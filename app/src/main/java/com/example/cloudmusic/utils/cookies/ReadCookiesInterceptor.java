package com.example.cloudmusic.utils.cookies;


import android.util.Log;

import com.example.cloudmusic.response.db.SharedPreferencesManager;
import com.example.cloudmusic.utils.CloudMusic;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class ReadCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HttpUrl httpUrl=chain.request().url();
        String urlString = httpUrl.toString();
        Log.d("TAG","request to url:"+urlString);
        if(urlString.equals(CloudMusic.baseUrl+"login/status")||
                urlString.startsWith(CloudMusic.baseUrl + "login/cellphone")||
                urlString.equals(CloudMusic.baseUrl+"login/refresh")||
                urlString.equals(CloudMusic.baseUrl+"logout")) {
            HashSet<String> preferences = (HashSet<String>) SharedPreferencesManager.getInstance().getCookieSet();
            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);
                Log.d("Cookie", "OkHttp And Cookie: " + cookie);
            }
        }
        return chain.proceed(builder.build());
    }
}
