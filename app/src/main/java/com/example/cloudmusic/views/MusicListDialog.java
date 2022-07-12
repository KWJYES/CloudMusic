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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.MusicAdapter;
import com.example.cloudmusic.databinding.DialogMusiclistBinding;
import com.example.cloudmusic.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicListDialog extends Dialog {

    DialogMusiclistBinding binding;

    public MusicListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_musiclist, null, false);
        setContentView(binding.getRoot());
        init();
        initList();
    }

    /**
     * 加载播放列表
     *
     */
    private void initList() {
        //显示音乐列表 test
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Song> songList = new ArrayList<>();
        Song song = new Song();
        song.setName("海阔天空");
        song.setArtist("Beyond");
        songList.add(song);
        Song song2 = new Song();
        song2.setName("灰色轨迹");
        song2.setArtist("Beyond");
        songList.add(song2);
        Song song3 = new Song();
        song3.setName("光辉岁月");
        song3.setArtist("Beyond");
        songList.add(song3);
        Song song4 = new Song();
        song4.setName("不再犹豫");
        song4.setArtist("Beyond");
        songList.add(song4);
        binding.recyclerView.setAdapter(new MusicAdapter(songList));
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
