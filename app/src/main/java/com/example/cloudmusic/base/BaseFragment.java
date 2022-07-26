package com.example.cloudmusic.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

abstract public class BaseFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=initFragment(inflater,container, savedInstanceState);
        initView();
        observerDataStateUpdateAction();
        initSomeData();
        return view;
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
