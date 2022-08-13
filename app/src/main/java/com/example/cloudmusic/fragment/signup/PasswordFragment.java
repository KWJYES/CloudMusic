package com.example.cloudmusic.fragment.signup;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentPasswordBinding;
import com.example.cloudmusic.state.signup.StatePasswordFragmentViewModel;
import com.example.cloudmusic.utils.callback.SignUpNextBtnCallback;

public class PasswordFragment extends BaseFragment {

    FragmentPasswordBinding binding;
    StatePasswordFragmentViewModel svm;

    private SignUpNextBtnCallback nextBtnCallback;

    public PasswordFragment() {
    }

    public PasswordFragment(SignUpNextBtnCallback nextBtnCallback) {
        this.nextBtnCallback = nextBtnCallback;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_password,container,false);
        svm=new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(StatePasswordFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    public class ClickClass{
        public void nextStep(View view){
            nextBtnCallback.onNextStep(svm.password.getValue());
        }
    }
}