package xyz.doikki.videoplayer.controller;

import androidx.annotation.IntRange;

/**
 * 作为一个基本播放器控制器需要持有的功能
 * 是播放器去实现的接口（类似VideoView），然后再将这个接口的实现传递给Controller
 */
public interface DKPlayerControl {

    /**
     * 开始播放
     */
    void start();

    /**
     * 重新播放
     *
     * @param resetPosition 是否重置播放位置；通常有以下情况不用应该不重置播放位置：1、播放失败之后重新播放 2、清晰度切换之后重新播放
     */
    void replay(boolean resetPosition);

    /**
     * 暂停
     */
    void pause();

    /**
     * 播放时长
     *
     * @return
     */
    long getDuration();

    /**
     * 当前播放位置
     *
     * @return
     */
    long getCurrentPosition();

    /**
     * 调整播放位置
     *
     * @param msec the offset in milliseconds from the start to seek to;偏移位置（毫秒）
     */
    void seekTo(long msec);

    /**
     * 是否正在播放
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 获取缓冲百分比
     */
    @IntRange(from = 0, to = 100)
    int getBufferedPercentage();


    /*以下是扩展的播放器功能代码*/

    /**
     * 设置播放速度
     *
     * @param speed 0.5f：表示0.5倍数 2f:表示2倍速
     */
    void setSpeed(float speed);

    /**
     * 获取播放速度
     *
     * @return
     */
    float getSpeed();

}
