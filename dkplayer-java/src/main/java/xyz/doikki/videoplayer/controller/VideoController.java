package xyz.doikki.videoplayer.controller;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.render.Render;

public interface VideoController {

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
