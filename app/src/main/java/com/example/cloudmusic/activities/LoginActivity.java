package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityLoginBinding;
import com.example.cloudmusic.request.activity.RequestLoginViewModel;
import com.example.cloudmusic.state.activity.StateLoginViewModel;


public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private StateLoginViewModel svm;
    private RequestLoginViewModel rvm;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateLoginViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestLoginViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);//数据绑定

    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.loginState.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void initView() {

    }

    public class ClickClass {
        /**
         * 登陆
         *
         * @param view
         */
        public void login(View view) {

        }

        /**
         * 注册
         *
         * @param view
         */
        public void signup(View view) {

        }

        /**
         * 立即体验
         *
         * @param view
         */
        public void going(View view) {
            Log.d("TAG", "going");
            rvm.applyUseState(LoginActivity.this,true);
        }

        /**
         * 获取验证码
         *
         * @param view
         */
        public void getVerificationCode(View view) {

        }
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG","LoginActivity onDestroy...");
        super.onDestroy();
    }
}