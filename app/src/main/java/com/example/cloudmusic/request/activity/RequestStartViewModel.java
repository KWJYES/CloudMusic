package com.example.cloudmusic.request.activity;



import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.db.SharedPreferencesManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestStartViewModel extends ViewModel {
    public MutableLiveData<Boolean> isLogin =new MutableLiveData<>();
    public MutableLiveData<String> isLoginRequestState =new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRefresh = new MutableLiveData<>();


    public void getUseState(){
        HttpRequestManager.getInstance().getLoginState(isLogin,isLoginRequestState);
    }

    public void loginRefresh() {
        HttpRequestManager.getInstance().loginRefresh(loginRefresh);
    }
}
