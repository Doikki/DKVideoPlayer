package xyz.doikki.videoplayer.sys;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.AbstractAVPlayer;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerException;
import xyz.doikki.videoplayer.util.L;

/**
 * 基于系统{@link android.media.MediaPlayer}封装
 * 注意：不推荐，兼容性差，建议使用IJK或者Exo播放器
 */
public class SysMediaPlayer extends AbstractAVPlayer implements android.media.MediaPlayer.OnErrorListener,
        android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnInfoListener,
        android.media.MediaPlayer.OnBufferingUpdateListener, android.media.MediaPlayer.OnPreparedListener,
        android.media.MediaPlayer.OnVideoSizeChangedListener {

    //系统播放器核心
    private android.media.MediaPlayer kernel;
    private int bufferedPercent;
    protected Context appContext;

    /**
     * 是否正在准备阶段：用于解决{@link android.media.MediaPlayer#MEDIA_INFO_VIDEO_RENDERING_START}多次回调问题
     */
    private boolean isPreparing;

    public SysMediaPlayer(Context context) {
        appContext = context.getApplicationContext();
    }

    @Override
    public void init() {
        kernel = new android.media.MediaPlayer();
        kernel.setAudioStreamType(AudioManager.STREAM_MUSIC);
        kernel.setOnErrorListener(this);
        kernel.setOnCompletionListener(this);
        kernel.setOnInfoListener(this);
        kernel.setOnBufferingUpdateListener(this);
        kernel.setOnPreparedListener(this);
        kernel.setOnVideoSizeChangedListener(this);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            kernel.setDataSource(appContext, Uri.parse(path), headers);
        } catch (Throwable e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            kernel.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
        } catch (Throwable e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void start() {
        try {
            kernel.start();
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void pause() {
        try {
            kernel.pause();
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void stop() {
        try {
            kernel.stop();
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void prepareAsync() {
        try {
            isPreparing = true;
            kernel.prepareAsync();
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void reset() {
        stop();
        kernel.reset();
        kernel.setSurface(null);
        kernel.setDisplay(null);
        kernel.setVolume(1, 1);
    }

    @Override
    public boolean isPlaying() {
        return kernel.isPlaying();
    }

    @Override
    public void seekTo(long msec) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                //使用这个api seekTo定位更加准确 支持android 8.0以上的设备 https://developer.android.com/reference/android/media/MediaPlayer#SEEK_CLOSEST
                kernel.seekTo(msec, android.media.MediaPlayer.SEEK_CLOSEST);
            } else {
                kernel.seekTo((int) msec);
            }
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void release() {
        kernel.setOnErrorListener(null);
        kernel.setOnCompletionListener(null);
        kernel.setOnInfoListener(null);
        kernel.setOnBufferingUpdateListener(null);
        kernel.setOnPreparedListener(null);
        kernel.setOnVideoSizeChangedListener(null);
        stop();
        final android.media.MediaPlayer mediaPlayer = kernel;
        kernel = null;
        new Thread() {
            @Override
            public void run() {
                try {
                    mediaPlayer.release();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public long getCurrentPosition() {
        return kernel.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return kernel.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return bufferedPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            kernel.setSurface(surface);
        } catch (Exception e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        try {
            kernel.setDisplay(holder);
        } catch (Exception e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        kernel.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        kernel.setLooping(isLooping);
    }

    @Override
    public void setSpeed(float speed) {
        // only support above Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            L.w("Android MediaPlayer do not support set speed");
            return;
        }
        try {
            kernel.setPlaybackParams(kernel.getPlaybackParams().setSpeed(speed));
        } catch (Exception e) {
            eventListener.onError(e);
        }
    }

    @Override
    public float getSpeed() {
        // only support above Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                float speed = kernel.getPlaybackParams().getSpeed();
                if (speed == 0f) speed = 1f;
                return speed;
            } catch (Exception e) {
                return 1f;
            }
        }
        L.w("Android MediaPlayer do not support tcp speed");
        return 1f;
    }

    @Override
    public long getTcpSpeed() {
        // no support
        L.w("Android MediaPlayer do not support tcp speed");
        return 0;
    }

    @Override
    public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
        eventListener.onError(new AndroidMediaPlayerException(what, extra));
        return true;
    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
        eventListener.onCompletion();
    }

    @Override
    public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
        //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
        if (what == AVPlayer.MEDIA_INFO_RENDERING_START) {
            if (isPreparing) {
                eventListener.onInfo(what, extra);
                isPreparing = false;
            }
        } else {
            eventListener.onInfo(what, extra);
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
        bufferedPercent = percent;
    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {
        eventListener.onPrepared();
        start();
        // 修复播放纯音频时状态出错问题
        if (!isVideo()) {
            eventListener.onInfo(AVPlayer.MEDIA_INFO_RENDERING_START, 0);
        }
    }

    private boolean isVideo() {
        try {
            android.media.MediaPlayer.TrackInfo[] trackInfo = kernel.getTrackInfo();
            for (android.media.MediaPlayer.TrackInfo info :
                    trackInfo) {
                if (info.getTrackType() == android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(android.media.MediaPlayer mp, int width, int height) {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            eventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

}
