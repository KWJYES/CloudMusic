package com.example.cloudmusic.utils.cookies;

import android.util.Log;

import com.example.cloudmusic.response.db.SharedPreferencesManager;
import com.example.cloudmusic.CloudMusic;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SaveCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        HttpUrl httpUrl = request.url();
        String urlString = httpUrl.toString();
        Log.d("Cookie", "response from url:" + urlString);
        Log.d("TAG", "response from url:" + urlString);
        if (urlString.startsWith(CloudMusic.baseUrl + "login/cellphone") ||
                urlString.equals(CloudMusic.baseUrl + "login/refresh") ||
                urlString.equals(CloudMusic.baseUrl + "logout")) {
            Log.d("Cookie", "保存CloudMusic.baseUrl" + "login的Cookie");
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();
                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                    Log.d("Cookie", "login OkHttp save LoginCookie: " + cookies);
                }
                SharedPreferencesManager.getInstance().saveCookieSet(cookies);
            }else {
                Log.d("Cookie", "login OkHttp save LoginCookie: NULL" );
            }
        }
        if (urlString.startsWith(CloudMusic.baseUrl + "login/status")) {
            Log.d("Cookie", "保存CloudMusic.baseUrl" + "login/status的Cookie");
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();
                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                    Log.d("Cookie", "login/status OkHttp save LoginCookie: " + cookies);
                }
                SharedPreferencesManager.getInstance().saveCookieNullSet(cookies);
            }
        }
        return originalResponse;
    }
}