package com.example.cloudmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityStartBinding;
import com.example.cloudmusic.request.RequestLoginViewModel;
import com.example.cloudmusic.request.RequestStartViewModel;

public class StartActivity extends BaseActivity {

    ActivityStartBinding binding;
    RequestStartViewModel rvm;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestStartViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initSomeData() {
        countDownTimer.start();
    }

    private void getUseState() {
        rvm.getUseState(StartActivity.this);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.useState.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    startActivity(new Intent(StartActivity.this,MainActivity.class));
                }else {
                    startActivity(new Intent(StartActivity.this,LoginActivity.class));
                }
                finish();
            }
        });
    }

    /**
     * 定时器
     */
    private final CountDownTimer countDownTimer=new CountDownTimer(2000,2000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            getUseState();
            countDownTimer.cancel();
        }
    };


    @Override
    protected void onDestroy() {
        Log.d("TAG","StartActivity onDestroy...");
        super.onDestroy();
    }
}