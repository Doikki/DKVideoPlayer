package com.dueeeke.videoplayer.thunder;

import android.content.res.AssetFileDescriptor;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.aplayer.APlayerAndroid;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.util.L;

import java.util.Map;
import java.util.Set;

/**
 * 迅雷APlayer，实验性
 */
public class ThunderMediaPlayer extends AbstractPlayer {

    protected APlayerAndroid mAPlayer;

    private String mPath;

    private boolean mIsLooping;


    @Override
    public void initPlayer() {
        mAPlayer = new APlayerAndroid();

        setOptions();

        mAPlayer.setOnFirstFrameRenderListener(new APlayerAndroid.OnFirstFrameRenderListener() {
            @Override
            public void onFirstFrameRender() {
                mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START, 0);
            }
        });

        mAPlayer.setOnOpenCompleteListener(new APlayerAndroid.OnOpenCompleteListener() {
            @Override
            public void onOpenComplete(boolean isOpenSuccess) {
                if (isOpenSuccess) {
                    mPlayerEventListener.onVideoSizeChanged(mAPlayer.getVideoWidth(), mAPlayer.getVideoHeight());
                    mPlayerEventListener.onPrepared();
                } else {
                    mPlayerEventListener.onError();
                }
            }
        });

        mAPlayer.setOnPlayCompleteListener(new APlayerAndroid.OnPlayCompleteListener() {
            @Override
            public void onPlayComplete(String playRet) {
                L.d("onPlayComplete: " + playRet);
                if (TextUtils.equals(playRet, "0x0")) {
                    if (mIsLooping) {
                        prepareAsync();
                    } else {
                        mPlayerEventListener.onCompletion();
                    }
                }
            }
        });

        mAPlayer.setOnSeekCompleteListener(new APlayerAndroid.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_BUFFERING_END, 0);
            }
        });

        mAPlayer.setOnBufferListener(new APlayerAndroid.OnBufferListener() {
            @Override
            public void onBuffer(int progress) {
                L.d("onBuffer: " + progress);
                if (progress == 0) {
                    mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_BUFFERING_START, progress);
                }

                if (progress == 100) {
                    mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_BUFFERING_END, progress);
                }
            }
        });
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        mPath = path;
        if (headers != null) {
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (TextUtils.equals(key, "User-Agent")) {
                    mAPlayer.setConfig(APlayerAndroid.CONFIGID.HTTP_USER_AGENT, value);
                } else {
                    sb.append(key).append(":").append(value).append("\r\n");
                }
            }
            mAPlayer.setConfig(APlayerAndroid.CONFIGID.HTTP_CUSTOM_HEADERS, sb.toString());
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {

    }

    @Override
    public void start() {
        mAPlayer.play();
    }

    @Override
    public void pause() {
        mAPlayer.pause();
    }

    @Override
    public void stop() {
    }

    @Override
    public void prepareAsync() {
        mAPlayer.setConfig(APlayerAndroid.CONFIGID.AUTO_PLAY, "1");//准备完后自动播放
        if (!TextUtils.isEmpty(mPath)) {
            mAPlayer.open(mPath);
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean isPlaying() {
        return mAPlayer.getState() == APlayerAndroid.PlayerState.APLAYER_PLAYING;
    }

    @Override
    public void seekTo(long time) {
        mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_BUFFERING_START, 0);
        mAPlayer.setPosition((int) time);
    }

    @Override
    public void release() {
        mAPlayer.close();
        mAPlayer.destroy();
    }

    @Override
    public long getCurrentPosition() {
        return mAPlayer.getPosition();
    }

    @Override
    public long getDuration() {
        return mAPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        String strPos = mAPlayer.getConfig(APlayerAndroid.CONFIGID.READPOSITION);
        long duration = getDuration();
        if (duration == 0) return 0;
        return (int) (Integer.parseInt(strPos) * 100 / duration);
    }

    @Override
    public void setSurface(Surface surface) {
        mAPlayer.setView(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mAPlayer.setView(holder.getSurface());
    }

    @Override
    public void setVolume(float v1, float v2) {
        mAPlayer.setVolume((int) v1);
    }

    @Override
    public void setLooping(boolean isLooping) {
        mIsLooping = isLooping;
    }

    @Override
    public void setOptions() {
        mAPlayer.setConfig(APlayerAndroid.CONFIGID.HTTP_USER_AHTTP2, "1");
    }

    @Override
    public void setSpeed(float speed) {
        mAPlayer.setConfig(APlayerAndroid.CONFIGID.PLAY_SPEED, String.valueOf(speed  * 100));
    }

    @Override
    public long getTcpSpeed() {
        return Long.parseLong(mAPlayer.getConfig(APlayerAndroid.CONFIGID.DOWN_SPEED));
    }
}
