package com.example.cloudmusic.fragment.signup;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentCaptchaBinding;
import com.example.cloudmusic.request.fragment.signup.RequestCaptchaFragmentViewModel;
import com.example.cloudmusic.state.signup.StateCaptchaFragmentViewModel;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.utils.callback.SignUpNextBtnCallback;

public class CaptchaFragment extends BaseFragment {

    FragmentCaptchaBinding binding;
    StateCaptchaFragmentViewModel svm;
    RequestCaptchaFragmentViewModel rvm;

    private String phone;
    private SignUpNextBtnCallback nextBtnCallback;

    public CaptchaFragment() {
    }

    public CaptchaFragment(SignUpNextBtnCallback nextBtnCallback) {
        this.nextBtnCallback = nextBtnCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_captcha, container, false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateCaptchaFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestCaptchaFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    protected void initView() {
        svm.correct.setValue(true);
        svm.wait.setValue(false);
        svm.captchaBtnEnable.setValue(true);
        svm.captchaBtnText.setValue("获取验证码");
    }

    @Override
    protected void initSomeData() {
        phone=CloudMusic.phone;
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.checkRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE))
                Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            svm.wait.setValue(false);
        });
        rvm.sentRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE))
                Toast.makeText(getContext(), "请求验证码失败", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
        });
        rvm.correct.observe(this, aBoolean -> {
            if (aBoolean) nextBtnCallback.onNextStep(svm.captcha.getValue());
            svm.correct.setValue(aBoolean);
        });
    }


    public class ClickClass {
        public void complete(View view) {
            svm.wait.setValue(true);
            rvm.checkCaptcha(phone,svm.captcha.getValue());
        }

        public void getCaptcha(View view) {
            svm.captchaBtnEnable.setValue(false);
            countDownTimer.start();
            rvm.getCaptcha(phone);
        }
    }

    private int count=0;
    /**
     * 定时器
     */
    private final CountDownTimer countDownTimer=new CountDownTimer(60000,1000) {
        @Override
        public void onTick(long l) {
            String timeText=(60-count)+"s";
            count++;
            svm.captchaBtnText.setValue(timeText);
        }

        @Override
        public void onFinish() {
            svm.captchaBtnText.setValue("获取验证码");
            count=0;
            svm.captchaBtnEnable.setValue(true);
            countDownTimer.cancel();
        }
    };

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }
}