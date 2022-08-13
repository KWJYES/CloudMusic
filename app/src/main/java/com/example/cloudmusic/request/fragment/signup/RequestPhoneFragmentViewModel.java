package com.example.cloudmusic.request.fragment.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestPhoneFragmentViewModel extends ViewModel {
    public MutableLiveData<Boolean> enable=new MutableLiveData<>();
    public MutableLiveData<String> requestState=new MutableLiveData<>();

    public void checkPhone(String phone){
        HttpRequestManager.getInstance().checkPhone(phone,enable,requestState);
    }
}
