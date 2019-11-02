package com.dueeeke.videocontroller.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.MarqueeTextView;
import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 播放器顶部标题栏
 */
public class TitleView extends FrameLayout implements IControlComponent {

    private MediaPlayerControlWrapper mMediaPlayer;

    private LinearLayout mTitleContainer;
    private MarqueeTextView mTitle;
    private TextView mSysTime;//系统当前时间

    private BatteryReceiver mBatteryReceiver;

    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

    private boolean mIsShowing;

    public TitleView(@NonNull Context context) {
        super(context);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_title_view, this, true);
        mTitleContainer = findViewById(R.id.title_container);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = PlayerUtils.scanForActivity(getContext());
                if (activity != null && mMediaPlayer.isFullScreen()) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mMediaPlayer.stopFullScreen();
                }
            }
        });
        mTitle = findViewById(R.id.title);
        mSysTime = findViewById(R.id.sys_time);
        //电量
        ImageView batteryLevel = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(batteryLevel);

        mShowAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        mShowAnimator.setDuration(300);
        mHideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
            }
        });
        mHideAnimator.setDuration(300);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mBatteryReceiver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void show() {
        mIsShowing = true;
        if (getVisibility() != VISIBLE) {
            mSysTime.setText(PlayerUtils.getCurrentSystemTime());
            if (mMediaPlayer.isFullScreen()) {
                setVisibility(VISIBLE);
                mHideAnimator.cancel();
                mShowAnimator.start();
            }
        }
    }

    @Override
    public void hide() {
        mIsShowing = false;
        if (getVisibility() == VISIBLE) {
            if (mMediaPlayer.isFullScreen()) {
                mShowAnimator.cancel();
                mHideAnimator.start();
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            if (mIsShowing) {
                setVisibility(VISIBLE);
            }
            mTitle.setNeedFocus(true);
        } else {
            setVisibility(GONE);
            mTitle.setNeedFocus(false);
        }
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
        mTitleContainer.setPadding(0, 0, 0, 0);
    }

    @Override
    public void adjustLandscape(int space) {
        mTitleContainer.setPadding(space, 0, 0, 0);
    }

    @Override
    public void adjustReserveLandscape(int space) {
        mTitleContainer.setPadding(0, 0, space, 0);
    }


    private static class BatteryReceiver extends BroadcastReceiver {
        private ImageView pow;

        public BatteryReceiver(ImageView pow) {
            this.pow = pow;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) return;
            int current = extras.getInt("level");// 获得当前电量
            int total = extras.getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            pow.getDrawable().setLevel(percent);
        }
    }
}
