package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.searched.OneSongAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityArtistBinding;
import com.example.cloudmusic.entity.Artist;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.request.activity.RequestArtisViewModel;
import com.example.cloudmusic.state.activity.StateArtisViewModel;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.views.OneSongMoreOperateDialog;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ArtistActivity extends BaseActivity {
    ActivityArtistBinding binding;
    StateArtisViewModel svm;
    RequestArtisViewModel rvm;
    private Artist artist;
    private List<Song> songList;
    private OneSongAdapter adapter;
    private OneSongMoreOperateDialog dialog;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_artist);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateArtisViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestArtisViewModel.class);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        songList=new ArrayList<>();
    }

    @Override
    protected void initView() {
        binding.artistLoadFalse.setVisibility(View.GONE);
        binding.artistLoading.show();
        binding.arDecTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent = getIntent();
        artist = (Artist) intent.getSerializableExtra("ar");
        Glide.with(ArtistActivity.this)
                .load(artist.getPicUrl())
                // 设置高斯模糊
                // "25"	模糊度，取值范围1~25
                // "3"	图片缩放3倍后再进行模糊，一般缩放3-5倍
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new CenterCrop(), new BlurTransformation(25, 3))
                // 将得到的模糊图像替换原来的背景
                .into(binding.imageView9);
        Glide.with(ArtistActivity.this).load(artist.getPicUrl()).transform(new CenterCrop(), new RoundedCorners(10)).placeholder(R.drawable.ic_cd_default).diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.arCircleImageView);
        upDateLikeButton();
    }

    @Override
    protected void getInternetData() {
        rvm.getArtistDetail(artist.getArId());
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.requestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.artistLoadFalse.setVisibility(View.VISIBLE);
            } else {
                binding.artistLoadFalse.setVisibility(View.GONE);
            }
            binding.artistLoading.hide();
        });
        rvm.arDec.observe(this, s -> svm.dec.setValue(s));
        rvm.songList.observe(this, sl -> {
            songList.addAll(sl);
            setSongListRV(songList);
        });
        rvm.song.observe(this, song -> {
            if (!CloudMusic.isStartPlayerActivity) {
                CloudMusic.isStartPlayerActivity = true;
                startActivity(new Intent(ArtistActivity.this, PlayerActivity.class));
            }
        });
        rvm.arLikeState.observe(this, s -> {
            if(s.equals(CloudMusic.FAILURE)){
                Toast toast = Toast.makeText(ArtistActivity.this, "\n操作失败\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }else {
                Toast toast = Toast.makeText(ArtistActivity.this, "\n操作成功\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            upDateLikeButton();
        });
        rvm.likeState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast falseToast = Toast.makeText(ArtistActivity.this, "\n操作失败!\n", Toast.LENGTH_SHORT);
                falseToast.setGravity(Gravity.CENTER, 0, 0);
                falseToast.show();
            } else {
                Toast toast = Toast.makeText(ArtistActivity.this, "\n操作成功!\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(dialog!=null&&dialog.isShowing())
                dialog.upDateLikeButton();
        });
    }

    private void upDateLikeButton() {
        for(String id:CloudMusic.likeArtistIdSet){
            if(id.equals(artist.getArId())){
                binding.likeArtistBtn.setLike(true);
                return;
            }
        }
        binding.likeArtistBtn.setLike(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        CloudMusic.isStartPlayerActivity = false;
    }

    private void setSongListRV(List<Song> songList) {
        binding.arSongsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OneSongAdapter(songList);
        adapter.setClickCallback(song -> {
            if (CloudMusic.isGettingSongUrl) return;
            rvm.play(song);
        });
        adapter.setMoreOperateClickCallback(song -> {
            dialog = new OneSongMoreOperateDialog(ArtistActivity.this, song, song12 -> {
                rvm.addSongToPlayList(song12);
                Toast.makeText(ArtistActivity.this, "已添加", Toast.LENGTH_SHORT).show();
            }, song1 -> {
                boolean isLike = true;
                for (String id : CloudMusic.likeSongIdSet) {
                    if (id.equals(song1.getSongId())) {
                        isLike=false;
                        break;
                    }
                }
                rvm.like(isLike, song1.getSongId());
            });
            dialog.show();
        });
        binding.arSongsRv.setAdapter(adapter);
    }

    public class ClickClass {
        public void like(View view) {
            if(CloudMusic.userId.equals("0")){
                Toast toast = Toast.makeText(ArtistActivity.this, "\n请先登陆\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
            binding.likeArtistBtn.setLike(!binding.likeArtistBtn.isLike());
            rvm.like(artist.getArId(),binding.likeArtistBtn.isLike());
        }

        public void refresh(View view) {
            binding.artistLoadFalse.setVisibility(View.GONE);
            binding.artistLoading.show();
            rvm.getArtistDetail(artist.getArId());
        }
    }
}