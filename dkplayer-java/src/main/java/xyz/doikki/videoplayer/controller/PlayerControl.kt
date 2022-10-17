package xyz.doikki.videoplayer.controller

import androidx.annotation.IntRange

/**
 * 作为一个基本播放器控制器需要持有的功能
 * 是播放器去实现的接口（类似VideoView），然后再将这个接口的实现传递给Controller
 */
interface PlayerControl {
    /**
     * 开始播放
     */
    fun start()

    /**
     * 重新播放
     *
     * @param resetPosition 是否重置播放位置；通常有以下情况不用应该不重置播放位置：1、播放失败之后重新播放 2、清晰度切换之后重新播放
     */
    fun replay(resetPosition: Boolean)

    /**
     * 暂停
     */
    fun pause()

    /**
     * 播放时长
     *
     * @return
     */
    val duration: Long

    /**
     * 当前播放位置
     *
     * @return
     */
    val currentPosition: Long

    /**
     * 调整播放位置
     *
     * @param position the offset in milliseconds from the start to seek to;偏移位置（毫秒）
     */
    fun seekTo(position: Long)

    /**
     * 是否正在播放
     *
     * @return
     */
    val isPlaying: Boolean

    /**
     * 获取缓冲百分比
     */
    @get:IntRange(from = 0, to = 100)
    val bufferedPercentage: Int
    /*以下是扩展的播放器功能代码*/
    /**
     * 获取播放速度
     *
     * @return
     */
    /**
     * 设置播放速度
     *
     * @param speed 0.5f：表示0.5倍数 2f:表示2倍速
     */
    var speed: Float
}