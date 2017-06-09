package com.devlin_n.yin_yang_player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.devlin_n.yin_yang_player.R;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yin_yang_player.util.WindowUtil;

/**
 * 悬浮窗控件（解决滑动冲突）
 * Created by Devlin_n on 2017/6/8.
 */

@SuppressLint("ViewConstructor")
public class FloatView extends FrameLayout{


    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    public YinYangPlayer magicVideoView;

    public FloatView(@NonNull Context context, WindowManager mWindowManager, WindowManager.LayoutParams mParams) {
        super(context);
        this.mWindowManager = mWindowManager;
        this.mParams = mParams;
        init();
    }


    private void init() {
        setBackgroundResource(R.drawable.shape_float_window_background);
        int padding = WindowUtil.dip2px(getContext(), 1);
        setPadding(padding, padding, padding, padding);
        magicVideoView = new YinYangPlayer(getContext());
        addView(magicVideoView);
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
                    floatY = (int) (event.getY() + WindowUtil.getStatusBarHeight(getContext()));
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
