package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityLoginBinding;
import com.example.cloudmusic.request.RequestInternetViewModel;
import com.example.cloudmusic.state.StateLoginViewModel;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private StateLoginViewModel stateLoginViewModel;
    private RequestInternetViewModel requestInternetViewModel;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        stateLoginViewModel=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(StateLoginViewModel.class);
        requestInternetViewModel =new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(RequestInternetViewModel.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.setClick(new ClickClass());
        binding.setSvm(stateLoginViewModel);
        binding.setLifecycleOwner(this);//数据绑定



    }

    public class ClickClass {
        public void login(View view){

        }
        public void signup(View view){

        }
        public void going(View view){
            Log.d("TAG","going");
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}