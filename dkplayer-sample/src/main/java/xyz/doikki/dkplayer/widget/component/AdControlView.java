package xyz.doikki.dkplayer.widget.component;

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

import xyz.doikki.dkplayer.R;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class AdControlView extends FrameLayout implements ControlComponent, View.OnClickListener {

    protected TextView mAdTime, mAdDetail;
    protected ImageView mBack, mVolume, mFullScreen, mPlayButton;
    protected AdControlListener mListener;

    private ControlWrapper mControlWrapper;

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
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PLAYING:
                mControlWrapper.startProgress();
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

        //暂未实现全面屏适配逻辑，需要你自己补全
    }

    @Override
    public void onProgressChanged(int duration, int position) {
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
            mControlWrapper.togglePlay();
        }
    }

    private void doMute() {
        mControlWrapper.setMute(!mControlWrapper.isMute());
        mVolume.setImageResource(mControlWrapper.isMute() ? R.drawable.dkplayer_ic_action_volume_up : R.drawable.dkplayer_ic_action_volume_off);
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
    }

    public void setListener(AdControlListener listener) {
        this.mListener = listener;
    }

    public interface AdControlListener {

        void onAdClick();

        void onSkipAd();
    }
}
