package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityStartBinding;
import com.example.cloudmusic.request.activity.RequestStartViewModel;
import com.example.cloudmusic.CloudMusic;

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

    }

    @Override
    protected void getInternetData() {
        rvm.getUseState();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.isLoginRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Log.d("TAG", "获取登陆状态失败");
                Toast toast = Toast.makeText(StartActivity.this, "\n网络不给力\n请退出重试\n", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
//                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
//                intent.putExtra(CloudMusic.LOGIN_TYPE, CloudMusic.LOGIN_START);
//                startActivity(intent);
//                finish();
            }
        });
        rvm.isLogin.observe(this, aBoolean -> {
            Log.d("TAG", "获取登陆状态成功:"+aBoolean);
            if (aBoolean) {
                CloudMusic.isLogin=true;
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            } else {
                CloudMusic.isLogin=false;
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                intent.putExtra(CloudMusic.LOGIN_TYPE,CloudMusic.LOGIN_START);
                startActivity(intent);
            }
            finish();
        });
        rvm.loginRefresh.observe(this, aBoolean -> {
            if (!aBoolean) {
                Log.d("TAG", "登陆状态刷新失败");
                Toast.makeText(StartActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("TAG", "登陆状态已刷新");

                //finish();
            }
            //rvm.getUseState();
        });
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG", "StartActivity onDestroy...");
        super.onDestroy();
    }
}