package com.example.cloudmusic.state.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateMusicListViewModel extends ViewModel {
    public MutableLiveData<String> dec=new MutableLiveData<>();
    public MutableLiveData<String> name=new MutableLiveData<>();
}
