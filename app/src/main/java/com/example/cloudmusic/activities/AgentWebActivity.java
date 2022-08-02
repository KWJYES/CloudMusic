package com.example.cloudmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityAgentWebBinding;
import com.example.cloudmusic.entity.Banner;
import com.just.agentweb.AgentWeb;

import java.util.Objects;

public class AgentWebActivity extends BaseActivity {

    ActivityAgentWebBinding binding;
    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_agent_web);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initView() {
        Banner banner= (Banner) getIntent().getSerializableExtra("banner");
        AgentWeb.with(AgentWebActivity.this)
                .setAgentWebParent(binding.l1, new LinearLayout.LayoutParams(-1, -1))//-1是指父布局
                .useDefaultIndicator(0xED2B2B) //默认进度条 可带颜色 例如 0xffffff
                .createAgentWeb()
                .ready()
                .go(banner.getUrl());
    }


    public class ClickClass{
        public void back(View view){
            finish();
        }
    }

}