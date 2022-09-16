package xyz.doikki.videoplayer;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

/**
 * 抽象的播放器，继承此接口扩展自己的播放器
 * 备注：本类的职责应该完全定位在播放器的“能力”上
 *
 * Created by Doikki on 2017/12/21.
 * update by luochao on 2022/9/16. 调整部分代码及结构
 */
public abstract class MediaPlayer {

    /**
     * 视频/音频开始渲染
     */
    public static final int MEDIA_INFO_RENDERING_START = 3;

    /**
     * 缓冲开始
     */
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    /**
     * 缓冲结束
     */
    public static final int MEDIA_INFO_BUFFERING_END = 702;

    /**
     * 视频旋转信息
     */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /**
     * 播放器事件回调
     */
    protected EventListener mPlayerEventListener;

    /**
     * 初始化播放器实例
     * todo 该方法放在这里不合理，是否是为了懒加载实现？
     */
    public abstract void initPlayer();

    /**
     * 设置播放地址
     *
     * @param path 播放地址
     */
    public void setDataSource(String path) {
        setDataSource(path, null);
    }

    /**
     * 设置播放地址
     *
     * @param path    播放地址
     * @param headers 播放地址请求头
     */
    public abstract void setDataSource(String path, Map<String, String> headers);

    /**
     * 用于播放raw和asset里面的视频文件
     */
    public abstract void setDataSource(AssetFileDescriptor fd);

    /**
     * 准备开始播放（异步）
     */
    public abstract void prepareAsync();

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
     * 重置播放器
     */
    public abstract void reset();

    /**
     * 是否正在播放
     */
    public abstract boolean isPlaying();

    /**
     * 调整进度
     *
     * @param timeMills 毫秒
     */
    public abstract void seekTo(long timeMills);

    /**
     * 释放播放器
     * 释放之后此播放器就不能再被使用
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
     * 获取缓冲百分比
     */
    public abstract int getBufferedPercentage();

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
     * @param leftVolume 左音量
     * @param rightVolume 右音量
     */
    public abstract void setVolume(float leftVolume, float rightVolume);

    /**
     * 设置是否循环播放
     */
    public abstract void setLooping(boolean isLooping);

    /**
     * 设置播放速度
     * 注意：使用系统播放器时，只有6.0及以上系统才支持
     */
    public abstract void setSpeed(float speed);

    /**
     * 获取播放速度
     * 注意：使用系统播放器时，只有6.0及以上系统才支持
     */
    public abstract float getSpeed();

    /**
     * 获取当前缓冲的网速（IJK播放器才支持）
     */
    public abstract long getTcpSpeed();

    /**
     * 设置播放器事件监听
     */
    public void setEventListener(EventListener playerEventListener) {
        this.mPlayerEventListener = playerEventListener;
    }

    /**
     * 设置其他播放配置
     * todo 该方法放在这里不合理
     */
    public abstract void setOptions();

    /**
     * 事件监听器
     */
    public interface EventListener {
        void onPrepared();
        void onInfo(int what, int extra);
        void onVideoSizeChanged(int width, int height);
        void onCompletion();
        void onError(Throwable e);
    }

}
