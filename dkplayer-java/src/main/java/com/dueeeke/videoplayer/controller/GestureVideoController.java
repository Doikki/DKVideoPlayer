package com.dueeeke.videoplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.dueeeke.videoplayer.widget.CenterView;

/**
 * 包含手势操作的VideoController
 * Created by xinyu on 2018/1/6.
 */

public abstract class GestureVideoController extends BaseVideoController{

    protected GestureDetector mGestureDetector;
    protected boolean mIsGestureEnabled;
    protected CenterView mCenterView;
    protected AudioManager mAudioManager;

    public GestureVideoController(@NonNull Context context) {
        super(context);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        mCenterView = new CenterView(getContext());
        mCenterView.setVisibility(GONE);
        addView(mCenterView);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    protected int mStreamVolume;

    protected float mBrightness;

    protected int mPosition;

    protected boolean mNeedSeek;

    protected class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean mFirstTouch;
        private boolean mChangePosition;
        private boolean mChangeBrightness;
        private boolean mChangeVolume;

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mIsGestureEnabled || PlayerUtils.isEdge(getContext(), e)) return super.onDown(e);
            mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mBrightness = PlayerUtils.scanForActivity(getContext()).getWindow().getAttributes().screenBrightness;
            mFirstTouch = true;
            mChangePosition = false;
            mChangeBrightness = false;
            mChangeVolume = false;
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mShowing) {
                hide();
            } else {
                show();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mIsGestureEnabled || PlayerUtils.isEdge(getContext(), e1)) return super.onScroll(e1, e2, distanceX, distanceY);
            float deltaX = e1.getX() - e2.getX();
            float deltaY = e1.getY() - e2.getY();
            if (mFirstTouch) {
                mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
                if (!mChangePosition) {
                    if (e2.getX() > PlayerUtils.getScreenWidth(getContext(), true) / 2) {
                        mChangeBrightness = true;
                    } else {
                        mChangeVolume = true;
                    }
                }
                mFirstTouch = false;
            }
            if (mChangePosition) {
                slideToChangePosition(deltaX);
            } else if (mChangeBrightness) {
                slideToChangeBrightness(deltaY);
            } else if (mChangeVolume) {
                slideToChangeVolume(deltaY);
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!mIsLocked) doPauseResume();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;
        if (!mGestureDetector.onTouchEvent(event) && detectedUp) {
            if (mCenterView.getVisibility() == VISIBLE) {
                mCenterView.setVisibility(GONE);
            }
            if (mNeedSeek) {
                mMediaPlayer.seekTo(mPosition);
                mNeedSeek = false;
            }
        }
        return super.onTouchEvent(event);
    }

    protected void slideToChangePosition(float deltaX) {
        mCenterView.setVisibility(VISIBLE);
        hide();
        mCenterView.setProVisibility(View.GONE);
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        int duration = (int) mMediaPlayer.getDuration();
        int currentPosition = (int) mMediaPlayer.getCurrentPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);
        if (position > currentPosition) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_forward);
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_rewind);
        }
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        mPosition = position;
        mCenterView.setTextView(stringForTime(position) + "/" + stringForTime(duration));
        mNeedSeek = true;
    }

    protected void slideToChangeBrightness(float deltaY) {
        mCenterView.setVisibility(VISIBLE);
        hide();
        mCenterView.setProVisibility(View.VISIBLE);
        Window window = PlayerUtils.scanForActivity(getContext()).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        mCenterView.setIcon(R.drawable.dkplayer_ic_action_brightness);
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
    }

    protected void slideToChangeVolume(float deltaY) {
        mCenterView.setVisibility(VISIBLE);
        hide();
        mCenterView.setProVisibility(View.VISIBLE);
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_off);
            index = 0;
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_up);
        }
        int percent = (int) (index / streamMaxVolume * 100);
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
    }
}
