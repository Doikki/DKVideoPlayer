package xyz.doikki.videoplayer.sys;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

import xyz.doikki.videoplayer.MediaPlayer;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerException;
import xyz.doikki.videoplayer.util.L;

/**
 * 基于系统{@link android.media.MediaPlayer}封装
 * 注意：不推荐，兼容性差，建议使用IJK或者Exo播放器
 */
public class SysMediaPlayer extends MediaPlayer implements android.media.MediaPlayer.OnErrorListener,
        android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnInfoListener,
        android.media.MediaPlayer.OnBufferingUpdateListener, android.media.MediaPlayer.OnPreparedListener,
        android.media.MediaPlayer.OnVideoSizeChangedListener {

    protected android.media.MediaPlayer mMediaPlayer;
    private int mBufferedPercent;
    protected Context mAppContext;
    private boolean mIsPreparing;

    public SysMediaPlayer(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Override
    public void initPlayer() {
        mMediaPlayer = new android.media.MediaPlayer();
        setOptions();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            mMediaPlayer.setDataSource(mAppContext, Uri.parse(path), headers);
        } catch (Exception e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
        } catch (Exception e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void start() {
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void stop() {
        try {
            mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mIsPreparing = true;
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void reset() {
        stop();
        mMediaPlayer.reset();
        mMediaPlayer.setSurface(null);
        mMediaPlayer.setDisplay(null);
        mMediaPlayer.setVolume(1, 1);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long timeMills) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //使用这个api seekTo定位更加准确 支持android 8.0以上的设备 https://developer.android.com/reference/android/media/MediaPlayer#SEEK_CLOSEST
                mMediaPlayer.seekTo(timeMills, android.media.MediaPlayer.SEEK_CLOSEST);
            } else {
                mMediaPlayer.seekTo((int) timeMills);
            }
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void release() {
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        stop();
        final android.media.MediaPlayer mediaPlayer = mMediaPlayer;
        mMediaPlayer = null;
        //todo 此处挂独立线程是否合理？
        new Thread() {
            @Override
            public void run() {
                try {
                    mediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            mMediaPlayer.setSurface(surface);
        } catch (Exception e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        try {
            mMediaPlayer.setDisplay(holder);
        } catch (Exception e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        mMediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setOptions() {
    }

    @Override
    public void setSpeed(float speed) {
        // only support above Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            L.w("Android MediaPlayer do not support set speed");
            return;
        }
        try {
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        } catch (Exception e) {
            mPlayerEventListener.onError(e);
        }
    }

    @Override
    public float getSpeed() {
        // only support above Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                float speed = mMediaPlayer.getPlaybackParams().getSpeed();
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
        mPlayerEventListener.onError(new AndroidMediaPlayerException(what, extra));
        return true;
    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
        mPlayerEventListener.onCompletion();
    }

    @Override
    public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
        //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
        if (what == MediaPlayer.MEDIA_INFO_RENDERING_START) {
            if (mIsPreparing) {
                mPlayerEventListener.onInfo(what, extra);
                mIsPreparing = false;
            }
        } else {
            mPlayerEventListener.onInfo(what, extra);
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
        mBufferedPercent = percent;
    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {
        mPlayerEventListener.onPrepared();
        start();
        // 修复播放纯音频时状态出错问题
        if (!isVideo()) {
            mPlayerEventListener.onInfo(MediaPlayer.MEDIA_INFO_RENDERING_START, 0);
        }
    }

    private boolean isVideo() {
        try {
            android.media.MediaPlayer.TrackInfo[] trackInfo = mMediaPlayer.getTrackInfo();
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
            mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

}
