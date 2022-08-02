package com.example.cloudmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityMusicListBinding;
import com.example.cloudmusic.entity.MusicList;

public class MusicListActivity extends BaseActivity {

    ActivityMusicListBinding binding;

    @Override
    protected void initActivity() {
        binding= DataBindingUtil.setContentView(this,R.layout.activity_music_list);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initView() {
        MusicList musicList= (MusicList) getIntent().getSerializableExtra("musicList");
    }

    public class ClickClass{

    }
}