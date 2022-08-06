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
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.databinding.DialogMusiclistBinding;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;

import java.util.ArrayList;
import java.util.List;

public class MusicListDialog extends Dialog {

    DialogMusiclistBinding binding;
    private SongListItemOnClickCallback clickCallback;
    private SongListItemRemoveCallback removeCallback;

    public MusicListDialog(@NonNull Context context, int themeResId,SongListItemOnClickCallback clickCallback,SongListItemRemoveCallback removeCallback) {
        super(context, themeResId);
        this.clickCallback=clickCallback;
        this.removeCallback=removeCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_musiclist, null, false);
        setContentView(binding.getRoot());
        initWindow();
        initList();

    }

    /**
     * 加载播放列表
     *
     */
    private void initList() {
        //显示音乐列表 test
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        List<Song> songList = new ArrayList<>(MediaManager.getInstance().getSongList());
        MusicAdapter adapter = new MusicAdapter(songList,layoutManager);
        adapter.setClickCallback(clickCallback);
        adapter.setRemoveCallback(removeCallback);
        binding.recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化对话框样式
     * 与显示位置
     */
    private void initWindow() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_shape_musici_list);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setWindowAnimations(R.style.music_list_dialog);//弹出动画
        window.setGravity(Gravity.BOTTOM);//设置显示位置
    }
}
