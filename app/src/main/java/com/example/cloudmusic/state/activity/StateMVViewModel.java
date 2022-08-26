package com.example.cloudmusic.state.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateMVViewModel extends ViewModel {
    public MutableLiveData<String> mvTittle=new MutableLiveData<>();
    public MutableLiveData<String> mvID=new MutableLiveData<>();
    public MutableLiveData<String> content=new MutableLiveData<>();
}
