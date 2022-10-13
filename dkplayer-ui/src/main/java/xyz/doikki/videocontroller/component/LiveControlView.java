package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 直播底部控制栏
 * <p>
 * 此控件不适配TV模式
 */
public class LiveControlView extends BaseControlComponent implements ControlComponent, View.OnClickListener {

    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private ImageView mPlayButton;

    public LiveControlView(@NonNull Context context) {
        super(context);
    }

    public LiveControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupViews() {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_live_control_view, this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.bottom_container);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        ImageView refresh = findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(this);
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case DKVideoView.STATE_IDLE:
            case DKVideoView.STATE_START_ABORT:
            case DKVideoView.STATE_PREPARING:
            case DKVideoView.STATE_PREPARED:
            case DKVideoView.STATE_ERROR:
            case DKVideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
            case DKVideoView.STATE_PLAYING:
                mPlayButton.setSelected(true);
                break;
            case DKVideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case DKVideoView.STATE_BUFFERING:
            case DKVideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        switch (screenMode) {
            case DKVideoView.SCREEN_MODE_NORMAL:
                mFullScreen.setSelected(false);
                break;
            case DKVideoView.SCREEN_MODE_FULL:
                mFullScreen.setSelected(true);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            mControlWrapper.toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.iv_refresh) {
            mControlWrapper.replay(true);
        }
    }
}
