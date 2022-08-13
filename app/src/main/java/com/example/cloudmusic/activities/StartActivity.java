package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityStartBinding;
import com.example.cloudmusic.request.activity.RequestStartViewModel;
import com.example.cloudmusic.utils.CloudMusic;

public class StartActivity extends BaseActivity {

    ActivityStartBinding binding;
    RequestStartViewModel rvm;

    @Override
    protected void initActivity() {
        CloudMusic.startActivityContext=this;
        setTransparentStatusBar(false);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestStartViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initSomeData() {

    }

    @Override
    protected void getInternetData() {
        //rvm.loginRefresh();
        rvm.getUseState();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.isLoginRequestState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals(CloudMusic.FAILURE)){
                    Log.d("TAG","获取登陆状态失败");
                    startActivity(new Intent(StartActivity.this,LoginActivity.class));
                }
            }
        });
        rvm.isLogin.observe(this, aBoolean -> {
            Log.d("TAG","获取登陆状态成功");
            if(aBoolean){
                startActivity(new Intent(StartActivity.this,MainActivity.class));
            }else {
                startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
            finish();
        });
        rvm.loginRefresh.observe(this, aBoolean -> {
            if(!aBoolean){
                Log.d("TAG","登陆状态刷新失败");
                Toast.makeText(StartActivity.this, "登陆状态刷新失败", Toast.LENGTH_SHORT).show();
            }
            else Log.d("TAG","登陆状态已刷新");
            rvm.getUseState();
        });
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG","StartActivity onDestroy...");
        super.onDestroy();
    }
}