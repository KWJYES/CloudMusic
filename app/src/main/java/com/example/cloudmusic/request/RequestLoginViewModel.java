package com.example.cloudmusic.request;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.db.SharedPreferencesManager;

public class RequestLoginViewModel extends ViewModel {
    public MutableLiveData<Boolean> loginState=new MutableLiveData<>();

    public void applyUseState(Context context, boolean isLogin){
        SharedPreferencesManager.getInstance().applyUseState(context,isLogin,loginState);
    }
}
