package com.example.cloudmusic.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

abstract public class BaseFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initFragment(inflater,container, savedInstanceState);
    }

    protected abstract View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
