package com.example.cloudmusic.state.activity;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateLoginViewModel extends ViewModel {
        public MutableLiveData<String> state=new MutableLiveData<>();
        public MutableLiveData<String> account=new MutableLiveData<>();
        public MutableLiveData<String> password=new MutableLiveData<>();
        public MutableLiveData<String> captcha=new MutableLiveData<>();
        public MutableLiveData<String> captchaTimeText=new MutableLiveData<>();
        public MutableLiveData<Boolean> wait=new MutableLiveData<>();
}