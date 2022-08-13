package com.example.cloudmusic.request.activity;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.db.SharedPreferencesManager;
import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestLoginViewModel extends ViewModel {
    public MutableLiveData<String> loginState = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRefresh = new MutableLiveData<>();
    public MutableLiveData<String> getCaptchaState = new MutableLiveData<>();
    public MutableLiveData<String> checkCaptchaState = new MutableLiveData<>();
    public MutableLiveData<Boolean> captchaCorrect = new MutableLiveData<>();

//    public void applyUseState(Context context, boolean isLogin) {
//        SharedPreferencesManager.getInstance().applyUseState(context, isLogin, loginState);
//    }

    public void login(String phone,String password, String captcha) {
        HttpRequestManager.getInstance().login(phone,password, captcha, loginState);
    }

    public void login(String phone,String password) {
        HttpRequestManager.getInstance().login(phone, password, loginState);
    }

    public void getCaptcha(String phone) {
        HttpRequestManager.getInstance().captchaSent(phone, getCaptchaState);
    }

    public void checkCaptcha(String phone, String captcha) {
        HttpRequestManager.getInstance().checkCaptcha(phone, captcha, checkCaptchaState, captchaCorrect);
    }

    public void loginRefresh() {
        HttpRequestManager.getInstance().loginRefresh(loginRefresh);
    }
}
