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
import com.example.cloudmusic.databinding.FragmentPhoneBinding;
import com.example.cloudmusic.request.fragment.signup.RequestPhoneFragmentViewModel;
import com.example.cloudmusic.state.activity.StateMainViewModel;
import com.example.cloudmusic.state.signup.StatePhoneFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.SignUpNextBtnCallback;


public class PhoneFragment extends BaseFragment {

    FragmentPhoneBinding binding;
    StatePhoneFragmentViewModel svm;
    RequestPhoneFragmentViewModel rvm;

    public PhoneFragment() {
    }

    public PhoneFragment(SignUpNextBtnCallback signUpNextBtnCallback) {
        this.signUpNextBtnCallback = signUpNextBtnCallback;
    }

    private SignUpNextBtnCallback signUpNextBtnCallback;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_phone,container,false);
        svm=new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StatePhoneFragmentViewModel.class);
        rvm=new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestPhoneFragmentViewModel.class);
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
        rvm.requestState.observe(this, s -> {
            if(s.equals(CloudMusic.FAILURE))
                Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            svm.wait.setValue(false);
        });
        rvm.enable.observe(this, aBoolean -> {
            if(aBoolean) signUpNextBtnCallback.onNextStep(svm.phone.getValue());
            svm.enable.setValue(aBoolean);
        });
    }

    public class ClickClass{
        public void nextStep(View view){
            svm.wait.setValue(true);
            rvm.checkPhone(svm.phone.getValue());
        }
    }
}