package com.example.cloudmusic.state.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateNicknameFragmentViewModel extends ViewModel {
    public MutableLiveData<String> nickname=new MutableLiveData<>();
    public MutableLiveData<Boolean> enable=new MutableLiveData<>();
    public MutableLiveData<Boolean> wait=new MutableLiveData<>();
}
