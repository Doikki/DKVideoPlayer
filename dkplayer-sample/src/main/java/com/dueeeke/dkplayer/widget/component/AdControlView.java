package com.dueeeke.dkplayer.widget.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class AdControlView extends FrameLayout implements IControlComponent, View.OnClickListener {

    protected TextView mAdTime, mAdDetail;
    protected ImageView mBack, mVolume, mFullScreen, mPlayButton;
    protected AdControlListener mListener;

    private ControlWrapper mMediaPlayer;

    public AdControlView(@NonNull Context context) {
        super(context);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ad_control_view, this, true);
        mAdTime = findViewById(R.id.ad_time);
        mAdDetail = findViewById(R.id.ad_detail);
        mAdDetail.setText("了解详情>");
        mBack = findViewById(R.id.back);
        mBack.setVisibility(GONE);
        mVolume = findViewById(R.id.iv_volume);
        mFullScreen = findViewById(R.id.fullscreen);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mAdTime.setOnClickListener(this);
        mAdDetail.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mVolume.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onAdClick();
            }
        });
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mMediaPlayer = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void show(Animation showAnim) {

    }

    @Override
    public void hide(Animation hideAnim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PLAYING:
                mMediaPlayer.startProgress();
                mPlayButton.setSelected(true);
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mBack.setVisibility(GONE);
                mFullScreen.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mBack.setVisibility(VISIBLE);
                mFullScreen.setSelected(true);
                break;
        }
    }

    @Override
    public void adjustView(int orientation, int space) {
        //暂未实现全面屏适配逻辑，需要你自己补全
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mAdTime != null)
            mAdTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back | id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_volume) {
            doMute();
        } else if (id == R.id.ad_detail) {
            if (mListener != null) mListener.onAdClick();
        } else if (id == R.id.ad_time) {
            if (mListener != null) mListener.onSkipAd();
        } else if (id == R.id.iv_play) {
            mMediaPlayer.togglePlay();
        }
    }

    private void doMute() {
        mMediaPlayer.setMute(!mMediaPlayer.isMute());
        mVolume.setImageResource(mMediaPlayer.isMute() ? R.drawable.dkplayer_ic_action_volume_up : R.drawable.dkplayer_ic_action_volume_off);
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mMediaPlayer.toggleFullScreen(activity);
    }

    public void setListener(AdControlListener listener) {
        this.mListener = listener;
    }

    public interface AdControlListener {

        void onAdClick();

        void onSkipAd();
    }
}
