package com.example.cloudmusic.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.cloudmusic.R;
import com.example.cloudmusic.databinding.DialogOnesongMoreOperateBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.utils.callback.OneSongMoreDialogClickCallback;

public class OneSongMoreOperateDialog extends Dialog {

    DialogOnesongMoreOperateBinding binding;
    private Song song;
    private OneSongMoreDialogClickCallback clickCallback;
    private OneSongMoreDialogClickCallback likeClickCallback;

    public OneSongMoreOperateDialog(@NonNull Context context, Song song, OneSongMoreDialogClickCallback clickCallback,OneSongMoreDialogClickCallback likeClickCallback) {
        super(context);
        this.song=song;
        this.clickCallback=clickCallback;
        this.likeClickCallback=likeClickCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_onesong_more_operate, null, false);
        setContentView(binding.getRoot());
        init();
        initView();
    }

    private void initView() {
        Glide.with(getContext()).load(song.getPicUrl()).transform(new CenterCrop(),new RoundedCorners(20)).placeholder(R.drawable.pic_cd).into(binding.songPic);
        binding.songName.setText(song.getName());
        binding.songAr.setText(song.getArtist());
        binding.nextPlay.setOnClickListener(view -> clickCallback.onClick(song));
        upDateLikeButton();
        binding.likeButton2.setOnClickListener(view -> {
            binding.likeButton2.setLike(!binding.likeButton2.isLike());
            likeClickCallback.onClick(song);
        });
    }

    public void upDateLikeButton() {
        for (String id : CloudMusic.likeSongIdSet) {
            if (id.equals(song.getSongId())) {
                binding.likeButton2.setLike(true);
                return;
            }
        }
        binding.likeButton2.setLike(false);
    }

    /**
     * 初始化对话框样式
     * 与显示位置
     */
    private void init() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_shape_musici_list);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.music_list_dialog);//弹出动画
        window.setGravity(Gravity.BOTTOM);//设置显示位置
    }
}
