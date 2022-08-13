package com.example.cloudmusic.request.fragment.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestNicknameFragmentViewModel extends ViewModel {
    public MutableLiveData<String> requestState=new MutableLiveData<>();
    public MutableLiveData<String> candidateNickname=new MutableLiveData<>();
    public MutableLiveData<Boolean> enable=new MutableLiveData<>();

    public void checkNickname(String nickName){
        HttpRequestManager.getInstance().checkNickname(nickName,enable,requestState,candidateNickname);
    }
}
