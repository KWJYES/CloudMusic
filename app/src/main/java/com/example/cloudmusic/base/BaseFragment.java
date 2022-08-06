package com.example.cloudmusic.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

abstract public class BaseFragment extends Fragment {

    protected boolean isVisited =false;//是否可见过
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=initFragment(inflater,container, savedInstanceState);
        initView();
        observerDataStateUpdateAction();
        initSomeData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        internetData();
    }

    /**
     * 已请求过，则不再次请求
     */
    private void internetData(){
        if(isVisited) return;
        isVisited=true;
        getInternetData();
    }

    /**
     * 请求网络数据
     */
    protected  void getInternetData(){

    }

    /**
     * 初始化数据，初始化其它操作
     */
    protected void initSomeData(){

    }

    protected abstract View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 为视图设置初值
     */
    protected void initView(){

    }

    /**
     * 监测数据变化
     */
    protected void observerDataStateUpdateAction(){

    }
}
