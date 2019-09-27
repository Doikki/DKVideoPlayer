package com.dueeeke.videocontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoViewManager;

/**
 * 错误提示，网络提示
 * Created by Devlin_n on 2017/4/13.
 */

public class StatusView extends LinearLayout {

    private TextView tvMessage;
    private TextView btnAction;
    private float downX;
    private float downY;

    private MediaPlayerControl mMediaPlayer;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_status_view, this);
        tvMessage = root.findViewById(R.id.message);
        btnAction = root.findViewById(R.id.status_btn);
        this.setBackgroundResource(android.R.color.black);
        setClickable(true);
    }

    public void attachMediaPlayer(MediaPlayerControl mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    /**
     * 显示移动网络播放警告
     * @param container 承载此界面的容器
     */
    public void showNetWarning(ViewGroup container) {
        showNetWarning(container, -1);
    }

    /**
     * 显示移动网络播放警告
     * @param container 承载此界面的容器
     * @param index 此界面在容器中所处的位置
     */
    public void showNetWarning(ViewGroup container, int index) {
        dismiss();
        setMessage(getResources().getString(R.string.dkplayer_wifi_tip));
        setButtonTextAndAction(getResources().getString(R.string.dkplayer_continue_play), new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                mMediaPlayer.start();
            }
        });
        container.addView(this, index);
    }

    /**
     * 隐藏此界面
     */
    public void dismiss() {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this);
        }
    }

    /**
     * 显示播放错误界面
     * @param container 承载此界面的容器
     */
    public void showErrorView(ViewGroup container) {
        showErrorView(container, -1);
    }

    /**
     * 显示播放错误界面
     * @param container 承载此界面的容器
     * @param index 此界面在容器中所处的位置
     */
    public void showErrorView(ViewGroup container, int index) {
        dismiss();
        setMessage(getResources().getString(R.string.dkplayer_error_message));
        setButtonTextAndAction(getResources().getString(R.string.dkplayer_retry), new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mMediaPlayer.replay(false);
            }
        });
        container.addView(this, index);
    }


    private void setMessage(String msg) {
        if (tvMessage != null) tvMessage.setText(msg);
    }

    private void setButtonTextAndAction(String text, OnClickListener listener) {
        if (btnAction != null) {
            btnAction.setText(text);
            btnAction.setOnClickListener(listener);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - downX);
                float absDeltaY = Math.abs(ev.getY() - downY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
