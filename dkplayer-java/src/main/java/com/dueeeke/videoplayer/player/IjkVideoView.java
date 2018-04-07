package com.dueeeke.videoplayer.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.util.Constants;
import com.dueeeke.videoplayer.util.NetworkUtil;
import com.dueeeke.videoplayer.util.WindowUtil;
import com.dueeeke.videoplayer.widget.ResizeSurfaceView;
import com.dueeeke.videoplayer.widget.ResizeTextureView;
import com.dueeeke.videoplayer.widget.StatusView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkVideoView extends BaseIjkVideoView {
    protected ResizeSurfaceView mSurfaceView;
    protected ResizeTextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;
    protected FrameLayout playerContainer;
    protected StatusView statusView;//显示错误信息的一个view
    protected boolean isFullScreen;//是否处于全屏状态

    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    public static final int SCREEN_SCALE_ORIGINAL = 4;

    protected int mCurrentScreenScale = SCREEN_SCALE_DEFAULT;

    public IjkVideoView(@NonNull Context context) {
        this(context, null);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * 初始化播放器视图
     */
    protected void initView() {
        Constants.SCREEN_HEIGHT = WindowUtil.getScreenHeight(getContext(), false);
        Constants.SCREEN_WIDTH = WindowUtil.getScreenWidth(getContext());
        playerContainer = new FrameLayout(getContext());
        playerContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(playerContainer, params);
    }

    /**
     * 创建播放器实例，设置播放地址及播放器参数
     */
    @Override
    protected void initPlayer() {
        super.initPlayer();
        mMediaPlayer.setEnableMediaCodec(mPlayerConfig.enableMediaCodec);
        addDisplay();
    }

    protected void addDisplay() {
        if (mPlayerConfig.usingSurfaceView) {
            addSurfaceView();
        } else {
            addTextureView();
        }
    }

    @Override
    protected void setPlayState(int playState) {
        if (mVideoController != null) mVideoController.setPlayState(playState);
    }

    @Override
    protected void setPlayerState(int playerState) {
        if (mVideoController != null) mVideoController.setPlayerState(playerState);
    }

    @Override
    protected void startPlay() {
        if (mPlayerConfig.addToPlayerManager) {
            VideoViewManager.instance().releaseVideoPlayer();
            VideoViewManager.instance().setCurrentVideoPlayer(this);
        }
        if (checkNetwork()) return;
        super.startPlay();
    }

    /**
     * 添加SurfaceView
     */
    private void addSurfaceView() {
        playerContainer.removeView(mSurfaceView);
        mSurfaceView = new ResizeSurfaceView(getContext());
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        playerContainer.addView(mSurfaceView, 0, params);
    }

    /**
     * 添加TextureView
     */
    private void addTextureView() {
        playerContainer.removeView(mTextureView);
        mSurfaceTexture = null;
        mTextureView = new ResizeTextureView(getContext());
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                if (mSurfaceTexture != null) {
                    mTextureView.setSurfaceTexture(mSurfaceTexture);
                } else {
                    mSurfaceTexture = surfaceTexture;
                    mMediaPlayer.setSurface(new Surface(surfaceTexture));
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return mSurfaceTexture == null;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        playerContainer.addView(mTextureView, 0, params);
    }


    protected boolean checkNetwork() {
        if (NetworkUtil.getNetworkType(getContext()) == NetworkUtil.NETWORK_MOBILE && !Constants.IS_PLAY_ON_MOBILE_NETWORK) {
            playerContainer.removeView(statusView);
            if (statusView == null) {
                statusView = new StatusView(getContext());
            }
            statusView.setMessage(getResources().getString(R.string.wifi_tip));
            statusView.setButtonTextAndAction(getResources().getString(R.string.continue_play), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.IS_PLAY_ON_MOBILE_NETWORK = true;
                    playerContainer.removeView(statusView);
                    initPlayer();
                    startPrepare();
                }
            });
            playerContainer.addView(statusView);
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        super.release();
        playerContainer.removeView(mTextureView);
        playerContainer.removeView(mSurfaceView);
        playerContainer.removeView(statusView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentScreenScale = SCREEN_SCALE_DEFAULT;
    }

    @Override
    public void startFullScreen() {
        if (mVideoController == null) return;
        Activity activity = WindowUtil.scanForActivity(mVideoController.getContext());
        if (activity == null) return;
        if (isFullScreen) return;
        WindowUtil.hideSystemBar(mVideoController.getContext());
        this.removeView(playerContainer);
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(playerContainer, params);
        orientationEventListener.enable();
        isFullScreen = true;
        mVideoController.setPlayerState(PLAYER_FULL_SCREEN);
        mCurrentPlayerState = PLAYER_FULL_SCREEN;
    }

    @Override
    public void stopFullScreen() {
        if (mVideoController == null) return;
        Activity activity = WindowUtil.scanForActivity(mVideoController.getContext());
        if (activity == null) return;
        if (!isFullScreen) return;
        if (!mPlayerConfig.mAutoRotate) orientationEventListener.disable();
        WindowUtil.showSystemBar(mVideoController.getContext());
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        contentView.removeView(playerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(playerContainer, params);
        isFullScreen = false;
        mVideoController.setPlayerState(PLAYER_NORMAL);
        mCurrentPlayerState = PLAYER_NORMAL;
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if (mPlayerConfig.usingAndroidMediaPlayer) mMediaPlayer.start();
    }

    @Override
    public void onError() {
        super.onError();
        playerContainer.removeView(statusView);
        if (statusView == null) {
            statusView = new StatusView(getContext());
        }
        statusView.setMessage(getResources().getString(R.string.error_message));
        statusView.setButtonTextAndAction(getResources().getString(R.string.retry), new OnClickListener() {
            @Override
            public void onClick(View v) {
                playerContainer.removeView(statusView);
                addDisplay();
                startPrepare();
            }
        });
        playerContainer.addView(statusView);
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        switch (what) {
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mTextureView != null)
                    mTextureView.setRotation(extra);
                break;
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        if (mPlayerConfig.usingSurfaceView) {
            mSurfaceView.setScreenScale(mCurrentScreenScale);
            mSurfaceView.setVideoSize(videoWidth, videoHeight);
        } else {
            mTextureView.setScreenScale(mCurrentScreenScale);
            mTextureView.setVideoSize(videoWidth, videoHeight);
        }
    }

    /**
     * 设置控制器
     */
    public void setVideoController(@Nullable BaseVideoController mediaController) {
        playerContainer.removeView(mVideoController);
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            mVideoController = mediaController;
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            playerContainer.addView(mVideoController, params);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    /**
     * 设置视频地址
     */
    public void setUrl(String url) {
        this.mCurrentUrl = url;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public void skipPositionWhenPlay(String url, int position) {
        this.mCurrentUrl = url;
        this.mCurrentPosition = position;
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        if (title != null) {
            this.mCurrentTitle = title;
        }
    }

    /**
     * 设置视频比例
     */
    @Override
    public void setScreenScale(int screenScale) {
        this.mCurrentScreenScale = screenScale;
        if (mSurfaceView != null) mSurfaceView.setScreenScale(screenScale);
        if (mTextureView != null) mTextureView.setScreenScale(screenScale);
    }
}
