package com.dueeeke.videoplayer.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.dueeeke.videoplayer.widget.ResizeSurfaceView;
import com.dueeeke.videoplayer.widget.ResizeTextureView;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkVideoView extends BaseIjkVideoView {
    protected ResizeSurfaceView mSurfaceView;
    protected ResizeTextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;
    protected FrameLayout mPlayerContainer;
    protected boolean mIsFullScreen;//是否处于全屏状态
    //通过添加和移除这个view来实现隐藏和显示系统navigation bar，可以避免出现一些奇奇怪怪的问题
    protected View mHideNavBarView;

    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    public static final int SCREEN_SCALE_ORIGINAL = 4;
    public static final int SCREEN_SCALE_CENTER_CROP = 5;

    protected int mCurrentScreenScale = SCREEN_SCALE_DEFAULT;

    protected int[] mVideoSize = {0, 0};

    protected static final int FULLSCREEN_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    protected boolean mIsTinyScreen;//是否处于小屏状态
    protected int[] mTinyScreenSize = {0, 0};

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
        mPlayerContainer = new FrameLayout(getContext());
        mPlayerContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);

        mHideNavBarView = new View(getContext());
        mHideNavBarView.setSystemUiVisibility(FULLSCREEN_FLAGS);
    }

    /**
     * 创建播放器实例，设置播放器参数，并且添加用于显示视频的View
     */
    @Override
    protected void initPlayer() {
        super.initPlayer();
        addDisplay();
    }

    protected void addDisplay() {
        if (mUsingSurfaceView || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            addSurfaceView();
        } else {
            addTextureView();
        }
    }

    /**
     * 添加SurfaceView
     */
    private void addSurfaceView() {
        mPlayerContainer.removeView(mSurfaceView);
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
        mPlayerContainer.addView(mSurfaceView, 0, params);
    }

    /**
     * 添加TextureView
     */
    private void addTextureView() {
        mPlayerContainer.removeView(mTextureView);
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
        mPlayerContainer.addView(mTextureView, 0, params);
    }

    @Override
    public void release() {
        super.release();
        mPlayerContainer.removeView(mTextureView);
        mPlayerContainer.removeView(mSurfaceView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentScreenScale = SCREEN_SCALE_DEFAULT;
    }

    /**
     * 进入全屏
     */
    @Override
    public void startFullScreen() {
        if (mIsFullScreen) return;
        if (mVideoController == null) return;
        Activity activity = PlayerUtils.scanForActivity(mVideoController.getContext());
        if (activity == null) return;
        //隐藏ActionBar
        PlayerUtils.hideActionBar(activity);
        //隐藏NavigationBar
        this.addView(mHideNavBarView);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //从当前FrameLayout中移除播放器视图
        this.removeView(mPlayerContainer);
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //将播放器视图添加到ContentView（就是setContentView的ContentView）中即实现了全屏
        contentView.addView(mPlayerContainer, params);
        mOrientationEventListener.enable();
        mIsFullScreen = true;
        setPlayerState(PLAYER_FULL_SCREEN);
    }

    /**
     * 退出全屏
     */
    @Override
    public void stopFullScreen() {
        if (!mIsFullScreen) return;
        if (mVideoController == null) return;
        Activity activity = PlayerUtils.scanForActivity(mVideoController.getContext());
        if (activity == null) return;
        if (!mAutoRotate) mOrientationEventListener.disable();
        //显示ActionBar
        PlayerUtils.showActionBar(activity);
        //显示NavigationBar
        this.removeView(mHideNavBarView);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        //把播放器视图从ContentView（就是setContentView的ContentView）中移除
        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //将播放器视图添加到当前FrameLayout中即退出了全屏
        this.addView(mPlayerContainer, params);
        mIsFullScreen = false;
        setPlayerState(PLAYER_NORMAL);
    }

    /**
     * 判断是否处于全屏状态
     */
    @Override
    public boolean isFullScreen() {
        return mIsFullScreen;
    }


    /**
     * 开启小屏
     */
    public void startTinyScreen() {
        if (mIsTinyScreen) return;
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        mOrientationEventListener.disable();
        this.removeView(mPlayerContainer);
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = PlayerUtils.getScreenWidth(activity, false) / 2;
        }

        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = width * 9 / 16;
        }

        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM|Gravity.END;
        contentView.addView(mPlayerContainer, params);
        mIsTinyScreen = true;
        setPlayerState(PLAYER_TINY_SCREEN);
    }

    /**
     * 退出小屏
     */
    public void stopTinyScreen() {
        if (!mIsTinyScreen) return;

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;

        ViewGroup contentView = activity.findViewById(android.R.id.content);
        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);
        if (mAutoRotate) mOrientationEventListener.enable();

        mIsTinyScreen = false;
        setPlayerState(PLAYER_NORMAL);
    }

    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mTextureView != null)
                    mTextureView.setRotation(extra);
                break;
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mVideoSize[0] = videoWidth;
        mVideoSize[1] = videoHeight;
        if (mUsingSurfaceView || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mSurfaceView.setScreenScale(mCurrentScreenScale);
            mSurfaceView.setVideoSize(videoWidth, videoHeight);
        } else {
            mTextureView.setScreenScale(mCurrentScreenScale);
            mTextureView.setVideoSize(videoWidth, videoHeight);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //重新获得焦点时保持全屏状态
            mHideNavBarView.setSystemUiVisibility(FULLSCREEN_FLAGS);
        }

        if (isInPlaybackState() && (mAutoRotate || mIsFullScreen)) {
            if (hasFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOrientationEventListener.enable();
                    }
                }, 800);
            } else {
                mOrientationEventListener.disable();
            }
        }
    }

    /**
     * 重新播放
     * @param resetPosition 是否从头开始播放
     */
    @Override
    public void replay(boolean resetPosition) {
        if (resetPosition) {
            mCurrentPosition = 0;
        }
        addDisplay();
        startPrepare(true);
    }

    /**
     * 设置控制器
     */
    public void setVideoController(@Nullable BaseVideoController mediaController) {
        mPlayerContainer.removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.addView(mVideoController, params);
        }
    }

    /**
     * 设置视频比例
     */
    @Override
    public void setScreenScale(int screenScale) {
        this.mCurrentScreenScale = screenScale;
        if (mSurfaceView != null) {
            mSurfaceView.setScreenScale(screenScale);
        } else if (mTextureView != null) {
            mTextureView.setScreenScale(screenScale);
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    @Override
    public void setMirrorRotation(boolean enable) {
        if (mTextureView != null) {
            mTextureView.setScaleX(enable ? -1 : 1);
        }
    }

    /**
     * 截图，暂不支持SurfaceView
     */
    @Override
    public Bitmap doScreenShot() {
        if (mTextureView != null) {
            return mTextureView.getBitmap();
        }
        return null;
    }

    /**
     * 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
     */
    @Override
    public int[] getVideoSize() {
        return mVideoSize;
    }

    /**
     * 旋转视频画面
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        if (mTextureView != null) {
            mTextureView.setRotation(rotation);
            mTextureView.requestLayout();
        }

        if (mSurfaceView != null) {
            mSurfaceView.setRotation(rotation);
            mSurfaceView.requestLayout();
        }
    }

    /**
     * 设置小屏的宽高
     * @param tinyScreenSize 其中tinyScreenSize[0]是宽，tinyScreenSize[1]是高
     */
    public void setTinyScreenSize(int[] tinyScreenSize) {
        this.mTinyScreenSize = tinyScreenSize;
    }
}
