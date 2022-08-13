package com.example.cloudmusic.request.fragment.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestCaptchaFragmentViewModel extends ViewModel {
    public MutableLiveData<Boolean> correct=new MutableLiveData<>();
    public MutableLiveData<String> checkRequestState=new MutableLiveData<>();
    public MutableLiveData<String> sentRequestState =new MutableLiveData<>();

    public void checkCaptcha(String phone,String captcha){
        HttpRequestManager.getInstance().checkCaptcha(phone,captcha,checkRequestState,correct);
    }

    public void getCaptcha(String phone){
        HttpRequestManager.getInstance().captchaSent(phone,sentRequestState);
    }
}
