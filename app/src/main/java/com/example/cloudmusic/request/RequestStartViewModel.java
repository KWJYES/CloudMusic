package com.example.cloudmusic.request;



import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.db.SharedPreferencesManager;

public class RequestStartViewModel extends ViewModel {
    public MutableLiveData<Boolean> useState=new MutableLiveData<>();


    public void getUseState(Context context){
        SharedPreferencesManager.getInstance().getUseState(context,useState);
    }
}
