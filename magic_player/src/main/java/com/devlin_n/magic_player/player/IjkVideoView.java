package com.devlin_n.magic_player.player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.controller.AdController;
import com.devlin_n.magic_player.controller.BaseMediaController;
import com.devlin_n.magic_player.controller.IjkMediaController;
import com.devlin_n.magic_player.util.KeyUtil;
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
    private IjkMediaPlayer mMediaPlayer;
    private BaseMediaController mMediaController;
    private boolean isControllerAdded;
    private MySurfaceView surfaceView;
    private RelativeLayout surfaceContainer;
    private FrameLayout controllerContainer;
    private StatusView statusView;
    private int bufferPercentage;
    private ProgressBar bufferProgress;
    private int originalWidth, originalHeight;
    private boolean isFullScreen;
    private String mCurrentUrl;
    private boolean isAutoPlay;
    private ImageView playButton;
    private ImageView thumb;
    private boolean isMute;

    public static final int VOD = 1;
    public static final int LIVE = 2;
    public static final int AD = 3;
    private List<VideoModel> mVideoModels;
    private int mCurrentVideoPosition = 0;
    private int mCurrentPosition;
    private int mCurrentVideoType;
    private String mCurrentTitle = "";
    private static boolean IS_PLAY_ON_MOBILE_NETWORK = false;


    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_BUFFERING = 6;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private AudioManager mAudioManager;

    public IjkVideoView(@NonNull Context context) {
        this(context, null);
    }


    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
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
                    playButton.setImageResource(R.drawable.ic_play);
                } else {
                    start();
                    playButton.setImageResource(R.drawable.ic_pause);
                }
            }
        });
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
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    private void openVideo() {
        if (getNetworkType(getContext()) == 4 && !IS_PLAY_ON_MOBILE_NETWORK) {
            if (statusView == null) {
                statusView = new StatusView(getContext());
            }
            statusView.setMessage(getResources().getString(R.string.wifi_tip));
            statusView.setButtonTextAndAction(getResources().getString(R.string.continue_play), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IS_PLAY_ON_MOBILE_NETWORK = true;
                    removeView(statusView);
                    openVideo();
                    startPrepare();
                }
            });
            addView(statusView);
            return;
        }
        if (mCurrentUrl == null || mCurrentUrl.trim().equals("")) return;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mMediaPlayer = new IjkMediaPlayer();
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        if (isAutoPlay) startPrepare();
    }

    private void startPrepare() {
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
    }

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

    public void setUrl(String url) {
        this.mCurrentUrl = url;
        openVideo();
    }

    public void skipPositionWhenPlay(String url, int position) {
        this.mCurrentUrl = url;
        this.mCurrentPosition = position;
        openVideo();
    }

    public void setVideoType(int type) {
        mCurrentVideoType = type;
    }

    public void setVideos(List<VideoModel> videoModels) {
        this.mVideoModels = videoModels;
        playNext();
        openVideo();
    }

    public void setTitle(String title) {
        if (title != null) {
            this.mCurrentTitle = title;
        }
    }

    public ImageView getThumb() {
        return thumb;
    }

    private void playNext() {
        VideoModel videoModel = mVideoModels.get(mCurrentVideoPosition);
        if (videoModel != null) {
            mCurrentUrl = videoModel.url;
            mCurrentTitle = videoModel.title;
            mCurrentPosition = 0;
            setMediaController(videoModel.controller);
        }
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            startPrepare();
        } else {
            if (isInPlaybackState()) {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            }
            mTargetState = STATE_PLAYING;
        }

    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying() && mCurrentState != STATE_PLAYBACK_COMPLETED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
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

        if (mMediaController != null) mMediaController.destroy();
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

    @Override
    public void startFloatWindow() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.URL, mCurrentUrl);
        getCurrentPosition();
        intent.putExtra(KeyUtil.POSITION, mCurrentPosition);
        intent.putExtra(KeyUtil.TYPE, mCurrentVideoType);
        getContext().startService(intent);
        WindowUtil.getAppCompActivity(getContext()).finish();
    }

    public void stopFloatWindow() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        getContext().stopService(intent);
    }

    @Override
    public void startFullScreenDirectly() {
        WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startFullScreen();
    }

    @Override
    public void skipToNext() {
        mCurrentVideoPosition++;
        if (mVideoModels != null && mVideoModels.size() > 1) {
            if (mCurrentVideoPosition >= mVideoModels.size()) return;
            playNext();
            startPrepare();
            addSurfaceView();
        }
    }

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
            if (mCurrentState == STATE_PREPARED) return;
            if (visibility != VISIBLE) {
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
            } else {
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
            }
        }
    }

    public void setMediaController(int type) {
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
                mCurrentVideoType = AD;
                break;
            default:
                break;
        }
    }

    public void setMediaController(BaseMediaController mediaController) {
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
                openVideo();
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
            addSurfaceView();
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            if (mMediaController != null) mMediaController.hide();
            bufferProgress.setVisibility(VISIBLE);
            mCurrentState = STATE_BUFFERING;
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            bufferProgress.setVisibility(GONE);
            mCurrentState = STATE_PLAYING;
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
            addSurfaceView();
            surfaceView.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    public boolean backFromFullScreen() {
        if (mMediaController != null && mMediaController.lockBack()) return true;
        if (isFullScreen) {
            stopFullScreen();
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (mMediaController != null) mMediaController.updateFullScreen();
            return true;
        }
        return false;
    }


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

    public void setAutoPlay(boolean autoPlay) {
        this.isAutoPlay = autoPlay;
        if (autoPlay) {
            playButton.setVisibility(GONE);
        } else {
            playButton.setVisibility(VISIBLE);
        }
    }

    /**
     * 判断当前网络类型-1为未知网络0为没有网络连接1网络断开或关闭2为以太网3为WiFi4为2G5为3G6为4G
     */
    public int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            // 没有任何网络
            return 0;
        }
        if (!networkInfo.isConnected()) {
            // 网络断开或关闭
            return 1;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            // 以太网网络
            return 2;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
            return 3;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
            switch (networkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    // 2G网络
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    // 3G网络
                case TelephonyManager.NETWORK_TYPE_LTE:
                    // 4G网络
                    return 4;
            }
        }
        // 未知网络
        return -1;
    }
}
