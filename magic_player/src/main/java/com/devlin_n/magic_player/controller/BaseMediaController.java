package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.util.WindowUtil;
import com.devlin_n.magic_player.widget.CenterView;

import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseMediaController extends FrameLayout {

    private static final String TAG = BaseMediaController.class.getSimpleName();
    protected View controllerView;//控制器视图
    protected MediaPlayerControlInterface mediaPlayer;//播放器
    protected static boolean mShowing;//控制器是否处于显示状态
    protected CenterView mCenterView;
    protected AudioManager mAudioManager;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected boolean isLocked;

    public BaseMediaController(@NonNull Context context) {
        this(context, null);
    }

    public BaseMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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

    /**
     * 重置
     */
    public void reset() {
    }

    public void updatePlayButton() {
    }

    /**
     * 返回控制器的显示状态
     */
    public static boolean isShowing() {
        return mShowing;
    }

    public void updateFullScreen() {
    }

    public void startFullScreenDirectly() {

    }

    public void removeAllCallbacks(){
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
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
        updateFullScreen();
    }


    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mShowing && mediaPlayer.isPlaying()) {
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

    private float mDownX, mDownY;

    protected boolean mChangeVolume = false;//是否改变音量

    protected boolean mChangePosition = false;//是否改变播放进度

    protected boolean mChangeBrightness = false;

    protected int streamVolume;

    protected float mBrightness;

    protected boolean mSliding;

    protected int mPosition;

    private int mThreshold;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isLocked && mediaPlayer.isFullScreen()) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mChangeBrightness = false;
                    mChangeVolume = false;
                    mChangePosition = false;
                    mSliding = false;
                    mThreshold = 60;
                    streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mBrightness = WindowUtil.getAppCompActivity(getContext()).getWindow().getAttributes().screenBrightness;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isLocked && mediaPlayer.isFullScreen()) {
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (absDeltaX < mThreshold && absDeltaY < mThreshold) return false;
                    if (!mSliding) {
                        if (absDeltaX > absDeltaY) {
                            mChangePosition = true;
                            mSliding = true;
                        } else {
                            int screenWidth = WindowUtil.getScreenWidth(getContext());

                            if (mDownX > screenWidth / 2) {
                                mChangeBrightness = true;
                                mSliding = true;
                            } else {
                                mChangeVolume = true;
                                mSliding = true;
                            }
                        }
                        mThreshold = 0;
                    }

                    if (mChangePosition) {
                        slideToChangePosition(deltaX);
                    } else if (mChangeVolume) {
                        slideToChangeVolume(deltaY);
                    } else if (mChangeBrightness) {
                        slideToChangeBrightness(deltaY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mSliding) {
                    if (isShowing()) {
                        hide();
                    } else {
                        show();
                    }
                }
                if (!isLocked && mediaPlayer.isFullScreen()) {
                    if (mSliding) {
                        mCenterView.setVisibility(GONE);
                        mSliding = false;
                    }
                    if (mChangePosition) mediaPlayer.seekTo(mPosition);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
            default:
                break;
        }
        return true;
    }

    protected void slideToChangePosition(float deltaX) {
        mCenterView.setVisibility(VISIBLE);
        hide();
        mCenterView.setProVisibility(View.GONE);
        int width = getMeasuredWidth();
        int duration = mediaPlayer.getDuration();
        int currentPosition = mediaPlayer.getCurrentPosition();
        int position = (int) (deltaX * 2 / width * duration + currentPosition);
        if (position > currentPosition) {
            mCenterView.setIcon(R.drawable.ic_fast_forward);
        } else {
            mCenterView.setIcon(R.drawable.ic_rewind);
        }
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        mPosition = position;
        mCenterView.setTextView(stringForTime(position) + "/" + stringForTime(duration));
    }

    protected void slideToChangeBrightness(float deltaY) {
        mCenterView.setVisibility(VISIBLE);
        hide();
        mCenterView.setProVisibility(View.VISIBLE);
        deltaY = -deltaY;
        Window window = WindowUtil.getAppCompActivity(getContext()).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        mCenterView.setIcon(R.drawable.ic_brightness);
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
        deltaY = -deltaY;
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = streamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) {
            mCenterView.setIcon(R.drawable.ic_volume_off);
            index = 0;
        } else {
            mCenterView.setIcon(R.drawable.ic_volume_on);
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

    public void updateProgress() {
        post(mShowProgress);
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

        void startFullScreen();

        void stopFullScreen();

        boolean isFullScreen();

        String getTitle();

        void startFullScreenDirectly();

        void skipToNext();

        void setMute();

        boolean isMute();

        void setLock(boolean isLocked);

        int getCurrentState();
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
