package com.example.cloudmusic.request.fragment.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.response.network.HttpRequestManager;

import java.util.List;

public class RequestMvFragmentViewModel extends ViewModel {
    public MutableLiveData<List<MV>> mvList=new MutableLiveData<>();
    public MutableLiveData<String> mvRequestState=new MutableLiveData<>();

    public void getMvs(String area,String type,int offset){
        HttpRequestManager.getInstance().getAllMv(area,type,offset,mvList,mvRequestState);
    }
}
