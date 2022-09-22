package xyz.doikki.videoplayer.controller;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.PartialFunc;
import xyz.doikki.videoplayer.render.AspectRatioType;
import xyz.doikki.videoplayer.render.Render;

/**
 * 作为一个基础控制器需要具备的功能接口
 */
public interface VideoController {

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 设置界面比例模式
     *
     * @param aspectRatioType 类型
     */
    void setScreenAspectRatioType(@AspectRatioType int aspectRatioType);

//    todo 是否应该提供该方法？提供的话，这个变量到底是应该render持有，还是controller持有？
//    int getScreenAspectRatioType();

    /**
     * 设置播放速度
     * @param speed 0.5f：表示0.5倍数 2f:表示2倍速
     */
    void setSpeed(float speed);

    /**
     * 获取播放速度
     * @return
     */
    float getSpeed();

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
}
