package com.example.cloudmusic.state.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateSignUpViewModel extends ViewModel {
    public MutableLiveData<String> phone=new MutableLiveData<>();
    public MutableLiveData<String> captcha=new MutableLiveData<>();
    public MutableLiveData<String> password=new MutableLiveData<>();
    public MutableLiveData<String> nickname=new MutableLiveData<>();
}
