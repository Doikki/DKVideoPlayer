package xyz.doikki.dkplayer.widget.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.util.PIPManager;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.controller.ControlWrapper;

public class PipControlView extends FrameLayout implements ControlComponent, View.OnClickListener {

    private ControlWrapper mControlWrapper;

    private final ImageView mPlay;
    private final ImageView mClose;
    private final ProgressBar mLoading;

    public PipControlView(@NonNull Context context) {
        super(context);
    }

    public PipControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PipControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_float_controller, this, true);
        mPlay = findViewById(R.id.start_play);
        mLoading = findViewById(R.id.loading);
        mClose = findViewById(R.id.btn_close);
        mClose.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            PIPManager.getInstance().stopFloatWindow();
            PIPManager.getInstance().reset();
        } else if (id == R.id.start_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.btn_skip) {
            if (PIPManager.getInstance().getActClass() != null) {
                Intent intent = new Intent(getContext(), PIPManager.getInstance().getActClass());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        }
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
        if (isVisible) {
            if (mPlay.getVisibility() == VISIBLE)
                return;
            mPlay.setVisibility(VISIBLE);
        } else {
            if (mPlay.getVisibility() == GONE)
                return;
            mPlay.setVisibility(GONE);
        }
        mPlay.startAnimation(anim);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PAUSED:
                mPlay.setSelected(false);
                mPlay.setVisibility(VISIBLE);
                mLoading.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                mPlay.setSelected(true);
                mPlay.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                mPlay.setVisibility(GONE);
                mLoading.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PREPARED:
                mPlay.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                break;
            case VideoView.STATE_ERROR:
                mLoading.setVisibility(GONE);
                mPlay.setVisibility(GONE);
                bringToFront();
                break;
            case VideoView.STATE_BUFFERED:
                mPlay.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                mPlay.setSelected(mControlWrapper.isPlaying());
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                bringToFront();
                break;
        }
    }

    @Override
    public void onScreenModeChanged(int screenMode) {

    }

    @Override
    public void onProgressChanged(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

}
