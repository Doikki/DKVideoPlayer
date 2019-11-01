package com.dueeeke.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 自动播放完成界面
 */
public class CompleteView extends FrameLayout implements IControlComponent {

    private MediaPlayerControl mMediaPlayer;

    private ImageView mStopFullscreen;

    private boolean mAdaptCutout;

    private int mSpace;

    public CompleteView(@NonNull Context context) {
        super(context);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_complete_view, this, true);
        findViewById(R.id.iv_replay).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.replay(true);
            }
        });
        mStopFullscreen = findViewById(R.id.stop_fullscreen);
        mStopFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isFullScreen()) {
                    Activity activity = PlayerUtils.scanForActivity(getContext());
                    if (activity != null && !activity.isFinishing()) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mMediaPlayer.stopFullScreen();
                    }
                }
            }
        });
        setClickable(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void onPlayStateChange(int playState) {
        if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(VISIBLE);
            mStopFullscreen.setVisibility(mMediaPlayer.isFullScreen() ? VISIBLE : GONE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChange(int playerState) {
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mStopFullscreen.setVisibility(VISIBLE);
        } else if (playerState == VideoView.PLAYER_NORMAL) {
            mStopFullscreen.setVisibility(GONE);
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
    public int setProgress() {
        return 0;
    }

    @Override
    public void onOrientationChanged() {

    }

    @Override
    public void setAdaptCutout(boolean adaptCutout, int space) {
        mAdaptCutout = adaptCutout;
        mSpace = space;
    }

    protected void adjustPortrait() {
        FrameLayout.LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
        sflp.setMargins(0, 0, 0, 0);
    }

    protected void adjustLandscape() {
        FrameLayout.LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
        sflp.setMargins(mSpace, 0, 0, 0);
    }

    protected void adjustReserveLandscape() {
        FrameLayout.LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
        sflp.setMargins(0, 0, 0, 0);
    }
}
