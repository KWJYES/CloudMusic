package com.example.cloudmusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyHRecyclerView extends RecyclerView {
    private int lastX;
    private int lastY;

    public MyHRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyHRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * getRowX：触摸点相对于屏幕的坐标
     * <p>
     * getX： 触摸点相对于RecyclerView的坐标
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();
        if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            lastX = rawX;
            lastY = rawY;
            getParent().requestDisallowInterceptTouchEvent(true);  //设置不拦截
        }else if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            int dx = (rawX - lastX) < 0 ? lastX - rawX : rawX - lastX;
            int dy = (rawY - lastY) < 0 ? lastY - rawY : rawY - lastY;
            if (dx - dy> -50)
                getParent().requestDisallowInterceptTouchEvent(true);  //设置不拦截
            else getParent().requestDisallowInterceptTouchEvent(false);  //设置拦截
        }else if((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }
}
