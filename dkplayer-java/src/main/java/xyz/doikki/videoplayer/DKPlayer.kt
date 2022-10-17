package xyz.doikki.videoplayer

import android.content.res.AssetFileDescriptor
import android.view.Surface
import android.view.SurfaceHolder
import androidx.annotation.FloatRange
import androidx.annotation.IntRange

/**
 * 抽象的播放器，继承此接口扩展自己的播放器
 * 备注：本类的职责应该完全定位在播放器的“能力”上，因此只考虑播放相关的逻辑（不包括UI层面）
 * Created by Doikki on 2017/12/21.
 * update by luochao on 2022/9/16. 调整部分代码及结构
 * @see AbstractDKPlayer
 */
interface DKPlayer {

    companion object {
        /**
         * 视频/音频开始渲染
         */
        const val MEDIA_INFO_RENDERING_START = 3

        /**
         * 缓冲开始
         */
        const val MEDIA_INFO_BUFFERING_START = 701

        /**
         * 缓冲结束
         */
        const val MEDIA_INFO_BUFFERING_END = 702

        /**
         * 视频旋转信息
         */
        const val MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001
    }

    /**
     * 初始化播放器实例
     * todo 该方法放在这里不合理，是否是为了懒加载实现？ 是否需要考虑播放器的重用
     */
    fun init()

    /**
     * 设置播放地址
     *
     * @param path 播放地址
     */
    fun setDataSource(path: String) {
        setDataSource(path, null)
    }

    /**
     * 设置播放地址
     *
     * @param path    播放地址
     * @param headers 播放地址请求头
     */
    fun setDataSource(path: String, headers: Map<String, String>?)

    /**
     * 用于播放raw和asset里面的视频文件
     */
    fun setDataSource(fd: AssetFileDescriptor)

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean

    /**
     * 准备开始播放（异步）
     */
    fun prepareAsync()

    /**
     * 开始播放
     */
    fun start()

    /**
     * 获取当前播放的位置
     */
    fun getCurrentPosition(): Long

    /**
     * 获取视频总时长
     */
    fun getDuration(): Long

    /**
     * 获取缓冲百分比
     */
    @IntRange(from = 0, to = 100)
    fun getBufferedPercentage(): Int

    /**
     * 设置是否循环播放
     */
    fun setLooping(isLooping: Boolean)

    /**
     * 设置音量 ；0.0f-1.0f 之间
     *
     * @param leftVolume  左声道音量
     * @param rightVolume 右声道音量
     */
    fun setVolume(
        @FloatRange(from = 0.0, to = 1.0) leftVolume: Float,
        @FloatRange(from = 0.0, to = 1.0) rightVolume: Float
    )

    /**
     * 获取播放速度
     * 注意：使用系统播放器时，只有6.0及以上系统才支持，6.0以下默认返回1
     */
    @PartialFunc(message = "使用系统播放器时，只有6.0及以上系统才支持")
    fun getSpeed(): Float

    /**
     * 设置播放速度
     * 注意：使用系统播放器时，只有6.0及以上系统才支持
     */
    @PartialFunc(message = "使用系统播放器时，只有6.0及以上系统才支持")
    fun setSpeed(speed: Float)

    /**
     * 调整进度
     *
     * @param msec the offset in milliseconds from the start to seek to;偏移位置（毫秒）
     */
    fun seekTo(msec: Long)

    /**
     * 暂停
     */
    fun pause()

    /**
     * 停止
     */
    fun stop()

    /**
     * 重置播放器
     */
    fun reset()

    /**
     * 释放播放器
     * 释放之后此播放器就不能再被使用
     */
    fun release()

    /**
     * 设置渲染视频的View,主要用于TextureView
     */
    fun setSurface(surface: Surface?)

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    fun setDisplay(holder: SurfaceHolder?)

    /**
     * 设置播放器事件监听
     */
    fun setEventListener(eventListener: EventListener?)

    /**
     * 获取当前缓冲的网速
     */
    @PartialFunc(message = "IJK播放器才支持")
    fun getTcpSpeed(): Long {
        return 0
    }

    /**
     * 事件监听器
     */
    interface EventListener {

        /**
         * 播放就绪
         */
        fun onPrepared() {}

        /**
         * 播放信息
         */
        fun onInfo(what: Int, extra: Int) {}

        /**
         * 视频大小发生变化
         */
        fun onVideoSizeChanged(width: Int, height: Int) {}

        /**
         * 播放完成
         */
        fun onCompletion() {}

        /**
         * 播放错误
         */
        fun onError(e: Throwable) {}
    }

}