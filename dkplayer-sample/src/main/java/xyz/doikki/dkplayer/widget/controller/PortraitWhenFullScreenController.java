package xyz.doikki.dkplayer.widget.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.dkplayer.R;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.controller.MediaPlayerControl;

public class PortraitWhenFullScreenController extends StandardVideoController {

    private View mFullScreen;

    public PortraitWhenFullScreenController(@NonNull Context context) {
        this(context, null);
    }

    public PortraitWhenFullScreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PortraitWhenFullScreenController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        VodControlView vodControlView = new VodControlView(getContext());
        vodControlView.showBottomProgress(false);
        mFullScreen = vodControlView.findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullScreen();
            }
        });
        addControlComponent(vodControlView);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        super.setMediaPlayer(mediaPlayer);
        //不监听设备方向
        mOrientationHelper.setOnOrientationChangeListener(null);
    }

    @Override
    protected void toggleFullScreen() {
        if (mActivity == null) return;
        int o = mActivity.getRequestedOrientation();
        if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mFullScreen.setSelected(o != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        adjustView();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (!mControlWrapper.isFullScreen()) {
            mControlWrapper.startFullScreen();
            return true;
        }
        mControlWrapper.toggleShowState();
        return true;
    }

    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mFullScreen.setSelected(false);
        } else {
            hide();
        }

        adjustView();

    }

    private void adjustView() {
        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setPadding(0, cutoutHeight, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setPadding(cutoutHeight, 0, 0, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen) {
            toggleFullScreen();
        } else if (i == R.id.lock) {
            mControlWrapper.toggleLockState();
        } else if (i == R.id.iv_play) {
            togglePlay();
        } else if (i == R.id.back) {
            stopFullScreen();
        } else if (i == R.id.thumb) {
            mControlWrapper.start();
            mControlWrapper.startFullScreen();
        } else if (i == R.id.iv_replay) {
            mControlWrapper.replay(true);
            mControlWrapper.startFullScreen();
        }
    }
}