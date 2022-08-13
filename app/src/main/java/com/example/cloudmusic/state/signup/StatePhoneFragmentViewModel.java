package com.example.cloudmusic.state.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatePhoneFragmentViewModel extends ViewModel {
    public MutableLiveData<Boolean> enable=new MutableLiveData<>();
    public MutableLiveData<String> phone=new MutableLiveData<>();
    public MutableLiveData<Boolean> wait=new MutableLiveData<>();
}
