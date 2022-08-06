package com.example.cloudmusic.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.DialogOnesongMoreOperateBinding;

public class OneSongMoreOperateDialog extends Dialog {

    DialogOnesongMoreOperateBinding binding;

    public OneSongMoreOperateDialog(@NonNull Context context) {
        super(context);
    }

    public OneSongMoreOperateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected OneSongMoreOperateDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_onesong_more_operate, null, false);
        setContentView(binding.getRoot());
        init();
    }

    /**
     * 初始化对话框样式
     * 与显示位置
     */
    private void init() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_shape_musici_list);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.music_list_dialog);//弹出动画
        window.setGravity(Gravity.BOTTOM);//设置显示位置
    }
}
