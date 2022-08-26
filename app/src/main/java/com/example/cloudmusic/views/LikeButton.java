package com.example.cloudmusic.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cloudmusic.R;

import java.util.HashMap;
import java.util.Map;

public class LikeButton extends RelativeLayout {
    private final TextView button;
    private final int unLikeColor;
    private boolean isLike=false;

    public LikeButton(Context context) {
        this(context, null);
    }

    public LikeButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(getContext(), R.layout.like_button_layout, null);
        addView(view);
        button = view.findViewById(R.id.likeButton);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LikeButton);
        unLikeColor = typedArray.getInt(R.styleable.LikeButton_unLikeColor, 0);
        typedArray.recycle();
        if (unLikeColor == 0)
            button.setBackgroundResource(R.drawable.ic_like0);
        else button.setBackgroundResource(R.drawable.ic_like0_black);
    }

    public void setLike(boolean like){
        Log.d("likeButton","setLike:"+like);
        Log.d("likeButton","unLikeColor:"+unLikeColor);
        if(like){
            button.setBackgroundResource(R.drawable.ic_like1);
        }else {
            if (unLikeColor == 0) {
                button.setBackgroundResource(R.drawable.ic_like0);
            }
            else button.setBackgroundResource(R.drawable.ic_like0_black);
        }
        isLike=like;
    }

    public boolean isLike(){
        Log.d("likeButton","getLike:"+isLike);
        return isLike;
    }
}
