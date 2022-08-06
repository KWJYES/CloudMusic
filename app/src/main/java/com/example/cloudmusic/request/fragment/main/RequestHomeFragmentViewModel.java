package com.example.cloudmusic.request.fragment.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.response.network.HttpRequestManager;

public class RequestHomeFragmentViewModel extends ViewModel {
    public MutableLiveData<String> defaultSearchWord = new MutableLiveData<>();
    public MutableLiveData<String> defaultSearchWordState = new MutableLiveData<>();

    /**
     * 获取推荐搜索词
     */
    public void requestDefaultSearchWord(){
        HttpRequestManager.getInstance().getDefaultSearchWord(defaultSearchWord,defaultSearchWordState);
    }
}
