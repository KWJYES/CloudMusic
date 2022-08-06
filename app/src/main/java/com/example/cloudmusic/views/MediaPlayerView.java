package com.example.cloudmusic.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.callback.MediaPlayerViewOnClickCallback;
import com.example.cloudmusic.utils.callback.PlayerViewMusicListOnClickCallback;
import com.example.cloudmusic.utils.callback.PlayOnClickCallback;

public class MediaPlayerView extends RelativeLayout {


    @SuppressLint("StaticFieldLeak")
    private static ImageView ic_songPng;
    @SuppressLint("StaticFieldLeak")
    private static TextView tv_songName;
    @SuppressLint("StaticFieldLeak")
    private static Button btn_startOrPause;
    @SuppressLint("StaticFieldLeak")
    private static Button btn_musicList;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static ConstraintLayout bg;
    @SuppressLint("StaticFieldLeak")
    private static int bgColor;
    @SuppressLint("StaticFieldLeak")
    private static boolean isPlaying;


    public MediaPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MediaPlayerView.context = context;
        initView();
        //获取属性值
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MediaPlayerView);
        int ic_resId = typedArray.getResourceId(R.styleable.MediaPlayerView_ic_songPng, R.drawable.ic_cd_default);
        String songName = typedArray.getString(R.styleable.MediaPlayerView_tv_songName);
        isPlaying = typedArray.getBoolean(R.styleable.MediaPlayerView_isPlaying, false);
        bgColor = typedArray.getInt(R.styleable.MediaPlayerView_bgColor, 0);
        //用完释放一下
        typedArray.recycle();
        //设置值
        Glide.with(getContext()).load(ic_resId).into(ic_songPng);
        tv_songName.setText(songName);
        if (isPlaying) btn_startOrPause.setBackgroundResource(R.drawable.ic_pause);
        else btn_startOrPause.setBackgroundResource(R.drawable.ic_play);
        setBgColor(this, bgColor);
    }

    /**
     * 设置控件背景颜色
     *
     * @param bgColor
     */
    @BindingAdapter("bgColor")
    public static void setBgColor(MediaPlayerView mediaPlayerView, int bgColor) {
        switch (bgColor) {
            case 1:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media1));
                break;
            case 2:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media2));
                break;
            case 3:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media3));
                break;
            case 4:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media4));
                break;
            case 5:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media5));
                break;
            case 6:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media6));
                break;
            case 7:
                bg.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_media7));
                break;
        }
    }

    /**
     * 导入布局
     */
    private void initView() {
        View view = View.inflate(getContext(), R.layout.media_player_view, null);
        addView(view);
        ic_songPng = view.findViewById(R.id.ic_musicPng);
        tv_songName = view.findViewById(R.id.tv_songName);
        tv_songName.setSelected(true);//实现跑马灯
        btn_musicList = view.findViewById(R.id.btn_musicList);
        btn_startOrPause = view.findViewById(R.id.btn_startOrPause);
        bg = view.findViewById(R.id.bg);
    }

    /**
     * 设置歌曲图片
     *
     * @param url
     */
    @BindingAdapter("ic_songPng")
    public static void setIc_songPng(MediaPlayerView mediaPlayerView, String url) {
        if (url == null)
            return;
        if (!"".equals(url))
            Glide.with(context).load(url).centerCrop().into(ic_songPng);
    }

    /**
     * 自定义view用dataBinding:   static方法    @BindingAdapter注解  参数有自定义view本类
     *
     * @param mediaPlayerView
     * @param tvsongName
     */
    @BindingAdapter("tv_songName")
    public static void setTv_songName(MediaPlayerView mediaPlayerView, String tvsongName) {
        if (tvsongName == null) return;
        if (!"".equals(tvsongName))
            tv_songName.setText(tvsongName);
    }

    @BindingAdapter("is_playing")
    public static void setPlayingState(MediaPlayerView mediaPlayerView,boolean playing){
        isPlaying=playing;
        if (isPlaying) btn_startOrPause.setBackgroundResource(R.drawable.ic_pause);
        else btn_startOrPause.setBackgroundResource(R.drawable.ic_play);
    }


    /**
     * 为播放按键设置点击事件
     *
     * @param callback
     */
    @BindingAdapter("btn_startOrPauseOnClickListener")
    public static void setBtn_startOrPauseOnClickListener(MediaPlayerView mediaPlayerView, PlayOnClickCallback callback) {
        if (callback == null)
            return;
        btn_startOrPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick(isPlaying);
            }
        });
    }


    /**
     * 设置播放列表按键监听
     *
     * @param callback
     */
    @BindingAdapter("btn_musicListOnClickListener")
    public static void setBtn_musicListOnClickListener(MediaPlayerView mediaPlayerView, PlayerViewMusicListOnClickCallback callback) {
        if (callback == null) return;
        btn_musicList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick();
            }
        });
    }

    /**
     * 设置跳转音乐播放页面的事件
     * @param mediaPlayerView
     * @param clickCallback
     */
    @BindingAdapter("mediaPlayerViewOnClickListener")
    public static void setMediaPlayerViewOnClickListener(MediaPlayerView mediaPlayerView, MediaPlayerViewOnClickCallback clickCallback) {
        if (clickCallback == null) return;
        ic_songPng.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallback.onClick();
            }
        });
        tv_songName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallback.onClick();
            }
        });
    }
}
