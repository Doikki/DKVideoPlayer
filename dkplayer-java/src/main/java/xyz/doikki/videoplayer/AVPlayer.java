package xyz.doikki.videoplayer;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

/**
 * 抽象的播放器，继承此接口扩展自己的播放器
 * <p>
 * 备注：本类的职责应该完全定位在播放器的“能力”上，因此只考虑播放相关的逻辑（不包括UI层面）
 * <p>
 * Created by Doikki on 2017/12/21.
 * update by luochao on 2022/9/16. 调整部分代码及结构
 */
public interface AVPlayer {

    /**
     * 视频/音频开始渲染
     */
    int MEDIA_INFO_RENDERING_START = 3;

    /**
     * 缓冲开始
     */
    int MEDIA_INFO_BUFFERING_START = 701;

    /**
     * 缓冲结束
     */
    int MEDIA_INFO_BUFFERING_END = 702;

    /**
     * 视频旋转信息
     */
    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /**
     * 初始化播放器实例
     * todo 该方法放在这里不合理，是否是为了懒加载实现？
     */
    void init();

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
    @IntRange(from = 0, to = 100)
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

    /**
     * 准备开始播放（异步）
     */
    void prepareAsync();

    /**
     * 停止
     */
    void stop();

    /**
     * 重置播放器
     */
    void reset();


    /**
     * 释放播放器
     * 释放之后此播放器就不能再被使用
     */
    void release();

    /**
     * 设置渲染视频的View,主要用于TextureView
     */
    void setSurface(Surface surface);

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    void setDisplay(SurfaceHolder holder);

    /**
     * 设置是否循环播放
     */
    void setLooping(boolean isLooping);

    /**
     * 设置播放速度
     */
    @PartialFunc(message = "使用系统播放器时，只有6.0及以上系统才支持")
    void setSpeed(float speed);

    /**
     * 获取播放速度
     * 注意：使用系统播放器时，只有6.0及以上系统才支持
     */
    float getSpeed();

    /**
     * 获取当前缓冲的网速
     */
    @PartialFunc(message = "IJK播放器才支持")
    long getTcpSpeed();

    /**
     * 设置播放器事件监听
     */
    void setEventListener(EventListener eventListener);

    /**
     * 事件监听器
     */
    interface EventListener {

        default void onPrepared() {
        }

        default void onInfo(int what, int extra) {

        }

        default void onVideoSizeChanged(int width, int height) {

        }

        default void onCompletion() {

        }

        default void onError(Throwable e) {

        }
    }

}
