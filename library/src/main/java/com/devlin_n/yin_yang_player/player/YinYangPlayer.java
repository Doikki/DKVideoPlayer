package com.devlin_n.yin_yang_player.player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.devlin_n.library.FloatWindowManager;
import com.devlin_n.yin_yang_player.R;
import com.devlin_n.yin_yang_player.controller.BaseVideoController;
import com.devlin_n.yin_yang_player.util.KeyUtil;
import com.devlin_n.yin_yang_player.util.NetworkUtil;
import com.devlin_n.yin_yang_player.util.WindowUtil;
import com.devlin_n.yin_yang_player.widget.StatusView;
import com.devlin_n.yin_yang_player.widget.YinYangSurfaceView;
import com.devlin_n.yin_yang_player.widget.YinYangTextureView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public class YinYangPlayer extends FrameLayout implements BaseVideoController.MediaPlayerControlInterface {

    private IMediaPlayer mMediaPlayer;//ijkPlayer
    @Nullable
    private BaseVideoController mVideoController;//控制器
    private YinYangSurfaceView mSurfaceView;
    private YinYangTextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;
    private FrameLayout playerContainer;
    private StatusView statusView;//显示错误信息的一个view
    private int bufferPercentage;//缓冲百分比
    private boolean isFullScreen;//是否处于全屏状态
    private boolean isMute;//是否静音
    private boolean useSurfaceView;//是否使用TextureView
    private boolean useAndroidMediaPlayer;//是否使用AndroidMediaPlayer

    private String mCurrentUrl;//当前播放视频的地址
    private List<VideoModel> mVideoModels;//列表播放数据
    private int mCurrentVideoPosition = 0;//列表播放时当前播放视频的在List中的位置
    private int mCurrentPosition;//当前正在播放视频的位置
    private String mCurrentTitle = "";//当前正在播放视频的标题
    private static boolean IS_PLAY_ON_MOBILE_NETWORK = false;//记录是否在移动网络下播放视频

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

    private int mCurrentState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器

    private AudioManager mAudioManager;//系统音频管理器
    @NonNull
    private AudioFocusHelper mAudioFocusHelper = new AudioFocusHelper();

    public static final int SCREEN_TYPE_DEFAULT = 0;
    public static final int SCREEN_TYPE_16_9 = 1;
    public static final int SCREEN_TYPE_4_3 = 2;
    public static final int SCREEN_TYPE_MATCH_PARENT = 3;
    public static final int SCREEN_TYPE_ORIGINAL = 4;

    private int mCurrentScreenType = SCREEN_TYPE_DEFAULT;

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener;
    protected boolean mAutoRotate;//是否旋转屏幕
    private boolean isLocked;
    private boolean mAlwaysFullScreen;//总是全屏
    private boolean isCache;
    private boolean addToPlayerManager;


    public YinYangPlayer(@NonNull Context context) {
        this(context, null);
    }


    public YinYangPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化播放器视图
     */
    private void initView() {
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
    private void initPlayer() {
        if (mMediaPlayer == null) {
            if (useAndroidMediaPlayer) {
                mMediaPlayer = new AndroidMediaPlayer();
            } else {
                mMediaPlayer = new IjkMediaPlayer();
                ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
                ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            }
            mAudioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnInfoListener(onInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mCurrentState = STATE_IDLE;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
        }
        if (useSurfaceView) {
            addSurfaceView();
        } else {
            addTextureView();
        }
        if (mDanmakuView != null) {
            playerContainer.removeView(mDanmakuView);
            playerContainer.addView(mDanmakuView, 1);
        }
    }

    /**
     * 开始准备播放（直接播放）
     */
    private void startPrepare() {
        if (mCurrentUrl == null || mCurrentUrl.trim().equals("")) return;
        try {
            if (isCache) {
                HttpProxyCacheServer cacheServer = getCacheServer();
                String proxyPath = cacheServer.getProxyUrl(mCurrentUrl);
                cacheServer.registerCacheListener(cacheListener, mCurrentUrl);
                if (cacheServer.isCached(mCurrentUrl)) {
                    bufferPercentage = 100;
                }
                mMediaPlayer.setDataSource(proxyPath);
            } else {
                mMediaPlayer.setDataSource(mCurrentUrl);
            }
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            if (mVideoController != null) {
                mVideoController.setPlayState(mCurrentState);
                mVideoController.setPlayerState(isFullScreen ? PLAYER_FULL_SCREEN : PLAYER_NORMAL);
            }
            if (mDanmakuView != null) {
                mDanmakuView.prepare(mParser, mContext);
            }
        } catch (IOException e) {
            mCurrentState = STATE_ERROR;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            e.printStackTrace();
        }
    }

    private HttpProxyCacheServer getCacheServer() {
        return VideoCacheManager.getProxy(getContext().getApplicationContext());
    }

    /**
     * 添加SurfaceView
     */
    private void addSurfaceView() {
        playerContainer.removeView(mSurfaceView);
        mSurfaceView = new YinYangSurfaceView(getContext());
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
        mTextureView = new YinYangTextureView(getContext());
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

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            if (mAlwaysFullScreen) startFullScreenDirectly();
            if (addToPlayerManager) {
                YinYangPlayerManager.instance().releaseVideoView();
                YinYangPlayerManager.instance().setCurrentVideoView(this);
            }
            if (mAutoRotate && orientationEventListener != null) orientationEventListener.enable();
            if (checkNetwork()) return;
            initPlayer();
            startPrepare();
        } else if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            if (mVideoController != null) {
                mVideoController.setPlayState(mCurrentState);
            }
            if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
                mDanmakuView.resume();
            }
        }
        setKeepScreenOn(true);
        mAudioFocusHelper.requestFocus();
    }

    private boolean checkNetwork() {
        if (NetworkUtil.getNetworkType(getContext()) == NetworkUtil.NETWORK_MOBILE && !IS_PLAY_ON_MOBILE_NETWORK) {
            playerContainer.removeView(statusView);
            if (statusView == null) {
                statusView = new StatusView(getContext());
            }
            statusView.setMessage(getResources().getString(R.string.wifi_tip));
            statusView.setButtonTextAndAction(getResources().getString(R.string.continue_play), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IS_PLAY_ON_MOBILE_NETWORK = true;
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
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
                setKeepScreenOn(false);
                mAudioFocusHelper.abandonFocus();
            }
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.pause();
            }
        }
    }

    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying() && mCurrentState != STATE_PLAYBACK_COMPLETED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            mAudioFocusHelper.requestFocus();
            setKeepScreenOn(true);
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            //起一个线程来释放播放器，解决列表播放卡顿问题
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }).start();
            mCurrentState = STATE_IDLE;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mAutoRotate && orientationEventListener != null) {
            orientationEventListener.disable();
        }
        if (isCache) getCacheServer().unregisterCacheListener(cacheListener);

        playerContainer.removeView(mTextureView);
        playerContainer.removeView(mSurfaceView);
        playerContainer.removeView(statusView);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        isLocked = false;
        mCurrentPosition = 0;
    }

    /**
     * 添加弹幕
     */
    public YinYangPlayer addDanmukuView(DanmakuView danmakuView, DanmakuContext context, BaseDanmakuParser parser) {
        this.mDanmakuView = danmakuView;
        this.mContext = context;
        this.mParser = parser;
        return this;
    }

    /**
     * 启用{@link AndroidMediaPlayer},如不调用默认使用 {@link IjkMediaPlayer}
     */
    public YinYangPlayer useAndroidMediaPlayer() {
        this.useAndroidMediaPlayer = true;
        return this;
    }

    /**
     * 设置视频比例
     */
    public YinYangPlayer setScreenType(int type) {
        this.mCurrentScreenType = type;
        if (mSurfaceView != null) mSurfaceView.setScreenType(type);
        if (mTextureView != null) mTextureView.setScreenType(type);
        return this;
    }

    /**
     * 设置视频地址
     */
    public YinYangPlayer setUrl(String url) {
        this.mCurrentUrl = url;
        return this;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public YinYangPlayer skipPositionWhenPlay(String url, int position) {
        this.mCurrentUrl = url;
        this.mCurrentPosition = position;
        return this;
    }

    /**
     * 设置一个列表的视频
     */
    public YinYangPlayer setVideos(List<VideoModel> videoModels) {
        this.mVideoModels = videoModels;
        playNext();
        return this;
    }

    /**
     * 设置标题
     */
    public YinYangPlayer setTitle(String title) {
        if (title != null) {
            this.mCurrentTitle = title;
        }
        return this;
    }

    /**
     * 开启缓存
     */
    public YinYangPlayer enableCache() {
        isCache = true;
        return this;
    }

    /**
     * 添加到{@link YinYangPlayerManager},如需集成到RecyclerView或ListView请开启此选项
     */
    public YinYangPlayer addToPlayerManager() {
        addToPlayerManager = true;
        return this;
    }

    /**
     * 播放下一条视频
     */
    private void playNext() {
        VideoModel videoModel = mVideoModels.get(mCurrentVideoPosition);
        if (videoModel != null) {
            mCurrentUrl = videoModel.url;
            mCurrentTitle = videoModel.title;
            mCurrentPosition = 0;
            setVideoController(videoModel.controller);
        }
    }

    /**
     * 启用SurfaceView
     */
    public YinYangPlayer useSurfaceView() {
        this.useSurfaceView = true;
        return this;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = (int) mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
            if (mDanmakuView != null) mDanmakuView.seekTo((long) pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return bufferPercentage;
        }
        return 0;
    }

    /**
     * 开始画中画播放，点播视频会记录播放位置
     */
    @Override
    public void startFloatWindow() {

        if (FloatWindowManager.getInstance().checkPermission(getContext())) {
            startBackgroundService();
        } else {
            FloatWindowManager.getInstance().applyPermission(getContext());
        }
    }

    /**
     * 启动画中画播放的后台服务
     */
    private void startBackgroundService() {
        if (!isInPlaybackState()) return;
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.URL, mCurrentUrl);
        getCurrentPosition();
        intent.putExtra(KeyUtil.POSITION, getDuration() <= 0 ? 0 : mCurrentPosition);
        intent.putExtra(KeyUtil.ENABLE_CACHE, isCache);
        getContext().getApplicationContext().startService(intent);
        WindowUtil.getAppCompActivity(getContext()).finish();
    }

    /**
     * 关闭画中画
     */
    @Override
    public void stopFloatWindow() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        getContext().getApplicationContext().stopService(intent);
    }

    /**
     * 直接开始全屏播放
     */
    @Override
    public void startFullScreenDirectly() {
        WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startFullScreen();
        if (mVideoController != null) mVideoController.startFullScreenDirectly();
    }

    public YinYangPlayer alwaysFullScreen() {
        mAlwaysFullScreen = true;
        return this;
    }

    /**
     * 播放下一条视频，可用于跳过广告
     */
    @Override
    public void skipToNext() {
        mCurrentVideoPosition++;
        if (mVideoModels != null && mVideoModels.size() > 1) {
            if (mCurrentVideoPosition >= mVideoModels.size()) return;
            playNext();
            resetPlayer();
            startPrepare();
        }
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute() {
        if (isMute) {
            mMediaPlayer.setVolume(1, 1);
            isMute = false;
        } else {
            mMediaPlayer.setVolume(0, 0);
            isMute = true;
        }
    }

    @Override
    public boolean isMute() {
        return isMute;
    }

    @Override
    public void setLock(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public void startFullScreen() {
        if (isFullScreen) return;
        WindowUtil.hideSupportActionBar(getContext(), true, true);
        WindowUtil.hideNavKey(getContext());
        this.removeView(playerContainer);
        ViewGroup contentView = (ViewGroup) WindowUtil.getAppCompActivity(getContext())
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(playerContainer, params);
        isFullScreen = true;
        if (mVideoController != null) mVideoController.setPlayerState(PLAYER_FULL_SCREEN);
    }

    @Override
    public void stopFullScreen() {
        if (!isFullScreen) return;
        WindowUtil.showSupportActionBar(getContext(), true, true);
        WindowUtil.showNavKey(getContext());
        ViewGroup contentView = (ViewGroup) WindowUtil.getAppCompActivity(getContext())
                .findViewById(android.R.id.content);
        contentView.removeView(playerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(playerContainer, params);
        isFullScreen = false;
        if (mVideoController != null) mVideoController.setPlayerState(PLAYER_NORMAL);
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public String getTitle() {
        return mCurrentTitle;
    }


    /**
     * 设置控制器
     */
    public YinYangPlayer setVideoController(BaseVideoController mediaController) {
        playerContainer.removeView(mVideoController);
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            mVideoController = mediaController;
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            playerContainer.addView(mVideoController, params);
        }
        return this;
    }

    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            mCurrentState = STATE_ERROR;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            mCurrentPosition = getCurrentPosition();
            playerContainer.removeView(statusView);
            if (statusView == null) {
                statusView = new StatusView(getContext());
            }
            statusView.setMessage(getResources().getString(R.string.error_message));
            statusView.setButtonTextAndAction(getResources().getString(R.string.retry), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerContainer.removeView(statusView);
                    resetPlayer();
                    startPrepare();
                }
            });
            playerContainer.addView(statusView);
            return true;
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            setKeepScreenOn(false);
            mCurrentVideoPosition++;
            if (mVideoModels != null && mVideoModels.size() > 1) {
                if (mCurrentVideoPosition >= mVideoModels.size()) {
                    return;
                }
                playNext();
                resetPlayer();
                startPrepare();
            }
        }
    };

    private void resetPlayer() {
        mMediaPlayer.reset();
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            initPlayer();
        } else {
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnInfoListener(onInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        }
    }

    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            switch (what) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mCurrentState = STATE_BUFFERING;
                    if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mCurrentState = STATE_BUFFERED;
                    if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
                    break;
                case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                    if (getWindowVisibility() != VISIBLE) pause();
                    break;
                case IjkMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    if (mTextureView != null)
                        mTextureView.setRotation(extra);
                    break;
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            if (!isCache) bufferPercentage = i;
        }
    };

    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_PREPARED;
            if (mVideoController != null) mVideoController.setPlayState(mCurrentState);
            if (mCurrentPosition > 0) {
                seekTo(mCurrentPosition);
            }
            start();
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                if (useSurfaceView) {
                    mSurfaceView.setScreenType(mCurrentScreenType);
                    mSurfaceView.setVideoSize(videoWidth, videoHeight);
                } else {
                    mTextureView.setScreenType(mCurrentScreenType);
                    mTextureView.setVideoSize(videoWidth, videoHeight);
                }

            }
        }
    };

    private CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            bufferPercentage = percentsAvailable;
        }
    };

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        if (mVideoController != null && isLocked) {
            mVideoController.show();
            Toast.makeText(getContext(), R.string.lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mAlwaysFullScreen) return false;
        if (isFullScreen) {
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            stopFullScreen();
            if (mVideoController != null)
                mVideoController.setPlayerState(PLAYER_NORMAL);
            return true;
        }
        return false;
    }

    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        boolean startRequested = false;
        boolean pausedForLoss = false;
        int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
            }
        }

        /**
         * Requests to obtain the audio focus
         *
         * @return True if the focus was granted
         */
        boolean requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (mAudioManager == null) {
                return false;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            startRequested = true;
            return false;
        }

        /**
         * Requests the system to drop the audio focus
         *
         * @return True if the focus was lost
         */
        boolean abandonFocus() {

            if (mAudioManager == null) {
                return false;
            }

            startRequested = false;
            int status = mAudioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }

    /**
     * 设置自动旋转
     */
    public YinYangPlayer autoRotate() {
        this.mAutoRotate = true;
        orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕

            private int CurrentOrientation = 0;
            private static final int PORTRAIT = 1;
            private static final int LANDSCAPE = 2;
            private static final int REVERSE_LANDSCAPE = 3;

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 340) { //屏幕顶部朝上
                    if (isLocked || mAlwaysFullScreen) return;
                    if (CurrentOrientation == PORTRAIT) return;
                    if ((CurrentOrientation == LANDSCAPE || CurrentOrientation == REVERSE_LANDSCAPE) && !isFullScreen()) {
                        CurrentOrientation = PORTRAIT;
                        return;
                    }
                    CurrentOrientation = PORTRAIT;
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    stopFullScreen();
                } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
                    if (CurrentOrientation == LANDSCAPE) return;
                    if (CurrentOrientation == PORTRAIT && isFullScreen()) {
                        CurrentOrientation = LANDSCAPE;
                        return;
                    }
                    CurrentOrientation = LANDSCAPE;
                    if (!isFullScreen()) {
                        startFullScreen();
                    }
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
                    if (CurrentOrientation == REVERSE_LANDSCAPE) return;
                    if (CurrentOrientation == PORTRAIT && isFullScreen()) {
                        CurrentOrientation = REVERSE_LANDSCAPE;
                        return;
                    }
                    CurrentOrientation = REVERSE_LANDSCAPE;
                    if (!isFullScreen()) {
                        startFullScreen();
                    }
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            }
        };
        return this;
    }
}
