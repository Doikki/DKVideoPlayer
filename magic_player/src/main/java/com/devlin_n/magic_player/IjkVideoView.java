package com.devlin_n.magic_player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

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
    private IjkMediaPlayer mediaPlayer;
    private BaseIjkMediaController ijkMediaController;
    private View videoView;
    private boolean isAutoPlay;
    private boolean isPlayed;
    private MySurfaceView surfaceView;
    private int bufferPercentage;
    private ProgressBar bufferProgress;
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private static boolean isOpenFloatWindow = false;
    private int originalWidth, originalHeight;
    private boolean isFullScreen;
    private View floatView;

    public IjkVideoView(@NonNull Context context) {
        super(context);
        init();
    }


    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        videoView = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_view, this);
        surfaceView = (MySurfaceView) videoView.findViewById(R.id.sv_1);
        bufferProgress = (ProgressBar) videoView.findViewById(R.id.buffering);
        mediaPlayer = new IjkMediaPlayer();
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        SurfaceHolder surfaceHolder_1 = surfaceView.getHolder();
        surfaceHolder_1.addCallback(this);
        surfaceHolder_1.setFormat(PixelFormat.RGBA_8888);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isAutoPlay && !isPlayed) {
            mediaPlayer.prepareAsync();
            bufferProgress.setVisibility(VISIBLE);
            ijkMediaController.hide();
            isPlayed = true;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setUrl(String url) {
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "播放地址有误~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void start() {
        if (!isPlayed && !isAutoPlay) {
            mediaPlayer.prepareAsync();
            bufferProgress.setVisibility(VISIBLE);
            ijkMediaController.hide();
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            ijkMediaController.show();
        }

        isPlayed = true;
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return (int) mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return bufferPercentage;
    }

    @Override
    public void startFloatScreen() {
        if (isOpenFloatWindow) return;
        surfaceView.setVisibility(INVISIBLE);
        ijkMediaController.hide();
        initWindow();
        floatView = LayoutInflater.from(getContext()).inflate(R.layout.layout_float_window, null);
        final SurfaceView surfaceView = (SurfaceView) floatView.findViewById(R.id.float_sv);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        floatView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(floatView);
                mediaPlayer.release();
                isOpenFloatWindow = false;
            }
        });
        floatView.setOnTouchListener(new OnTouchListener() {
            private int floatX;
            private int floatY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        floatX = (int) event.getX();
                        floatY = (int) (event.getY() + WindowUtil.getStatusBarHeight(getContext()));
                        break;
                    case MotionEvent.ACTION_UP:
//                        return !(Math.abs(mDownY - Y) < 5 && Math.abs(mDownX - X) < 5);
                    case MotionEvent.ACTION_MOVE:
                        Log.d("!!!!!!!!!!!!", "onTouch: " + "floatX:" + floatX + "   floatY:" + floatY + "    X:" + X + "   Y:" + Y);
                        wmParams.gravity = Gravity.START | Gravity.TOP;
                        wmParams.x = X - floatX;
                        wmParams.y = Y - floatY;
                        wm.updateViewLayout(floatView, wmParams);
                        break;
                }
                return false;
            }
        });
        wm.addView(floatView, wmParams);
        isOpenFloatWindow = true;
    }

    @Override
    public void startFullScreen() {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

        if (isFullScreen) {
            WindowUtil.showSupportActionBar(getContext(), true, true);
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            layoutParams.width = originalWidth;
            layoutParams.height = originalHeight;
            this.setLayoutParams(layoutParams);
            isFullScreen = false;
        } else {
            WindowUtil.hideSupportActionBar(getContext(), true, true);
            layoutParams.width = WindowUtil.getScreenHeight(getContext());
            layoutParams.height = WindowUtil.getScreenWidth(getContext());
            this.setLayoutParams(layoutParams);
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullScreen = true;
        }

    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    private void initWindow() {
        wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.TRANSPARENT;
        // 设置Window flag
//        wmParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
//        下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.gravity = Gravity.END | Gravity.BOTTOM; // 调整悬浮窗口至左上角
        // 设置悬浮窗口长宽数据
        wmParams.width = 800;
        wmParams.height = 600;
    }

    public void release() {
        if(isOpenFloatWindow) return;
        mediaPlayer.release();
    }

    public void setIjkMediaController(BaseIjkMediaController ijkMediaController) {
        this.ijkMediaController = ijkMediaController;
        this.ijkMediaController.setMediaPlayer(this);
        this.addView(this.ijkMediaController);
        this.ijkMediaController.show();
    }

    public void setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        this.ijkMediaController.reset();
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            ijkMediaController.hide();
            bufferProgress.setVisibility(VISIBLE);
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            bufferProgress.setVisibility(GONE);
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        bufferPercentage = i;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        bufferProgress.setVisibility(GONE);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            surfaceView.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    public boolean backFromFullScreen(){
        if(isFullScreen) {
            startFullScreen();
            ijkMediaController.updateFullScreen();
            return true;
        }
        return false;
    }
}
