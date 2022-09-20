package xyz.doikki.videoplayer.ijk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;
import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.AbstractAVPlayer;
import xyz.doikki.videoplayer.VideoViewManager;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerException;

public class IjkAVPlayer extends AbstractAVPlayer implements IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnVideoSizeChangedListener, IjkMediaPlayer.OnNativeInvokeListener {

    protected IjkMediaPlayer kernel;
    private int bufferedPercent;
    private final Context appContext;

    public IjkAVPlayer(Context context) {
        appContext = context;
    }

    @Override
    public void init() {
        kernel = new IjkMediaPlayer();
        //native日志
        IjkMediaPlayer.native_setLogLevel(VideoViewManager.getConfig().mIsEnableLog ? IjkMediaPlayer.IJK_LOG_INFO : IjkMediaPlayer.IJK_LOG_SILENT);
        kernel.setOnErrorListener(this);
        kernel.setOnCompletionListener(this);
        kernel.setOnInfoListener(this);
        kernel.setOnBufferingUpdateListener(this);
        kernel.setOnPreparedListener(this);
        kernel.setOnVideoSizeChangedListener(this);
        kernel.setOnNativeInvokeListener(this);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            Uri uri = Uri.parse(path);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(appContext, uri);
                kernel.setDataSource(rawDataSourceProvider);
            } else {
                if (headers != null && headers.containsKey("User-Agent")) {
                    //处理UA问题
                    //update by luochao: 直接在Map参数中移除字段，可能影响调用者的逻辑
                    Map<String, String> clonedHeaders = new HashMap<>(headers);
                    // 移除header中的User-Agent，防止重复
                    String userAgent = clonedHeaders.remove("User-Agent");
//                    if (TextUtils.isEmpty(userAgent)) {
//                        userAgent = "";
//                    }
                    kernel.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
                    kernel.setDataSource(appContext, uri, clonedHeaders);
                } else {
                    //不包含UA，直接设置
                    kernel.setDataSource(appContext, uri, headers);
                }
            }
        } catch (Throwable e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            kernel.setDataSource(new RawDataSourceProvider(fd));
        } catch (Exception e) {
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
    public void start() {
        try {
            kernel.start();
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
            kernel.prepareAsync();
        } catch (IllegalStateException e) {
            eventListener.onError(e);
        }
    }

    @Override
    public void reset() {
        kernel.reset();
        kernel.setOnVideoSizeChangedListener(this);
    }

    @Override
    public boolean isPlaying() {
        return kernel.isPlaying();
    }

    @Override
    public void seekTo(long msec) {
        try {
            kernel.seekTo((int) msec);
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
        new Thread() {
            @Override
            public void run() {
                try {
                    kernel.release();
                } catch (Exception e) {
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
        kernel.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        kernel.setDisplay(holder);
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
        kernel.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return kernel.getSpeed(0);
    }

    @Override
    public long getTcpSpeed() {
        return kernel.getTcpSpeed();
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        eventListener.onError(new AndroidMediaPlayerException(what, extra));
        return true;
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        eventListener.onCompletion();
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        eventListener.onInfo(what, extra);
        return true;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        bufferedPercent = percent;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        eventListener.onPrepared();
        // 修复播放纯音频时状态出错问题
        if (!isVideo()) {
            eventListener.onInfo(AVPlayer.MEDIA_INFO_RENDERING_START, 0);
        }
    }

    private boolean isVideo() {
        IjkTrackInfo[] trackInfo = kernel.getTrackInfo();
        if (trackInfo == null) return false;
        for (IjkTrackInfo info : trackInfo) {
            if (info.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            eventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

    @Override
    public boolean onNativeInvoke(int what, Bundle args) {
        return true;
    }
}
