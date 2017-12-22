package com.dueeeke.videoplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.listener.MediaEngineInterface;
import com.dueeeke.videoplayer.listener.VideoListener;
import com.dueeeke.videoplayer.util.Constants;
import com.dueeeke.videoplayer.util.KeyUtil;
import com.dueeeke.videoplayer.util.WindowUtil;

import java.io.File;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public abstract class BaseIjkVideoView extends FrameLayout implements BaseVideoController.MediaPlayerControl, MediaEngineInterface {

    protected BaseMediaEngine mMediaPlayer;//播放引擎
    protected VideoListener listener;
    protected int bufferPercentage;//缓冲百分比
    protected boolean isMute;//是否静音
    protected boolean useAndroidMediaPlayer;//是否使用AndroidMediaPlayer

    protected String mCurrentUrl;//当前播放视频的地址
    protected List<VideoModel> mVideoModels;//列表播放数据
    protected int mCurrentVideoPosition = 0;//列表播放时当前播放视频的在List中的位置
    protected int mCurrentPosition;//当前正在播放视频的位置
    protected String mCurrentTitle = "";//当前正在播放视频的标题

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

    protected int mCurrentState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器

    protected AudioManager mAudioManager;//系统音频管理器
    @NonNull
    protected AudioFocusHelper mAudioFocusHelper = new AudioFocusHelper();

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕

        private int CurrentOrientation = 0;
        private static final int PORTRAIT = 1;
        private static final int LANDSCAPE = 2;
        private static final int REVERSE_LANDSCAPE = 3;

        @Override
        public void onOrientationChanged(int orientation) {
            Activity activity = WindowUtil.scanForActivity(getContext());
            if (activity == null) return;
            if (orientation >= 340) { //屏幕顶部朝上
                if (mAlwaysFullScreen || isLockFullScreen || !mAutoRotate) return;
                if (CurrentOrientation == PORTRAIT) return;
                if ((CurrentOrientation == LANDSCAPE || CurrentOrientation == REVERSE_LANDSCAPE) && !isFullScreen()) {
                    CurrentOrientation = PORTRAIT;
                    return;
                }
                CurrentOrientation = PORTRAIT;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    };
    protected boolean mAutoRotate;//是否旋转屏幕
    protected boolean isLockFullScreen;//是否锁定屏幕
    protected boolean mAlwaysFullScreen;//总是全屏
    protected boolean isCache;//是否开启缓存
    protected boolean addToPlayerManager;//是否添加到播放管理器


    public BaseIjkVideoView(@NonNull Context context) {
        this(context, null);
    }


    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initPlayer() {
        if (mMediaPlayer == null) {
            if (useAndroidMediaPlayer) {
                mMediaPlayer = new AndroidMediaEngine();
                ((AndroidMediaEngine)mMediaPlayer).setMediaEngineInterface(this);
            } else {
                mMediaPlayer = new IjkMediaEngine();
                ((IjkMediaEngine)mMediaPlayer).setMediaEngineInterface(this);
            }
            mMediaPlayer.initPlayer();
        }
    }

    protected abstract void setPlayState(int playState);

    protected abstract void setPlayerState(int playerState);

    protected abstract boolean checkNetwork();

    /**
     * 开始准备播放（直接播放）
     */
    protected void startPrepare() {
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
            setPlayState(mCurrentState);
            setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : PLAYER_NORMAL);
        } catch (Exception e) {
            mCurrentState = STATE_ERROR;
            setPlayState(mCurrentState);
            e.printStackTrace();
        }
    }

    private HttpProxyCacheServer getCacheServer() {
        return VideoCacheManager.getProxy(getContext().getApplicationContext());
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
        setKeepScreenOn(true);
        mAudioFocusHelper.requestFocus();
    }

    protected void startPlay() {
        if (mAlwaysFullScreen) startFullScreenDirectly();
        if (mAutoRotate) orientationEventListener.enable();
        if (checkNetwork()) return;
        initPlayer();
        startPrepare();
    }

    protected void startInPlaybackState() {
        mMediaPlayer.start();
        mCurrentState = STATE_PLAYING;
        setPlayState(mCurrentState);
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                setPlayState(mCurrentState);
                setKeepScreenOn(false);
                mAudioFocusHelper.abandonFocus();
            }
        }
    }


    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying() && mCurrentState != STATE_PLAYBACK_COMPLETED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            setPlayState(mCurrentState);
            mAudioFocusHelper.requestFocus();
            setKeepScreenOn(true);
        }
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            setPlayState(mCurrentState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            //启动一个线程来释放播放器，解决列表播放卡顿问题
            new Thread(() -> {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }).start();
            mCurrentState = STATE_IDLE;
            setPlayState(mCurrentState);
            mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        orientationEventListener.disable();
        if (isCache) getCacheServer().unregisterCacheListener(cacheListener);

        isLockFullScreen = false;
        mCurrentPosition = 0;
    }

    public void setVideoListener(VideoListener listener) {
        this.listener = listener;
    }

    protected boolean isInPlaybackState() {
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
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mMediaPlayer != null ? bufferPercentage : 0;
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
        intent.putExtra(KeyUtil.ACTION, Constants.COMMAND_START);
        getContext().getApplicationContext().startService(intent);
    }

    /**
     * 关闭画中画
     */
    @Override
    public void stopFloatWindow() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.ACTION, Constants.COMMAND_STOP);
        getContext().getApplicationContext().startService(intent);
    }

    /**
     * 直接开始全屏播放
     */
    @Override
    public void startFullScreenDirectly() {
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startFullScreen();
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
            this.isLockFullScreen = isLocked;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public String getTitle() {
        return mCurrentTitle;
    }


    private CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            bufferPercentage = percentsAvailable;
        }
    };

    @Override
    public void onError() {
        mCurrentState = STATE_ERROR;
        if (listener != null) listener.onError();
        setPlayState(mCurrentState);
        mCurrentPosition = getCurrentPosition();
    }

    @Override
    public void onCompletion() {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        if (listener != null) listener.onComplete();
        setPlayState(mCurrentState);
        setKeepScreenOn(false);
    }

    @Override
    public void onInfo(int what, int extra) {
        if (listener != null) listener.onInfo(what, extra);
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                mCurrentState = STATE_BUFFERING;
                setPlayState(mCurrentState);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                mCurrentState = STATE_BUFFERED;
                setPlayState(mCurrentState);
                break;
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                if (getWindowVisibility() != VISIBLE) pause();
                break;
        }
    }

    @Override
    public void onBufferingUpdate(int position) {
        if (!isCache) bufferPercentage = position;
    }

    @Override
    public void onPrepared() {
        mCurrentState = STATE_PREPARED;
        if (listener != null) listener.onPrepared();
        setPlayState(mCurrentState);
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
        start();
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
}
