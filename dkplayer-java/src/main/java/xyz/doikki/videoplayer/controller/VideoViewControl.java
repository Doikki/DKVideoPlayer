package xyz.doikki.videoplayer.controller;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.render.AspectRatioType;
import xyz.doikki.videoplayer.render.Render;

/**
 * 播放控制类：用于提供给Controller控制播放器（VideoView）
 * 在{@link DKPlayerControl}功能的基础上新增了视图方面的控制
 */
public interface VideoViewControl extends DKPlayerControl, ScreenModeControl {

    /**
     * 设置界面比例（宽比高）模式
     *
     * @param aspectRatioType 类型
     */
    void setScreenAspectRatioType(@AspectRatioType int aspectRatioType);

    /**
     * 截图
     *
     * @see Render#screenshot(Render.ScreenShotCallback)
     */
    default void screenshot(@NonNull Render.ScreenShotCallback callback) {
        screenshot(false, callback);
    }

    /**
     * 截图
     *
     * @see Render#screenshot(boolean, Render.ScreenShotCallback)
     */
    void screenshot(boolean highQuality, @NonNull Render.ScreenShotCallback callback);

    /**
     * 设置静音
     *
     * @param isMute true:静音 false：相反
     */
    void setMute(boolean isMute);

    /**
     * 当前是否静音
     *
     * @return true:静音 false：相反
     */
    boolean isMute();

    /**
     * 设置播放控件旋转角度
     *
     * @param degree 角度 0-360
     */
    void setRotation(@IntRange(from = 0, to = 360) int degree);

    /**
     * 获取图像宽高
     *
     * @return 0-width 1-height
     */
    int[] getVideoSize();


    /*以下方法还未梳理*/

    /**
     * 获取缓冲网速：只有IJK播放器支持
     *
     * @return
     */
    long getTcpSpeed();

    /**
     * 设置镜像旋转
     */
    void setMirrorRotation(boolean enable);

}