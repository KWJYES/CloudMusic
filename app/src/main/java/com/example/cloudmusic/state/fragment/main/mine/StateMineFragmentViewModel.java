package com.example.cloudmusic.state.fragment.main.mine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateMineFragmentViewModel extends ViewModel {
    public MutableLiveData<String> userName=new MutableLiveData<>();
    public MutableLiveData<String> userLevel=new MutableLiveData<>();
    public MutableLiveData<String> userHearUrl=new MutableLiveData<>();
}
