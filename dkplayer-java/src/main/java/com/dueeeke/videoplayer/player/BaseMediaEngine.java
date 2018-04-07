package com.dueeeke.videoplayer.player;

import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Devlin_n on 2017/12/21.
 */

public abstract class BaseMediaEngine {

    public abstract void initPlayer();

    public abstract void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    public abstract void start();

    public abstract void pause();

    public abstract void stop();

    public abstract void prepareAsync();

    public abstract void reset();

    public abstract boolean isPlaying();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract void setSurface(Surface surface);

    public abstract void setDisplay(SurfaceHolder holder);

    public abstract void setVolume(int v1, int v2);

    public abstract void setLooping(boolean isLooping);

    public abstract void setEnableMediaCodec(boolean isEnable);

}
