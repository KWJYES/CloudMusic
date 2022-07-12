package com.example.cloudmusic.state;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateLoginViewModel extends ViewModel {
        public MutableLiveData<String> loginState=new MutableLiveData<>();
        public MutableLiveData<String> account=new MutableLiveData<>();
        public MutableLiveData<String> password=new MutableLiveData<>();
}