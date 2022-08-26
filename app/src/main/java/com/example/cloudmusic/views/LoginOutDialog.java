package com.example.cloudmusic.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.DialogLoginoutBinding;
import com.example.cloudmusic.response.network.HttpRequestManager;
import com.example.cloudmusic.utils.ActivityCollector;
import com.example.cloudmusic.utils.callback.LoginOutDialogClickCallback;

public class LoginOutDialog extends Dialog {
    private LoginOutDialogClickCallback finishAppCallback;
    private LoginOutDialogClickCallback loginOutCallback;
    private DialogLoginoutBinding binding;

    public LoginOutDialog(@NonNull Context context,LoginOutDialogClickCallback finishAppCallback,LoginOutDialogClickCallback loginOutCallback) {
        super(context);
        this.finishAppCallback=finishAppCallback;
        this.loginOutCallback=loginOutCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_loginout, null, false);
        setContentView(binding.getRoot());
        initWindow();
        binding.finishApp.setOnClickListener(view -> finishAppCallback.onItemOnClick());
        binding.loginOut.setOnClickListener(view -> loginOutCallback.onItemOnClick());
    }

    /**
     * 初始化对话框样式
     * 与显示位置
     */
    private void initWindow() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_loginout);
        window.setGravity(Gravity.CENTER);//设置显示位置
    }
}
