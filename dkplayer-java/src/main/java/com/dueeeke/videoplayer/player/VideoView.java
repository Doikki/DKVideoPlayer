package com.dueeeke.videoplayer.player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.listener.PlayerEventListener;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.dueeeke.videoplayer.widget.ResizeSurfaceView;
import com.dueeeke.videoplayer.widget.ResizeTextureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public class VideoView extends FrameLayout implements MediaPlayerControl, PlayerEventListener {
    protected AbstractPlayer mMediaPlayer;//播放器
    protected PlayerFactory mPlayerFactory;//工厂类，用于实例化播放核心
    @Nullable
    protected BaseVideoController mVideoController;//控制器

    protected ResizeSurfaceView mSurfaceView;
    protected ResizeTextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;
    /**
     * 真正承载播放器视图的容器
     */
    protected FrameLayout mPlayerContainer;
    protected boolean mIsFullScreen;//是否处于全屏状态
    //通过添加和移除这个view来实现隐藏和显示系统navigation bar，可以避免出现一些奇奇怪怪的问题
    protected View mHideNavBarView;
    protected static final int FULLSCREEN_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    public static final int SCREEN_SCALE_ORIGINAL = 4;
    public static final int SCREEN_SCALE_CENTER_CROP = 5;

    protected int mCurrentScreenScale = SCREEN_SCALE_DEFAULT;

    protected int[] mVideoSize = {0, 0};

    protected boolean mIsTinyScreen;//是否处于小屏状态
    protected int[] mTinyScreenSize = {0, 0};

    protected boolean mIsMute;//是否静音

    protected String mCurrentUrl;//当前播放视频的地址
    protected Map<String, String> mHeaders;//当前视频地址的请求头
    protected AssetFileDescriptor mAssetFileDescriptor;//assets文件
    protected long mCurrentPosition;//当前正在播放视频的位置

    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    protected int mCurrentPlayState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_SCREEN = 12;   // 小屏播放器
    protected int mCurrentPlayerState = PLAYER_NORMAL;

    protected AudioManager mAudioManager;//系统音频管理器
    @Nullable
    protected AudioFocusHelper mAudioFocusHelper;

    protected int mCurrentOrientation = 0;
    protected static final int PORTRAIT = 1;
    protected static final int LANDSCAPE = 2;
    protected static final int REVERSE_LANDSCAPE = 3;

    protected boolean mIsLockFullScreen;//是否锁定屏幕

    protected List<OnVideoViewStateChangeListener> mOnVideoViewStateChangeListeners;

    @Nullable
    protected ProgressManager mProgressManager;

    protected boolean mAutoRotate;

    protected boolean mUsingSurfaceView;//启用SurfaceView

    protected boolean mIsLooping;//循环洗脑播放

    protected boolean mEnableAudioFocus;//监听音频焦点变化

    protected boolean mEnableMediaCodec;//启用MediaCodec解码

    protected boolean mEnableParallelPlay;//支持多开

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //读取全局配置
        VideoViewConfig config = VideoViewManager.getConfig();
        mAutoRotate = config.mAutoRotate;
        mUsingSurfaceView = config.mUsingSurfaceView;
        mEnableMediaCodec = config.mEnableMediaCodec;
        mEnableAudioFocus = config.mEnableAudioFocus;
        mEnableParallelPlay = config.mEnableParallelPlay;
        mProgressManager = config.mProgressManager;
        //默认使用系统的MediaPlayer进行解码
        mPlayerFactory = config.mPlayerFactory == null ? AndroidMediaPlayerFactory.create(context) : config.mPlayerFactory;

        //读取xml中的配置，并综合全局配置
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VideoView);
        mAutoRotate = a.getBoolean(R.styleable.VideoView_autoRotate, mAutoRotate);
        mUsingSurfaceView = a.getBoolean(R.styleable.VideoView_usingSurfaceView, mUsingSurfaceView);
        mEnableAudioFocus = a.getBoolean(R.styleable.VideoView_enableAudioFocus, mEnableAudioFocus);
        mEnableMediaCodec = a.getBoolean(R.styleable.VideoView_enableMediaCodec, mEnableMediaCodec);
        mEnableParallelPlay = a.getBoolean(R.styleable.VideoView_enableParallelPlay, mEnableParallelPlay);
        mIsLooping = a.getBoolean(R.styleable.VideoView_looping, false);
        a.recycle();

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
     * 开始播放
     */
    @Override
    public void start() {
        if (isInIdleState()) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
        setKeepScreenOn(true);
        if (mAudioFocusHelper != null)
            mAudioFocusHelper.requestFocus();
    }

    /**
     * 第一次播放
     */
    protected void startPlay() {
        if (!mEnableParallelPlay) {
            VideoViewManager.instance().release();
        }
        VideoViewManager.instance().addVideoView(this);

        if (checkNetwork()) return;

        if (mEnableAudioFocus) {
            mAudioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioFocusHelper = new AudioFocusHelper();
        }

        if (mProgressManager != null) {
            mCurrentPosition = mProgressManager.getSavedProgress(mCurrentUrl);
        }

        if (mAutoRotate)
            mOrientationEventListener.enable();

        initPlayer();
        startPrepare(false);
    }

    protected boolean checkNetwork() {
        //播放本地文件、Asset、raw时不检测网络
        if (mAssetFileDescriptor != null) {
            return false;
        } else if (!TextUtils.isEmpty(mCurrentUrl)) {
            Uri uri = Uri.parse(mCurrentUrl);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())
                    || ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                return false;
            }
        }

        if (mVideoController != null
                && PlayerUtils.getNetworkType(getContext()) == PlayerUtils.NETWORK_WIFI
                && !VideoViewManager.instance().playOnMobileNetwork()) {
            mVideoController.showStatusView();
            return true;
        }
        return false;
    }

    /**
     * 初始化播放器
     */
    protected void initPlayer() {
        mMediaPlayer = mPlayerFactory.createPlayer();
        mMediaPlayer.bindVideoView(this);
        mMediaPlayer.initPlayer();
        mMediaPlayer.setEnableMediaCodec(mEnableMediaCodec);
        mMediaPlayer.setLooping(mIsLooping);
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

    /**
     * 开始准备播放（直接播放）
     */
    protected void startPrepare(boolean needReset) {
        if (TextUtils.isEmpty(mCurrentUrl) && mAssetFileDescriptor == null) return;
        if (needReset) mMediaPlayer.reset();
        if (mAssetFileDescriptor != null) {
            mMediaPlayer.setDataSource(mAssetFileDescriptor);
        } else {
            mMediaPlayer.setDataSource(mCurrentUrl, mHeaders);
        }
        mMediaPlayer.prepareAsync();
        setPlayState(STATE_PREPARING);
        setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : isTinyScreen() ? PLAYER_TINY_SCREEN : PLAYER_NORMAL);
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        mMediaPlayer.start();
        setPlayState(STATE_PLAYING);
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
            setPlayState(STATE_PAUSED);
            setKeepScreenOn(false);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (isInPlaybackState()
                && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setPlayState(STATE_PLAYING);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.requestFocus();
            setKeepScreenOn(true);
        }
    }

    /**
     * 停止播放
     *
     * @deprecated 使用release代替
     */
    @Deprecated
    public void stopPlayback() {
        release();
    }

    /**
     * 释放播放器
     */
    public void release() {
        VideoViewManager.instance().removeVideoView(this);
        if (mVideoController != null) {
            mVideoController.hideStatusView();
        }
        if (!isInIdleState()) {
            saveProgress();
            mMediaPlayer.release();
            mMediaPlayer = null;
            setKeepScreenOn(false);
            if (mAudioFocusHelper != null) {
                mAudioFocusHelper.abandonFocus();
            }
            mOrientationEventListener.disable();

            if (mTextureView != null) {
                mPlayerContainer.removeView(mTextureView);
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                    mSurfaceTexture = null;
                }
            } else if (mSurfaceView != null) {
                mPlayerContainer.removeView(mSurfaceView);
            }

            mIsLockFullScreen = false;
            mCurrentPosition = 0;
            mCurrentScreenScale = SCREEN_SCALE_DEFAULT;
            setPlayState(STATE_IDLE);
        }
    }

    /**
     * 保存播放进度
     */
    protected void saveProgress() {
        if (mProgressManager != null && isInPlaybackState())
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
    }

    /**
     * 是否处于播放状态
     */
    protected boolean isInPlaybackState() {
        return mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED;
    }

    protected boolean isInIdleState() {
        return mMediaPlayer == null
                || mCurrentPlayState == STATE_IDLE;
    }

    /**
     * 重新播放
     *
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
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        if (mMediaPlayer != null) {
            this.mIsMute = isMute;
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return mIsMute;
    }

    /**
     * 设置controller是否处于锁定状态
     */
    @Override
    public void setLock(boolean isLocked) {
        this.mIsLockFullScreen = isLocked;
    }

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError() {
        setPlayState(STATE_ERROR);
    }

    /**
     * 视频播放完成回调
     */
    @Override
    public void onCompletion() {
        setPlayState(STATE_PLAYBACK_COMPLETED);
        setKeepScreenOn(false);
        mCurrentPosition = 0;
    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_BUFFERING_START:
                setPlayState(STATE_BUFFERING);
                break;
            case AbstractPlayer.MEDIA_INFO_BUFFERING_END:
                setPlayState(STATE_BUFFERED);
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                setPlayState(STATE_PLAYING);
                if (getWindowVisibility() != VISIBLE) pause();
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mTextureView != null)
                    mTextureView.setRotation(extra);
                break;
        }
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        setPlayState(STATE_PREPARED);
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    /**
     * 获取当前播放器的状态
     */
    public int getCurrentPlayerState() {
        return mCurrentPlayerState;
    }

    /**
     * 获取当前的播放状态
     */
    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }


    /**
     * 获取缓冲速度
     */
    @Override
    public long getTcpSpeed() {
        return mMediaPlayer != null ? mMediaPlayer.getTcpSpeed() : 0;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    /**
     * 设置视频地址
     */
    public void setUrl(String url) {
        setUrl(url, null);
    }

    /**
     * 设置包含请求头信息的视频地址
     *
     * @param url     视频地址
     * @param headers 请求头
     */
    public void setUrl(String url, Map<String, String> headers) {
        mCurrentUrl = url;
        mHeaders = headers;
    }

    /**
     * 用于播放assets里面的视频文件
     */
    public void setAssetFileDescriptor(AssetFileDescriptor fd) {
        this.mAssetFileDescriptor = fd;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public void skipPositionWhenPlay(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * 设置音量 0.0f-1.0f 之间
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    public void setVolume(float v1, float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    /**
     * 设置进度管理器，用于保存播放进度
     */
    public void setProgressManager(@Nullable ProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    /**
     * 循环播放， 默认不循环播放
     */
    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    /**
     * 是否自动旋转， 默认不自动旋转
     */
    public void setAutoRotate(boolean autoRotate) {
        mAutoRotate = autoRotate;
    }

    /**
     * 是否启用SurfaceView，默认不启用
     */
    public void setUsingSurfaceView(boolean usingSurfaceView) {
        mUsingSurfaceView = usingSurfaceView;
    }

    /**
     * 是否开启AudioFocus监听， 默认开启
     */
    public void setEnableAudioFocus(boolean enableAudioFocus) {
        mEnableAudioFocus = enableAudioFocus;
    }

    /**
     * 是否使用MediaCodec进行解码（硬解码），默认不开启，使用软解
     */
    public void setEnableMediaCodec(boolean enableMediaCodec) {
        mEnableMediaCodec = enableMediaCodec;
    }

    /**
     * 自定义播放核心，继承{@link AbstractPlayer}实现自己的播放核心
     * @deprecated 使用 {@link #setPlayerFactory(PlayerFactory)} ()} 代替
     */
    @Deprecated
    public void setCustomMediaPlayer(@NonNull AbstractPlayer abstractPlayer) {
        mMediaPlayer = abstractPlayer;
    }

    /**
     * 自定义播放核心，继承{@link PlayerFactory}实现自己的播放核心
     */
    public void setPlayerFactory(PlayerFactory playerFactory) {
        if (playerFactory == null) {
            throw new IllegalArgumentException("PlayerFactory can not be null!");
        }
        mPlayerFactory = playerFactory;
    }

    /**
     * 设置调用{@link #start()}后在移动环境下是否继续播放，默认不继续播放
     *
     * @deprecated 使用 {@link VideoViewConfig}代替
     */
    @Deprecated
    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
    }

    /**
     * 添加到{@link VideoViewManager},如需集成到RecyclerView或ListView请开启此选项
     * 用于实现同一列表同时只播放一个视频
     *
     * @deprecated 默认会添加到VideoViewManager，不再需要调用此方法
     */
    @Deprecated
    public void addToVideoViewManager() {
    }

    /**
     * 支持多开
     */
    public void setEnableParallelPlay(boolean enableParallelPlay) {
        mEnableParallelPlay = enableParallelPlay;
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
        params.gravity = Gravity.BOTTOM | Gravity.END;
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
     *
     * @param tinyScreenSize 其中tinyScreenSize[0]是宽，tinyScreenSize[1]是高
     */
    public void setTinyScreenSize(int[] tinyScreenSize) {
        this.mTinyScreenSize = tinyScreenSize;
    }

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener mOrientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕
        private long mLastTime;

        @Override
        public void onOrientationChanged(int orientation) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime < 300) return;//300毫秒检测一次
            if (mVideoController == null) return;
            Activity activity = PlayerUtils.scanForActivity(mVideoController.getContext());
            if (activity == null) return;
            if (orientation >= 340) { //屏幕顶部朝上
                onOrientationPortrait(activity);
            } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
                onOrientationLandscape(activity);
            } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
                onOrientationReverseLandscape(activity);
            }
            mLastTime = currentTime;
        }
    };

    /**
     * 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        if (mIsLockFullScreen || !mAutoRotate || mCurrentOrientation == PORTRAIT)
            return;
        if ((mCurrentOrientation == LANDSCAPE || mCurrentOrientation == REVERSE_LANDSCAPE) && !isFullScreen()) {
            mCurrentOrientation = PORTRAIT;
            return;
        }
        mCurrentOrientation = PORTRAIT;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        stopFullScreen();
    }

    /**
     * 横屏
     */
    protected void onOrientationLandscape(Activity activity) {
        if (mCurrentOrientation == LANDSCAPE) return;
        if (mCurrentOrientation == PORTRAIT
                && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                && isFullScreen()) {
            mCurrentOrientation = LANDSCAPE;
            return;
        }
        mCurrentOrientation = LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        if (mCurrentOrientation == REVERSE_LANDSCAPE) return;
        if (mCurrentOrientation == PORTRAIT
                && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && isFullScreen()) {
            mCurrentOrientation = REVERSE_LANDSCAPE;
            return;
        }
        mCurrentOrientation = REVERSE_LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    /**
     * 向Controller设置播放状态，用于控制Controller的ui展示
     */
    protected void setPlayState(int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null)
            mVideoController.setPlayState(playState);
        if (mOnVideoViewStateChangeListeners != null) {
            for (int i = 0, z = mOnVideoViewStateChangeListeners.size(); i < z; i++) {
                OnVideoViewStateChangeListener listener = mOnVideoViewStateChangeListeners.get(i);
                if (listener != null) {
                    listener.onPlayStateChanged(playState);
                }
            }
        }
    }

    /**
     * 向Controller设置播放器状态，包含全屏状态和非全屏状态
     */
    protected void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
        if (mVideoController != null)
            mVideoController.setPlayerState(playerState);
        if (mOnVideoViewStateChangeListeners != null) {
            for (int i = 0, z = mOnVideoViewStateChangeListeners.size(); i < z; i++) {
                OnVideoViewStateChangeListener listener = mOnVideoViewStateChangeListeners.get(i);
                if (listener != null) {
                    listener.onPlayerStateChanged(playerState);
                }
            }
        }
    }

    /**
     * 监听播放状态变化
     */
    public void addOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners == null) {
            mOnVideoViewStateChangeListeners = new ArrayList<>();
        }
        mOnVideoViewStateChangeListeners.add(listener);
    }

    /**
     * 移除播放状态监听
     */
    public void removeOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.remove(listener);
        }
    }

    /**
     * 设置播放状态监听
     */
    public void setOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners == null) {
            mOnVideoViewStateChangeListeners = new ArrayList<>();
        } else {
            mOnVideoViewStateChangeListeners.clear();
        }
        mOnVideoViewStateChangeListeners.add(listener);
    }

    /**
     * 移除所有播放状态监听
     */
    public void clearOnVideoViewStateChangeListeners() {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.clear();
        }
    }

    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        private boolean startRequested = false;
        private boolean pausedForLoss = false;
        private int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mMediaPlayer != null && !mIsMute)//恢复音量
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://焦点暂时丢失
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://此时需降低音量
                    if (mMediaPlayer != null && isPlaying() && !mIsMute) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        /**
         * Requests to obtain the audio focus
         */
        void requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return;
            }

            if (mAudioManager == null) {
                return;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return;
            }

            startRequested = true;
        }

        /**
         * Requests the system to drop the audio focus
         */
        void abandonFocus() {

            if (mAudioManager == null) {
                return;
            }

            startRequested = false;
            mAudioManager.abandonAudioFocus(this);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        L.d("onSaveInstanceState: " + mCurrentPosition);
        //activity切到后台后可能被系统回收，故在此处进行进度保存
        saveProgress();
        return super.onSaveInstanceState();
    }
}
