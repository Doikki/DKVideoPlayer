package com.dueeeke.videocontroller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 播放出错提示界面
 * Created by Devlin_n on 2017/4/13.
 */
public class ErrorView extends LinearLayout implements IControlComponent {

    private float mDownX;
    private float mDownY;

    private MediaPlayerControlWrapper mMediaPlayer;

    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_error_view, this, true);
        findViewById(R.id.status_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                mMediaPlayer.replay(false);
            }
        });
        setClickable(true);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
        } else if (playState == VideoView.STATE_IDLE) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void attach(MediaPlayerControlWrapper mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void adjustPortrait(int space) {

    }

    @Override
    public void adjustLandscape(int space) {

    }

    @Override
    public void adjustReserveLandscape(int space) {

    }
}
