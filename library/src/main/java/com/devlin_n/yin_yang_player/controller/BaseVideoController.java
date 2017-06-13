package com.devlin_n.yin_yang_player.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.devlin_n.yin_yang_player.R;
import com.devlin_n.yin_yang_player.util.WindowUtil;
import com.devlin_n.yin_yang_player.widget.CenterView;

import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseVideoController extends FrameLayout {

    protected View controllerView;//控制器视图
    protected MediaPlayerControlInterface mediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected CenterView mCenterView;
    protected AudioManager mAudioManager;
    protected boolean isLocked;
    protected int sDefaultTimeout = 3000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private GestureDetector mGestureDetector;
    protected boolean gestureEnabled;
    private float downX;
    private float downY;


    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mCenterView = new CenterView(getContext());
        mCenterView.setVisibility(GONE);
        addView(mCenterView);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        setClickable(true);
        setFocusable(true);
        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示
     */
    public void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    public void setPlayState(int playState) {
    }

    public void setPlayerState(int playerState) {
    }

    public void startFullScreenDirectly() {

    }

    protected void doPauseResume() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        if (mediaPlayer.isFullScreen()) {
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mediaPlayer.stopFullScreen();
        } else {
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mediaPlayer.startFullScreen();
        }
    }


    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    protected int setProgress() {
        return 0;
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

    protected int streamVolume;

    protected float mBrightness;

    protected int mPosition;

    protected boolean mNeedSeek;

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean mChangePosition;
        private boolean mChangeBrightness;
        private boolean mChangeVolume;

        @Override
        public boolean onDown(MotionEvent e) {
            if (!gestureEnabled) return super.onDown(e);
            streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mBrightness = WindowUtil.getAppCompActivity(getContext()).getWindow().getAttributes().screenBrightness;
            firstTouch = true;
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
            if (!gestureEnabled) return super.onScroll(e1, e2, distanceX, distanceY);
            float deltaX = e1.getX() - e2.getX();
            float deltaY = e1.getY() - e2.getY();
            if (firstTouch) {
                mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
                if (!mChangePosition) {
                    int screenWidth = WindowUtil.getScreenWidth(getContext());
                    if (e2.getX() > screenWidth / 2) {
                        mChangeBrightness = true;
                    } else {
                        mChangeVolume = true;
                    }
                }
                firstTouch = false;
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
            if (!isLocked) doPauseResume();
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
                mediaPlayer.seekTo(mPosition);
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
        int duration = mediaPlayer.getDuration();
        int currentPosition = mediaPlayer.getCurrentPosition();
        int position = (int) (deltaX / width * duration + currentPosition);
        if (position > currentPosition) {
            mCenterView.setIcon(R.drawable.ic_action_fast_forward);
        } else {
            mCenterView.setIcon(R.drawable.ic_action_fast_rewind);
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
        Window window = WindowUtil.getAppCompActivity(getContext()).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        mCenterView.setIcon(R.drawable.ic_action_brightness);
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
        float index = streamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) {
            mCenterView.setIcon(R.drawable.ic_action_volume_off);
            index = 0;
        } else {
            mCenterView.setIcon(R.drawable.ic_action_volume_up);
        }
        int percent = (int) (index / streamMaxVolume * 100);
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
    }

    protected String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    public interface MediaPlayerControlInterface {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        void startFloatWindow();

        void stopFloatWindow();

        void startFullScreen();

        void stopFullScreen();

        boolean isFullScreen();

        String getTitle();

        void startFullScreenDirectly();

        void skipToNext();

        void setMute();

        boolean isMute();

        void setLock(boolean isLocked);
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
