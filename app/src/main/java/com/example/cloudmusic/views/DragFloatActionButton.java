package com.example.cloudmusic.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DragFloatActionButton extends FloatingActionButton {
    //父布局宽高
    private int parentHeight;
    private int parentWidth;
    //private MotionEvent ev;

    public DragFloatActionButton(@NonNull Context context) {
        super(context);
    }

    public DragFloatActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragFloatActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int lastX;
    private int lastY;
    private boolean isDrag;


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);//默认是点击事件
                isDrag = false;//默认是非拖动而是点击事件
                getParent().requestDisallowInterceptTouchEvent(true);//父布局不要拦截子布局的监听，没有这个可能拖到toolBar等控件时会卡住
                lastX = rawX;
                lastY = rawY;
                ViewGroup parent;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //父成局存在才可拖动
                if (parentHeight <= 0 || parentWidth <= 0) {
                    isDrag = false;
                    break;
                }
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                isDrag = true;
                float x = getX() + dx;
                float y = getY() + dy;
                if (x < 0) {
                    x = 0;
                } else if (x > parentWidth - getWidth()) {
                    x = parentWidth - getWidth();
                }
                if (y < 250) {
                    y = 250;
                } else if (y > parentHeight - getHeight()) {
                    y = parentHeight - getHeight();
                }
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;

            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    setPressed(false);
                }
                //动画贴边
                if (getX() != 0) {
                    if (getX() >= (float) (parentWidth / 2 - getWidth() / 2)) {
                        ObjectAnimator animator = ObjectAnimator
                                .ofFloat(this, "x", getX(), parentWidth - getWidth());
                        animator.setDuration(400);//动画时长
                        animator.start();//开始动画
                    } else {
                        ObjectAnimator animator = ObjectAnimator
                                .ofFloat(this, "x", getX(), 0);
                        animator.setDuration(400);
                        animator.start();
                    }
                }
                break;
        }
        return isDrag || super.onTouchEvent(ev);
    }
}
