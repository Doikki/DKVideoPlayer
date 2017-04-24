package com.devlin_n.magic_player.player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.controller.AdController;
import com.devlin_n.magic_player.controller.BaseMediaController;
import com.devlin_n.magic_player.controller.IjkMediaController;
import com.devlin_n.magic_player.util.KeyUtil;
import com.devlin_n.magic_player.util.NetworkUtil;
import com.devlin_n.magic_player.util.WindowUtil;
import com.devlin_n.magic_player.widget.MySurfaceView;
import com.devlin_n.magic_player.widget.StatusView;

import java.io.IOException;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkVideoView extends FrameLayout implements SurfaceHolder.Callback, IjkMediaController.MediaPlayerControlInterface,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = IjkVideoView.class.getSimpleName();
    private IjkMediaPlayer mMediaPlayer;//ijkPlayer
    private BaseMediaController mMediaController;//控制器
    private boolean isControllerAdded;//师傅添加控制器
    private MySurfaceView surfaceView;
    private RelativeLayout surfaceContainer;
    private FrameLayout controllerContainer;
    private StatusView statusView;//显示错误信息的一个view
    private int bufferPercentage;//缓冲百分比
    private ProgressBar bufferProgress;//缓冲时显示的圆形进度条
    private int originalWidth, originalHeight;//原始宽高，主要用于恢复竖屏
    private boolean isFullScreen;//是否处于全屏状态
    private ImageView playButton;//播放按钮
    private ImageView thumb;//缩略图
    private boolean isMute;//是否静音

    public static final int ALERT_WINDOW_PERMISSION_CODE = 1;

    //三种视频类型
    public static final int VOD = 1;//点播
    public static final int LIVE = 2;//直播
    public static final int AD = 3;//广告

    private String mCurrentUrl;//当前播放视频的地址
    private List<VideoModel> mVideoModels;//列表播放数据
    private int mCurrentVideoPosition = 0;//列表播放时当前播放视频的在List中的位置
    private int mCurrentPosition;//当前正在播放视频的位置
    private int mCurrentVideoType;//当前正在播放视频的类型
    private String mCurrentTitle = "";//当前正在播放视频的标题
    private static boolean IS_PLAY_ON_MOBILE_NETWORK = false;//记录是否在移动网络下播放视频

    //播放器的各种状态
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_BUFFERING = 6;

    private int mCurrentState = STATE_IDLE;//当前播放器的状态
    private int mTargetState = STATE_IDLE;//代码执行完播放器应该达到的状态

    private AudioManager mAudioManager;//系统音频管理器

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener;
    protected boolean mAutoRotate;//是否旋转屏幕
    private boolean isLocked;
    private boolean mAlwaysFullScreen;//总是全屏


    public IjkVideoView(@NonNull Context context) {
        this(context, null);
    }


    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化播放器视图
     */
    private void initView() {
        View videoView = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_view, this);
        bufferProgress = (ProgressBar) videoView.findViewById(R.id.buffering);
        surfaceContainer = (RelativeLayout) videoView.findViewById(R.id.surface_container);
        controllerContainer = (FrameLayout) videoView.findViewById(R.id.controller_container);
        playButton = (ImageView) videoView.findViewById(R.id.iv_play);
        thumb = (ImageView) videoView.findViewById(R.id.iv_thumb);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pause();
                } else {
                    start();
                }
            }
        });
        //获取播放器竖屏时的原始宽高
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                originalWidth = getWidth();
                originalHeight = getHeight();
                if (originalWidth != -1 && originalHeight != -1) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    /**
     * 创建播放器实例，设置播放地址及播放器参数
     */
    public IjkVideoView init() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mMediaPlayer = new IjkMediaPlayer();
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        addSurfaceView();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        return this;
    }

    /**
     * 开始准备播放（直接播放）
     */
    private void startPrepare() {
        if (mCurrentUrl == null || mCurrentUrl.trim().equals("")) return;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mCurrentUrl);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            bufferProgress.setVisibility(VISIBLE);
            playButton.setVisibility(GONE);
            thumb.setVisibility(GONE);
        } catch (IOException e) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            e.printStackTrace();
        }
        if (mAlwaysFullScreen) {
            startFullScreenDirectly();
        }
    }

    /**
     * 添加surfaceView
     */
    private void addSurfaceView() {
        surfaceContainer.removeAllViews();
        surfaceView = new MySurfaceView(getContext());
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceContainer.addView(surfaceView);
    }

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

    /**
     * 设置视频地址
     */
    public IjkVideoView setUrl(String url) {
        this.mCurrentUrl = url;
        return this;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public IjkVideoView skipPositionWhenPlay(String url, int position) {
        this.mCurrentUrl = url;
        this.mCurrentPosition = position;
        return this;
    }

    /**
     * 设置视频的类型
     */
    public IjkVideoView setVideoType(int type) {
        mCurrentVideoType = type;
        return this;
    }

    /**
     * 设置一个列表的视频
     */
    public IjkVideoView setVideos(List<VideoModel> videoModels) {
        this.mVideoModels = videoModels;
        playNext();
        return this;
    }

    /**
     * 设置标题
     */
    public IjkVideoView setTitle(String title) {
        if (title != null) {
            this.mCurrentTitle = title;
        }
        return this;
    }

    /**
     * 获取缩略图
     */
    public ImageView getThumb() {
        return thumb;
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
            setMediaController(videoModel.type);
        }
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            if (checkNetwork()) return;
            startPrepare();
        } else {
            if (isInPlaybackState()) {
                mMediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_pause);
                mCurrentState = STATE_PLAYING;
            }
            mTargetState = STATE_PLAYING;
        }
        AppCompatActivity activity = WindowUtil.getAppCompActivity(getContext());
        if (activity != null)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private boolean checkNetwork() {
        if (NetworkUtil.getNetworkType(getContext()) == NetworkUtil.NETWORK_MOBILE && !IS_PLAY_ON_MOBILE_NETWORK) {
            if (statusView == null) {
                statusView = new StatusView(getContext());
            }
            statusView.setMessage(getResources().getString(R.string.wifi_tip));
            statusView.setButtonTextAndAction(getResources().getString(R.string.continue_play), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IS_PLAY_ON_MOBILE_NETWORK = true;
                    removeView(statusView);
                    startPrepare();
                }
            });
            addView(statusView);
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                playButton.setImageResource(R.drawable.ic_play);
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
        AppCompatActivity activity = WindowUtil.getAppCompActivity(getContext());
        if (activity != null)
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying() && mCurrentState != STATE_PLAYBACK_COMPLETED) {
            mMediaPlayer.start();
            playButton.setImageResource(R.drawable.ic_pause);
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
        AppCompatActivity activity = WindowUtil.getAppCompActivity(getContext());
        if (activity != null)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mMediaController != null) mMediaController.updateProgress();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
        if (mAutoRotate) {
            orientationEventListener.disable();
            orientationEventListener = null;
        }
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
            mCurrentPosition = (int) mMediaPlayer.getCurrentPosition() + 500;
            return mCurrentPosition;
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState() && mCurrentVideoType != LIVE) {
            mMediaPlayer.seekTo(pos);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android M 以上系统需要请求权限
            if (!Settings.canDrawOverlays(WindowUtil.getAppCompActivity(getContext()))) {
                Toast.makeText(WindowUtil.getAppCompActivity(getContext()), R.string.float_window_warning, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + WindowUtil.getAppCompActivity(getContext()).getPackageName()));
                WindowUtil.getAppCompActivity(getContext()).startActivityForResult(intent, ALERT_WINDOW_PERMISSION_CODE);
            } else {
                startBackgroundService();
            }
        } else {
            startBackgroundService();
        }
    }

    /**
     * 启动画中画播放的后台服务
     */
    private void startBackgroundService() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.URL, mCurrentUrl);
        getCurrentPosition();
        intent.putExtra(KeyUtil.POSITION, mCurrentPosition);
        intent.putExtra(KeyUtil.TYPE, mCurrentVideoType);
        getContext().getApplicationContext().startService(intent);
        WindowUtil.getAppCompActivity(getContext()).finish();
    }

    /**
     * 关闭画中画
     */
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
        if (mMediaController != null) mMediaController.startFullScreenDirectly();
    }

    public IjkVideoView alwaysFullScreen() {
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
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        WindowUtil.hideSupportActionBar(getContext(), true, true);
        WindowUtil.hideNavKey(getContext());
        int height = WindowUtil.getScreenWidth(getContext());
        layoutParams.width = WindowUtil.getScreenHeight(getContext(), true);
        layoutParams.height = height;
        if (mMediaController != null)
            mMediaController.setLayoutParams(new LayoutParams(WindowUtil.getScreenHeight(getContext(), false), height));
        this.setLayoutParams(layoutParams);
        isFullScreen = true;
    }

    @Override
    public void stopFullScreen() {
        if (!isFullScreen) return;
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        WindowUtil.showSupportActionBar(getContext(), true, true);
        WindowUtil.showNavKey(getContext());
        layoutParams.width = originalWidth;
        layoutParams.height = originalHeight;
        if (mMediaController != null)
            mMediaController.setLayoutParams(new LayoutParams(originalWidth, originalHeight));
        this.setLayoutParams(layoutParams);
        isFullScreen = false;
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public String getTitle() {
        return mCurrentTitle;
    }

    @Override
    public void updatePlayButton(int visibility) {
        if (mCurrentState == STATE_BUFFERING) {
            playButton.setVisibility(GONE);
        } else {
            playButton.setVisibility(visibility);
            if (mCurrentState == STATE_PREPARED || mCurrentState == STATE_BUFFERING) return;
            if (visibility != VISIBLE) {
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
            } else {
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
            }
        }
    }

    /**
     * 根据视频类型快速设置控制器
     */
    public IjkVideoView setMediaController(int type) {
        controllerContainer.removeAllViews();
        isControllerAdded = false;
        switch (type) {
            case VOD: {
                IjkMediaController ijkMediaController = new IjkMediaController(getContext());
                ijkMediaController.setMediaPlayer(this);
                mMediaController = ijkMediaController;
                mCurrentVideoType = VOD;
                break;
            }
            case LIVE: {
                IjkMediaController ijkMediaController = new IjkMediaController(getContext());
                ijkMediaController.setMediaPlayer(this);
                ijkMediaController.setLive(true);
                mMediaController = ijkMediaController;
                mCurrentVideoType = LIVE;
                break;
            }
            case AD:
                AdController adController = new AdController(getContext());
                adController.setMediaPlayer(this);
                mMediaController = adController;
                mCurrentVideoType = AD;
                break;
            default:
                break;
        }
        return this;
    }

    /**
     * 设置控制器
     */
    public IjkVideoView setMediaController(BaseMediaController mediaController) {
        controllerContainer.removeAllViews();
        isControllerAdded = false;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            mMediaController = mediaController;
            if (mediaController instanceof IjkMediaController) {
                if (((IjkMediaController) mediaController).getLive()) {
                    mCurrentVideoType = LIVE;
                } else {
                    mCurrentVideoType = VOD;
                }
            } else if (mediaController instanceof AdController) {
                mCurrentVideoType = AD;
            }
        }
        return this;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        bufferProgress.setVisibility(GONE);
        mCurrentPosition = getCurrentPosition();
        if (statusView == null) {
            statusView = new StatusView(getContext());
        }
        statusView.setMessage(getResources().getString(R.string.error_message));
        statusView.setButtonTextAndAction(getResources().getString(R.string.retry), new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(statusView);
                startPrepare();
            }
        });
        addView(statusView);
        return true;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mMediaController != null && mVideoModels == null) mMediaController.reset();
        playButton.setImageResource(R.drawable.ic_play);
        mCurrentVideoPosition++;
        if (mVideoModels != null && mVideoModels.size() > 1) {
            if (mCurrentVideoPosition >= mVideoModels.size()) return;
            playNext();
            startPrepare();
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            bufferProgress.setVisibility(VISIBLE);
            mCurrentState = STATE_BUFFERING;
            if (mMediaController != null) mMediaController.hide();
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            bufferProgress.setVisibility(GONE);
            mCurrentState = STATE_PLAYING;
            if (mMediaController != null && mMediaController.isShowing()) playButton.setVisibility(VISIBLE);
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        bufferPercentage = i;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PREPARED;
        bufferProgress.setVisibility(GONE);
        playButton.setImageResource(R.drawable.ic_pause);
        playButton.setVisibility(GONE);
        if (mCurrentPosition > 0 && mCurrentVideoType == VOD) {
            seekTo(mCurrentPosition);
        }
        if (mMediaController != null && !isControllerAdded) {
            if (isFullScreen) {
                mMediaController.setLayoutParams(new LayoutParams(WindowUtil.getScreenWidth(getContext()),
                        WindowUtil.getScreenHeight(getContext(), false)));
                mMediaController.updateFullScreen();
            } else {
                mMediaController.setLayoutParams(new LayoutParams(originalWidth, originalHeight));
            }
            controllerContainer.addView(mMediaController);
            mMediaController.updateProgress();
            isControllerAdded = true;
        }
        mCurrentState = STATE_PLAYING;
        if (mTargetState == STATE_PAUSED) mMediaPlayer.pause();
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            surfaceView.setVideoSize(videoWidth, videoHeight);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        if (mMediaController != null && isLocked) {
            mMediaController.show();
            return true;
        }
        if (mAlwaysFullScreen) {
            return false;
        }
        if (isFullScreen) {
            stopFullScreen();
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (mMediaController != null) mMediaController.updateFullScreen();
            return true;
        }
        return false;
    }

    /**
     * 音频焦点改变监听
     */
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            try {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        pause();
                        playButton.setImageResource(R.drawable.ic_play);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pause();
                        playButton.setImageResource(R.drawable.ic_play);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        break;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 设置自动旋转
     */
    public IjkVideoView autoRotate() {
        this.mAutoRotate = true;
        orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕

            private int CurrentOrientation = 0;
            private static final int PORTRAIT = 1;
            private static final int LANDSCAPE = 2;
            private static final int REVERSE_LANDSCAPE = 3;


            @Override
            public void onOrientationChanged(int orientation) {
                //根据系统设置进行自动旋转
//            boolean autoRotateOn = (android.provider.Settings.System.getInt(WindowUtil.getAppCompActivity(getContext()).getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
//            if (!autoRotateOn) return;

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
                if (mMediaController != null) mMediaController.updateFullScreen();
            }
        };
        orientationEventListener.enable();
        return this;
    }
}
