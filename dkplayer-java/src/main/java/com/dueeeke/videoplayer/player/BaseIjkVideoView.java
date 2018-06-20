package com.dueeeke.videoplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.listener.PlayerEventListener;
import com.dueeeke.videoplayer.listener.VideoListener;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.ProgressUtil;
import com.dueeeke.videoplayer.util.WindowUtil;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public abstract class BaseIjkVideoView extends FrameLayout implements MediaPlayerControl, PlayerEventListener {

    protected AbstractPlayer mMediaPlayer;//播放器
    @Nullable
    protected BaseVideoController mVideoController;//控制器
    protected VideoListener mVideoListener;
    protected int bufferPercentage;//缓冲百分比
    protected boolean isMute;//是否静音

    protected String mCurrentUrl;//当前播放视频的地址
    protected Map<String, String> mHeaders;//当前视频地址的请求头
    protected AssetFileDescriptor mAssetFileDescriptor;//assets文件
    protected long mCurrentPosition;//当前正在播放视频的位置
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
    protected int mCurrentPlayState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    protected int mCurrentPlayerState = PLAYER_NORMAL;

    protected AudioManager mAudioManager;//系统音频管理器
    @Nullable
    protected AudioFocusHelper mAudioFocusHelper;

    protected int currentOrientation = 0;
    protected static final int PORTRAIT = 1;
    protected static final int LANDSCAPE = 2;
    protected static final int REVERSE_LANDSCAPE = 3;

    protected boolean isLockFullScreen;//是否锁定屏幕
    protected PlayerConfig mPlayerConfig;//播放器配置
    private HttpProxyCacheServer mCacheServer;

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕
        @Override
        public void onOrientationChanged(int orientation) {
            if (mVideoController == null) return;
            Activity activity = WindowUtil.scanForActivity(mVideoController.getContext());
            if (activity == null) return;
            if (orientation >= 340) { //屏幕顶部朝上
                onOrientationPortrait(activity);
            } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
                onOrientationLandscape(activity);
            } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
                onOrientationReverseLandscape(activity);
            }
        }
    };

    /**
     * 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        if (isLockFullScreen || !mPlayerConfig.mAutoRotate || currentOrientation == PORTRAIT)
            return;
        if ((currentOrientation == LANDSCAPE || currentOrientation == REVERSE_LANDSCAPE) && !isFullScreen()) {
            currentOrientation = PORTRAIT;
            return;
        }
        currentOrientation = PORTRAIT;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        stopFullScreen();
    }

    /**
     * 横屏
     */
    protected void onOrientationLandscape(Activity activity) {
        if (currentOrientation == LANDSCAPE) return;
        if (currentOrientation == PORTRAIT && isFullScreen()) {
            currentOrientation = LANDSCAPE;
            return;
        }
        currentOrientation = LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        if (currentOrientation == REVERSE_LANDSCAPE) return;
        if (currentOrientation == PORTRAIT && isFullScreen()) {
            currentOrientation = REVERSE_LANDSCAPE;
            return;
        }
        currentOrientation = REVERSE_LANDSCAPE;
        if (!isFullScreen()) {
            startFullScreen();
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    public BaseIjkVideoView(@NonNull Context context) {
        this(context, null);
    }


    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPlayerConfig = new PlayerConfig.Builder().build();
    }

    /**
     * 初始化播放器
     */
    protected void initPlayer() {
        if (mMediaPlayer == null) {
            if (mPlayerConfig.mAbstractPlayer != null) {
                mMediaPlayer = mPlayerConfig.mAbstractPlayer;
            } else {
                mMediaPlayer = new IjkPlayer(getContext());
            }
            mMediaPlayer.bindVideoView(this);
            mMediaPlayer.initPlayer();
            mMediaPlayer.setEnableMediaCodec(mPlayerConfig.enableMediaCodec);
            mMediaPlayer.setLooping(mPlayerConfig.isLooping);
        }
    }

    protected abstract void setPlayState(int playState);

    protected abstract void setPlayerState(int playerState);

    /**
     * 开始准备播放（直接播放）
     */
    protected void startPrepare(boolean needReset) {
        if (TextUtils.isEmpty(mCurrentUrl) && mAssetFileDescriptor == null) return;
        if (needReset) mMediaPlayer.reset();
        if (mAssetFileDescriptor != null) {
            mMediaPlayer.setDataSource(mAssetFileDescriptor);
        } else if (mPlayerConfig.isCache) {
            mCacheServer = getCacheServer();
            String proxyPath = mCacheServer.getProxyUrl(mCurrentUrl);
            mCacheServer.registerCacheListener(cacheListener, mCurrentUrl);
            if (mCacheServer.isCached(mCurrentUrl)) {
                bufferPercentage = 100;
            }
            mMediaPlayer.setDataSource(proxyPath, mHeaders);
        } else {
            mMediaPlayer.setDataSource(mCurrentUrl, mHeaders);
        }
        mMediaPlayer.prepareAsync();
        setPlayState(STATE_PREPARING);
        setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : PLAYER_NORMAL);
    }

    private HttpProxyCacheServer getCacheServer() {
        return VideoCacheManager.getProxy(getContext().getApplicationContext());
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mCurrentPlayState == STATE_IDLE) {
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
        if (!mPlayerConfig.disableAudioFocus) {
            mAudioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioFocusHelper = new AudioFocusHelper();
        }
        if (mPlayerConfig.savingProgress) {
            mCurrentPosition = ProgressUtil.getSavedProgress(mCurrentUrl);
        }
        if (mPlayerConfig.mAutoRotate)
            orientationEventListener.enable();
        initPlayer();
        startPrepare(false);
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        mMediaPlayer.start();
        setPlayState(STATE_PLAYING);
        if (mVideoListener != null) mVideoListener.onVideoStarted();
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
            if (mVideoListener != null) mVideoListener.onVideoPaused();
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
            if (mVideoListener != null) mVideoListener.onVideoStarted();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlayback() {
        if (mPlayerConfig.savingProgress && isInPlaybackState())
            ProgressUtil.saveProgress(mCurrentUrl, mCurrentPosition);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            setPlayState(STATE_IDLE);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    /**
     * 释放播放器
     */
    public void release() {
        if (mPlayerConfig.savingProgress && isInPlaybackState())
            ProgressUtil.saveProgress(mCurrentUrl, mCurrentPosition);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            setPlayState(STATE_IDLE);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    private void onPlayStopped() {
        if (mVideoController != null) mVideoController.hideStatusView();
        orientationEventListener.disable();
        if (mCacheServer != null)
            mCacheServer.unregisterCacheListener(cacheListener);
        isLockFullScreen = false;
        mCurrentPosition = 0;
    }

    /**
     * 设置播放器监听，用于外部监听播放器的各种状态
     */
    public void setVideoListener(VideoListener listener) {
        this.mVideoListener = listener;
    }

    /**
     * 是否处于播放状态
     */
    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED);
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
    public int getBufferPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        this.isMute = isMute;
        float volume = isMute ? 1.0f : 0.0f;
        mMediaPlayer.setVolume(volume, volume);
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return isMute;
    }

    /**
     * 设置controller是否处于锁定状态
     */
    @Override
    public void setLock(boolean isLocked) {
        this.isLockFullScreen = isLocked;
    }

    /**
     * 获取是否处于全屏状态
     */
    @Override
    public boolean isFullScreen() {
        return false;
    }

    /**
     * 获取当前播放视频的标题
     */
    @Override
    public String getTitle() {
        return mCurrentTitle;
    }

    /**
     * 缓存监听
     */
    private CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            bufferPercentage = percentsAvailable;
        }
    };

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError() {
        setPlayState(STATE_ERROR);
        if (mVideoListener != null) mVideoListener.onError();
    }

    /**
     * 视频播放完成回调
     */
    @Override
    public void onCompletion() {
        setPlayState(STATE_PLAYBACK_COMPLETED);
        setKeepScreenOn(false);
        mCurrentPosition = 0;
        if (mVideoListener != null) mVideoListener.onComplete();
    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                setPlayState(STATE_BUFFERING);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                setPlayState(STATE_BUFFERED);
                break;
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                setPlayState(STATE_PLAYING);
                if (mVideoListener != null) mVideoListener.onVideoStarted();
                if (getWindowVisibility() != VISIBLE) pause();
                break;
        }
        if (mVideoListener != null) mVideoListener.onInfo(what, extra);
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        setPlayState(STATE_PREPARED);
        if (mCurrentPosition > 0) {
            L.d("seek to:" + mCurrentPosition);
            seekTo(mCurrentPosition);
        }
        if (mVideoListener != null) mVideoListener.onPrepared();
    }

    public void setPlayerConfig(PlayerConfig config) {
        this.mPlayerConfig = config;
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
        return mMediaPlayer.getTcpSpeed();
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
     * 重新播放
     */
    @Override
    public void refresh() {
        mCurrentPosition = 0;
        retry();
    }

    /**
     * 设置视频地址
     */
    public void setUrl(String url) {
        this.mCurrentUrl = url;
    }

    /**
     * 设置包含请求头信息的视频地址
     *
     * @param url     视频地址
     * @param headers 请求头
     */
    private void setUrl(String url, Map<String, String> headers) {
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
     * 设置标题
     */
    public void setTitle(String title) {
        if (title != null) {
            this.mCurrentTitle = title;
        }
    }

    /**
     * 设置音量 0.0f-1.0f 之间
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    public void setVolume(float v1, float v2) {
        if (isInPlaybackState()) {
            mMediaPlayer.setVolume(v1, v2);
        }
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
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mMediaPlayer != null && !isMute)//恢复音量
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
                    if (mMediaPlayer != null && isPlaying() && !isMute) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
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
