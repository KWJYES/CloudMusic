package com.example.cloudmusic.state.fragment.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.MV;

import java.util.List;

public class StateAllMVFragmentViewModel extends ViewModel {
    public MutableLiveData<String> firstSelect=new MutableLiveData<>();
    public MutableLiveData<String> secondSelect=new MutableLiveData<>();
}
