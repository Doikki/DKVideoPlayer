package xyz.doikki.videoplayer;

import android.content.res.AssetFileDescriptor;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public interface AVPlayerFunction {

    /**
     * 设置播放地址
     *
     * @param path 播放地址
     */
    default void setDataSource(@NonNull String path) {
        setDataSource(path, null);
    }

    /**
     * 设置播放地址
     *
     * @param path    播放地址
     * @param headers 播放地址请求头
     */
    void setDataSource(@NonNull String path, @Nullable Map<String, String> headers);

    /**
     * 用于播放raw和asset里面的视频文件
     */
    void setDataSource(@NonNull AssetFileDescriptor fd);

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 获取当前播放的位置
     */
    long getCurrentPosition();

    /**
     * 获取视频总时长
     */
    long getDuration();

    /**
     * 获取缓冲百分比
     */
    @IntRange(from = 0,to = 100)
    int getBufferedPercentage();

    /**
     * 调整进度
     *
     * @param msec the offset in milliseconds from the start to seek to;偏移位置（毫秒）
     */
    void seekTo(long msec);

    /**
     * 是否正在播放
     */
    boolean isPlaying();

    /**
     * 设置音量 ；0.0f-1.0f 之间
     *
     * @param leftVolume  左声道音量
     * @param rightVolume 右声道音量
     */
    void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float leftVolume, @FloatRange(from = 0.0f, to = 1.0f) float rightVolume);
}
