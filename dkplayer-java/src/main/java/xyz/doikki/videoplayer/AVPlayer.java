package xyz.doikki.videoplayer;

import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * 抽象的播放器，继承此接口扩展自己的播放器
 * <p>
 * 备注：本类的职责应该完全定位在播放器的“能力”上，因此只考虑播放相关的逻辑（不包括UI层面）
 * <p>
 * Created by Doikki on 2017/12/21.
 * update by luochao on 2022/9/16. 调整部分代码及结构
 */
public interface AVPlayer extends AVPlayerFunction {

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
