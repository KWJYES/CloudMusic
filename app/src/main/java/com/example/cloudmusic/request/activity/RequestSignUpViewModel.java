package com.example.cloudmusic.request.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestSignUpViewModel extends ViewModel {
    public MutableLiveData<String> signUpState=new MutableLiveData<>();

    public void signUp(String phone,String password,String nickname,String captcha){
        HttpRequestManager.getInstance().signUp(phone,captcha,nickname,password,signUpState);
    }

}
