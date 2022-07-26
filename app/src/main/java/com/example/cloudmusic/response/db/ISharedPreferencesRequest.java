package com.example.cloudmusic.response.db;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

public interface ISharedPreferencesRequest {
    void getUseState(Context context,MutableLiveData<Boolean> useState);
    void applyUseState(Context context, boolean isLogin, MutableLiveData<Boolean> loginState);
    String getAccount(Context context);
    void applyAccount(Context context,String account);
}
