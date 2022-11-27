package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivitySignUpBinding;
import com.example.cloudmusic.fragment.signup.CaptchaFragment;
import com.example.cloudmusic.fragment.signup.NicknameFragment;
import com.example.cloudmusic.fragment.signup.PasswordFragment;
import com.example.cloudmusic.request.activity.RequestSignUpViewModel;
import com.example.cloudmusic.fragment.signup.PhoneFragment;
import com.example.cloudmusic.state.activity.StateSignUpViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SignUpNextBtnCallback;

public class SignUpActivity extends BaseActivity {

    ActivitySignUpBinding binding;
    StateSignUpViewModel svm;
    RequestSignUpViewModel rvm;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateSignUpViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSignUpViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initView() {
        binding.signupLoginIng.setVisibility(View.GONE);
        Fragment captchaFragment = new CaptchaFragment(userInput -> {
            svm.captcha.setValue(userInput);
            binding.signupLoginIng.setVisibility(View.VISIBLE);
            rvm.signUp(svm.phone.getValue(), svm.password.getValue(), svm.nickname.getValue(), svm.captcha.getValue());
        });
        Fragment passwordFragment = new PasswordFragment(userInput -> {
            svm.password.setValue(userInput);
            replaceFragment(captchaFragment);
        });
        Fragment nicknameFragment = new NicknameFragment(userInput -> {
            svm.nickname.setValue(userInput);
            replaceFragment(passwordFragment);
        });
        Fragment phoneFragment = new PhoneFragment(userInput -> {
            svm.phone.setValue(userInput);
            CloudMusic.phone=userInput;
            replaceFragment(nicknameFragment);
        });
        replaceFragment(phoneFragment);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.signUpState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals(CloudMusic.SUCCEED)) {
                    Toast.makeText(SignUpActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                }
                binding.signupLoginIng.setVisibility(View.GONE);
                finish();
            }
        });
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }
    }

    public void replaceFragment(Fragment framgent) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.signupFragment, framgent);
        //transaction.addToBackStack(null);//碎片模拟返回栈
        transaction.commit();
    }
}