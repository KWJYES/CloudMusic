package com.example.cloudmusic.base;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

abstract public class BaseActivity extends AppCompatActivity {

    /**
     * 初始化DataBinding与ViewModel
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
        initView();
        observerDataStateUpdateAction();
        initSomeData();
    }

    /**
     * 初始化数据，初始化其它操作
     */
    protected void initSomeData(){

    }

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


    /**
     * 设置沉浸式状态栏
     * @param isDark 是否设置为深色主题
     */
    protected void setTransparentStatusBar(boolean isDark) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (isDark)
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//全屏布局|深色状态(黑色字体)
        else
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);//设置透明色
    }

    /**
     * 对Activity做一些初始化工作：
     * 初始化DataBing
     */
    abstract protected void initActivity();
}