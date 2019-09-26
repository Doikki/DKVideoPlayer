package com.dueeeke.videoplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 包含手势操作的VideoController
 * Created by xinyu on 2018/1/6.
 */

public abstract class GestureVideoController<T extends MediaPlayerControl> extends BaseVideoController<T> implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    protected GestureDetector mGestureDetector;
    protected AudioManager mAudioManager;
    protected boolean mIsGestureEnabled;
    protected int mStreamVolume;
    protected float mBrightness;
    protected int mPosition;
    protected boolean mNeedSeek;
    protected boolean mFirstTouch;
    protected boolean mChangePosition;
    protected boolean mChangeBrightness;
    protected boolean mChangeVolume;

    protected GestureListener mGestureListener;

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
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!mIsGestureEnabled || PlayerUtils.isEdge(getContext(), e)) return false;
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
        if (!mIsGestureEnabled || PlayerUtils.isEdge(getContext(), e1))
            return false;
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();
        if (mFirstTouch) {
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (!mChangePosition) {
                if (e2.getX() > PlayerUtils.getScreenWidth(getContext(), true) / 2) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }
            if (mGestureListener != null) {
                mGestureListener.onStartSlide();
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

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;
        if (!mGestureDetector.onTouchEvent(event) && detectedUp) {
            if (mGestureListener != null) {
                mGestureListener.onStopSlide();
            }
            if (mNeedSeek) {
                mMediaPlayer.seekTo(mPosition);
                mNeedSeek = false;
            }
        }
        return super.onTouchEvent(event);
    }

    protected void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        int duration = (int) mMediaPlayer.getDuration();
        int currentPosition = (int) mMediaPlayer.getCurrentPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        if (mGestureListener != null) {
            mGestureListener.onPositionChange(position, currentPosition, duration);
        }
        mPosition = position;
        mNeedSeek = true;
    }

    protected void slideToChangeBrightness(float deltaY) {
        Window window = PlayerUtils.scanForActivity(getContext()).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
        if (mGestureListener != null) {
            mGestureListener.onBrightnessChange(percent);
        }
    }

    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
        if (mGestureListener != null) {
            mGestureListener.onVolumeChange(percent);
        }
    }


    public interface GestureListener {
        /**
         * 开始滑动
         */
        void onStartSlide();

        /**
         * 结束滑动
         */
        void onStopSlide();

        /**
         * 滑动调整进度
         * @param slidePosition 滑动进度
         * @param currentPosition 当前播放进度
         * @param duration 视频总长度
         */
        void onPositionChange(int slidePosition, int currentPosition, int duration);

        /**
         * 滑动调整亮度
         * @param percent 亮度百分比
         */
        void onBrightnessChange(int percent);

        /**
         * 滑动调整音量
         * @param percent 音量百分比
         */
        void onVolumeChange(int percent);
    }

    public void setGestureListener(GestureListener gestureListener) {
        mGestureListener = gestureListener;
    }
}
