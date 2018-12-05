package com.dueeeke.dkplayer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 悬浮窗控件（解决滑动冲突）
 * Created by Devlin_n on 2017/6/8.
 */

@SuppressLint("ViewConstructor")
public class FloatView extends FrameLayout{

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    public FloatView(@NonNull Context context, int x, int y) {
        super(context);
        floatX = x;
        floatY = y;
        init();
    }


    private void init() {
        setBackgroundResource(R.drawable.shape_float_window_background);
        int padding = PlayerUtils.dp2px(getContext(), 1);
        setPadding(padding, padding, padding, padding);
        initWindow();
    }

    private void initWindow() {
        mWindowManager = PlayerUtils.getWindowManager(getContext().getApplicationContext());
        mParams = new WindowManager.LayoutParams();
//        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            mParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.windowAnimations = R.style.FloatWindowAnimation;
        mParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = PlayerUtils.dp2px(getContext(), 250);
        mParams.width = width;
        mParams.height = width * 9 / 16;
        mParams.x = floatX;
        mParams.y = floatY;
    }

    /**
     * 添加至窗口
     */
    public boolean addToWindow() {
        if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isAttachedToWindow()) {
                    mWindowManager.addView(this, mParams);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() == null) {
                        mWindowManager.addView(this, mParams);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 从窗口移除
     */
    public boolean removeFromWindow() {
        if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    mWindowManager.removeViewImmediate(this);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() != null) {
                        mWindowManager.removeViewImmediate(this);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getAction() != MotionEvent.ACTION_DOWN;
    }

    private int floatX;
    private int floatY;
    private boolean firstTouch = true;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (firstTouch) {
                    floatX = (int) event.getX();
                    floatY = (int) (event.getY() + PlayerUtils.getStatusBarHeight(getContext()));
                    firstTouch = false;
                }
                mParams.x = X - floatX;
                mParams.y = Y - floatY;
                mWindowManager.updateViewLayout(this, mParams);
                break;
        }
        return super.onTouchEvent(event);
    }
}
