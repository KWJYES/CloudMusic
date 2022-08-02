package com.example.cloudmusic.fragment.search;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentSearchedBinding;


public class SearchedFragment extends BaseFragment {

    FragmentSearchedBinding binding;

    private String searedWord;

    public SearchedFragment(String searedWord) {
        this.searedWord = searedWord;
    }

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_searched,container,false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}