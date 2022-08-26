package com.example.cloudmusic.state.fragment.main.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StateArtistAllFragmentViewModel extends ViewModel {
    public MutableLiveData<String> firstSelect=new MutableLiveData<>();
    public MutableLiveData<String> secondSelect=new MutableLiveData<>();
}
