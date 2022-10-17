package xyz.doikki.videoplayer.controller;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.render.AspectRatioType;
import xyz.doikki.videoplayer.render.Render;

/**
 * 播放控制类：用于提供给Controller控制播放器（VideoView）
 * 在{@link PlayerControl}功能的基础上新增了视图方面的控制
 */
public interface VideoViewControl extends PlayerControl {

    /**********[Start]全屏、小窗口、自动横屏等 屏幕相关操作所需接口***********/
    /**
     * 是否是全屏状态
     *
     * @return
     */
    boolean isFullScreen();

    /**
     * 当前是否是小窗播放状态
     *
     * @return
     */
    boolean isTinyScreen();

    /**
     * 横竖屏切换
     */
    boolean toggleFullScreen();

    default boolean startFullScreen(){
        return startFullScreen(false);
    }

    /**
     * 开始全屏;
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     * @param isLandscapeReversed 是否是反向横屏
     * @see DKVideoView#addOnStateChangeListener(DKVideoView.OnStateChangeListener)
     */
    boolean startFullScreen(boolean isLandscapeReversed);

    /**
     * 结束全屏
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    boolean stopFullScreen();

    /**
     * 开始VideoView全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与{@link #startFullScreen()}方法的区别在于，此方法不会调用{@link android.app.Activity#setRequestedOrientation(int)}改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    boolean startVideoViewFullScreen();

    /**
     * 结束VideoView的全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与{@link #startFullScreen()}方法的区别在于，此方法不会调用{@link android.app.Activity#setRequestedOrientation(int)}改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    boolean stopVideoViewFullScreen();

    /**
     * 开始小窗播放
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void startTinyScreen();

    /**
     * 结束小窗播放
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void stopTinyScreen();

    /**********[END]全屏、小窗口、自动横屏等 屏幕相关操作所需接口***********/

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