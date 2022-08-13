package com.example.cloudmusic.state.signup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateCaptchaFragmentViewModel extends ViewModel {
    public MutableLiveData<String> captcha=new MutableLiveData<>();
    public MutableLiveData<Boolean> correct=new MutableLiveData<>();
    public MutableLiveData<Boolean> wait=new MutableLiveData<>();
    public MutableLiveData<Boolean> captchaBtnEnable=new MutableLiveData<>();
    public MutableLiveData<String> captchaBtnText=new MutableLiveData<>();

}
