package com.dueeeke.videocontroller.component;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.BatteryReceiver;
import com.dueeeke.videocontroller.MarqueeTextView;
import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 标题栏
 */
public class TitleView extends FrameLayout implements IControlComponent {

    private MediaPlayerControl mMediaPlayer;

    private LinearLayout mTitleContainer;
    private ImageView mBack;
    private MarqueeTextView mTitle;
    private TextView mSysTime;//系统当前时间
    private ImageView mBatteryLevel;//电量
    private BatteryReceiver mBatteryReceiver;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

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
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_title_view, this, true);
        mTitleContainer = findViewById(R.id.title_container);
        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
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
        mBatteryLevel = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(mBatteryLevel);

        mShowAnimator = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
        mShowAnimator.setDuration(300);
        mHideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
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
        if (mMediaPlayer.isFullScreen()) {
            mSysTime.setText(PlayerUtils.getCurrentSystemTime());
            setVisibility(VISIBLE);
            mHideAnimator.cancel();
            mShowAnimator.start();
        }
    }

    @Override
    public void hide() {
        if (mMediaPlayer.isFullScreen()) {
            setVisibility(GONE);
            mShowAnimator.cancel();
            mHideAnimator.start();
        }
    }

    @Override
    public void onPlayStateChange(int playState) {

    }

    @Override
    public void onPlayerStateChange(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            setVisibility(VISIBLE);
            mTitle.setNeedFocus(true);
        } else {
            setVisibility(GONE);
            mTitle.setNeedFocus(false);
        }
    }

    @Override
    public void attach(MediaPlayerControl mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setProgress(int position) {

    }

    @Override
    public void adjustPortrait(int space) {
        setPadding(0, 0, 0, 0);
    }

    @Override
    public void adjustLandscape(int space) {
        setPadding(space, 0, 0, 0);
    }

    @Override
    public void adjustReserveLandscape(int space) {
        setPadding(0, 0, space, 0);
    }
}
