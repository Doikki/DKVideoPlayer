package com.dueeeke.videoplayer.player;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.listener.MediaEngineInterface;

/**
 * Created by Devlin_n on 2017/12/21.
 */

public abstract class BaseMediaEngine {

    protected MediaEngineInterface mMediaEngineInterface;

    /**
     * 初始化播放器实例
     */
    public abstract void initPlayer();

    /**
     * 设置播放地址
     * @param path 播放地址
     */
    public abstract void setDataSource(String path);

    /**
     * 播放
     */
    public abstract void start();

    /**
     * 暂停
     */
    public abstract void pause();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 准备开始播放（异步）
     */
    public abstract void prepareAsync();

    /**
     * 重置播放器
     */
    public abstract void reset();

    /**
     * 是否正在播放
     */
    public abstract boolean isPlaying();

    /**
     * 调整进度
     */
    public abstract void seekTo(long time);

    /**
     * 释放播放器
     */
    public abstract void release();

    /**
     * 获取当前播放的位置
     */
    public abstract long getCurrentPosition();

    /**
     * 获取视频总时长
     */
    public abstract long getDuration();

    /**
     * 设置渲染视频的View,主要用于TextureView
     */
    public abstract void setSurface(Surface surface);

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    public abstract void setDisplay(SurfaceHolder holder);

    /**
     * 设置音量
     */
    public abstract void setVolume(int v1, int v2);

    /**
     * 设置是否循环播放
     */
    public abstract void setLooping(boolean isLooping);

    /**
     * 设置硬解码
     */
    public abstract void setEnableMediaCodec(boolean isEnable);

    /**
     * 设置其他播放配置
     */
    public abstract void setOptions();

    /**
     * 设置播放速度
     */
    public abstract void setSpeed(float speed);

    /**
     * 获取当前缓冲的网速
     */
    public abstract long getTcpSpeed();

    /**
     * 绑定VideoView
     */
    public void bindVideoView(MediaEngineInterface mediaEngineInterface) {
        this.mMediaEngineInterface = mediaEngineInterface;
    }

}
