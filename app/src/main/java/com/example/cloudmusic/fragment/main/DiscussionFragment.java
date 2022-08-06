package com.example.cloudmusic.fragment.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentDiscussionBinding;


public class DiscussionFragment extends BaseFragment {

    FragmentDiscussionBinding binding;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_discussion,container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG","DiscussionFragment onResume");
    }
}