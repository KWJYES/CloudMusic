package com.example.cloudmusic.fragment.signup;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentNicknameBinding;
import com.example.cloudmusic.request.activity.RequestSignUpViewModel;
import com.example.cloudmusic.request.fragment.signup.RequestNicknameFragmentViewModel;
import com.example.cloudmusic.state.activity.StateSignUpViewModel;
import com.example.cloudmusic.state.signup.StateNicknameFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SignUpNextBtnCallback;


public class NicknameFragment extends BaseFragment {

    FragmentNicknameBinding binding;
    StateNicknameFragmentViewModel svm;
    RequestNicknameFragmentViewModel rvm;

    private SignUpNextBtnCallback nextBtnCallback;

    public NicknameFragment() {
    }

    public NicknameFragment(SignUpNextBtnCallback nextBtnCallback) {
        this.nextBtnCallback = nextBtnCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_nickname,container,false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateNicknameFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestNicknameFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        svm.enable.setValue(true);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.candidateNickname.observe(this, s -> svm.nickname.setValue(s));
        rvm.requestState.observe(this, s -> {
            if(s.equals(CloudMusic.FAILURE))
                Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            svm.wait.setValue(false);
        });
        rvm.enable.observe(this, aBoolean -> {
            if(aBoolean) nextBtnCallback.onNextStep(svm.nickname.getValue());
            svm.enable.setValue(aBoolean);
        });
    }

    public class ClickClass{
        public void nextStep(View view){
            svm.wait.setValue(true);
            rvm.checkNickname(svm.nickname.getValue());
        }
    }
}