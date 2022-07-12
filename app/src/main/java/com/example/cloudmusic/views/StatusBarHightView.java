package com.example.cloudmusic.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 一个有系统状态栏高度的透明View
 */
public class StatusBarHightView extends View {

    private final Paint mPaint;
    public StatusBarHightView(Context context) {
        this(context, null);
    }

    public StatusBarHightView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public StatusBarHightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width ;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else {
            width = 0;
        }
        height = getStatusBarHeight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.TRANSPARENT);//设置为透明色
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
    }

    /**
     * 利用反射获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
