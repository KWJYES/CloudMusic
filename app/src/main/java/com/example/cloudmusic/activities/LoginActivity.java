package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityLoginBinding;
import com.example.cloudmusic.request.activity.RequestLoginViewModel;
import com.example.cloudmusic.state.activity.StateLoginViewModel;
import com.example.cloudmusic.utils.ActivityCollector;
import com.example.cloudmusic.CloudMusic;


public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private StateLoginViewModel svm;
    private RequestLoginViewModel rvm;
    private String loginType;

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
        rvm.loginState.observe(this, s -> {
            if (s.equals(CloudMusic.SUCCEED)) {
                CloudMusic.phone = svm.account.getValue();
                if(loginType.equals(CloudMusic.LOGIN_INSIDE)){
                    ActivityCollector.finishActivity(MainActivity.class);
                }
                CloudMusic.isLogin=true;
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                //rvm.loginRefresh();
            } else {
                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
            }
            binding.loginButton.setEnabled(true);
            binding.loginButton.setText("登   陆");
        });
        rvm.getCaptchaState.observe(this, s -> {
            if (s.equals(CloudMusic.SUCCEED)) {
                Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LoginActivity.this, "请求验证码失败", Toast.LENGTH_SHORT).show();
            }
        });
        rvm.captchaCorrect.observe(this, aBoolean -> {
            if(aBoolean){
                rvm.login(svm.account.getValue(),"password",svm.captcha.getValue());
            }else {
                svm.captcha.setValue("");
                Toast.makeText(LoginActivity.this, "验证码错误!", Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText("登   陆");
            }
        });
        rvm.checkCaptchaState.observe(this, s -> {
            if(s.equals(CloudMusic.FAILURE)){
                Toast.makeText(LoginActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText("登   陆");
            }
        });
        rvm.loginRefresh.observe(this, aBoolean -> {
            if(aBoolean)
                Log.d("TAG","登陆状态刷新失败");
            else Log.d("TAG","登陆状态已刷新");
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
        });
    }

    @Override
    protected void initView() {
        svm.captchaTimeText.setValue("点击获取验证码");
        loginType=getIntent().getStringExtra(CloudMusic.LOGIN_TYPE);
        if(loginType.equals(CloudMusic.LOGIN_INSIDE))binding.goingTV.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CloudMusic.phone != null) svm.account.setValue(CloudMusic.phone);
    }

    public class ClickClass {
        /**
         * 登陆
         * @param view
         */
        public void login(View view) {
//            if (svm.account.getValue() != null && svm.account.getValue().length() == 11) {
//                if (svm.password.getValue() != null) {
//                    binding.loginButton.setEnabled(false);
//                    binding.loginButton.setText("登陆中...");
//                    rvm.login(svm.account.getValue(),svm.password.getValue());
//                }
//            } else {
//                Toast.makeText(LoginActivity.this, "请正确填写手机号", Toast.LENGTH_SHORT).show();
//            }
            if (svm.account.getValue() != null && svm.account.getValue().length() == 11) {
                if (svm.captcha.getValue() != null && svm.captcha.getValue().length() >= 4) {
                    binding.loginButton.setEnabled(false);
                    binding.loginButton.setText("登陆中...");
                    rvm.checkCaptcha(svm.account.getValue(),svm.captcha.getValue());
                } else {
                    Toast.makeText(LoginActivity.this, "请正确验证码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "请正确填写手机号", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 注册
         *
         * @param view
         */
        public void signup(View view) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        }

        /**
         * 立即体验
         *
         * @param view
         */
        public void going(View view) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        /**
         * 获取验证码
         *
         * @param view
         */
        public void getCaptcha(View view) {
            if (svm.account.getValue() != null && svm.account.getValue().length() == 11) {
                svm.wait.setValue(true);
                countDownTimer.start();
                rvm.getCaptcha(svm.account.getValue());
            } else {
                Toast.makeText(LoginActivity.this, "请正确填写手机号", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int count = 0;
    /**
     * 定时器
     */
    private final CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            String timeText = (60 - count) + "s";
            count++;
            svm.captchaTimeText.setValue(timeText);
        }

        @Override
        public void onFinish() {
            count = 0;
            svm.wait.setValue(false);
            countDownTimer.cancel();
            svm.captchaTimeText.setValue("获取验证码");
        }
    };

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        Log.d("TAG", "LoginActivity onDestroy...");
        super.onDestroy();
    }
}