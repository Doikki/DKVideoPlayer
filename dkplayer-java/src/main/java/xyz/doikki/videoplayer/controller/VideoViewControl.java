package xyz.doikki.videoplayer.controller;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.render.AspectRatioType;
import xyz.doikki.videoplayer.render.Render;

/**
 * 播放控制类：用于提供给Controller控制播放器（VideoView）
 * 在{@link MediaPlayerControl}功能的基础上新增了视图方面的控制
 */
public interface VideoViewControl extends MediaPlayerControl {

    /**
     * 开始全屏;
     * 屏幕切换之后会回调{@link xyz.doikki.videoplayer.VideoView.OnStateChangeListener#onScreenModeChanged(int)}
     *
     * @see xyz.doikki.videoplayer.VideoView#addOnStateChangeListener(VideoView.OnStateChangeListener)
     */
    void startFullScreen();

    /**
     * 结束全屏
     * 屏幕切换之后会回调{@link xyz.doikki.videoplayer.VideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void stopFullScreen();

    /**
     * 是否是全屏状态
     *
     * @return
     */
    boolean isFullScreen();

    /**
     * 开始小窗播放
     * 屏幕切换之后会回调{@link xyz.doikki.videoplayer.VideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void startTinyScreen();

    /**
     * 结束小窗播放
     * 屏幕切换之后会回调{@link xyz.doikki.videoplayer.VideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void stopTinyScreen();

    /**
     * 当前是否是小窗播放状态
     *
     * @return
     */
    boolean isTinyScreen();

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


    /*以下方法还未梳理*/

    /**
     * 获取缓冲网速：只有IJK播放器支持
     *
     * @return
     */
    long getTcpSpeed();


    void setMirrorRotation(boolean enable);


    int[] getVideoSize();

    void setRotation(float rotation);


}