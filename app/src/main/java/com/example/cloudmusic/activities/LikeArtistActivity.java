package com.example.cloudmusic.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.ArtistAllAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityLikeArtistBinding;
import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.request.activity.RequestLikeArtistViewModel;
import com.example.cloudmusic.request.activity.RequestLikeSongViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class LikeArtistActivity extends BaseActivity {

    ActivityLikeArtistBinding binding;
    RequestLikeArtistViewModel rvm;
    private List<Artist> mArtistList;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like_artist);
        binding.setClick(new ClickClass());
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestLikeArtistViewModel.class);
        binding.setLifecycleOwner(this);
        mArtistList=new ArrayList<>(CloudMusic.likeArtistSet);
    }

    @Override
    protected void initView() {
        binding.likeArNull.setVisibility(View.GONE);
        binding.likeListLoading.hide();
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> rvm.getLikeArtistList());
        setArtistRV(mArtistList);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.artistListRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.SUCCEED)) {
                binding.likeArNull.setVisibility(View.GONE);
            }else {
                binding.likeArNull.setVisibility(View.VISIBLE);
            }
            binding.likeListLoading.hide();
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.artistList.observe(this, artists -> {
            CloudMusic.likeArtistSet.addAll(artists);
            for(Artist artist:artists){
                CloudMusic.likeArtistIdSet.add(artist.getArId());
                Log.d("TAG","CloudMusic.likeArtistIdSet:"+CloudMusic.likeArtistIdSet.toString());
            }
            mArtistList.clear();
            CloudMusic.likeArtistSet.clear();
            CloudMusic.likeArtistSet.addAll(artists);
            mArtistList.addAll(artists);
            setArtistRV(mArtistList);
        });
    }

    private void setArtistRV(List<Artist> mArtistList) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        binding.likeArRV.setLayoutManager(layoutManager);
        ArtistAllAdapter adapter = new ArtistAllAdapter(mArtistList);
        adapter.setClickCallback(artist -> {
            Intent intent = new Intent(LikeArtistActivity.this, ArtistActivity.class);
            intent.putExtra("ar", artist);
            startActivity(intent);
        });
        binding.likeArRV.setAdapter(adapter);
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }
    }
}