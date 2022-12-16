package xyz.doikki.videoplayer.controller

import androidx.annotation.IntRange
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.Render
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback

/**
 * 播放控制类：用于提供给Controller控制播放器（VideoView）
 * 在[PlayerControl]功能的基础上新增了视图方面的控制
 */
interface VideoViewControl : PlayerControl {
    //**********[Start]全屏、小窗口、自动横屏等 屏幕相关操作所需接口***********/
    /**
     * 是否是全屏状态
     *
     * @return
     */
    val isFullScreen: Boolean

    /**
     * 当前是否是小窗播放状态
     *
     * @return
     */
    val isTinyScreen: Boolean

    /**
     * 横竖屏切换
     */
    fun toggleFullScreen(): Boolean
    fun startFullScreen(): Boolean {
        return startFullScreen(false)
    }

    /**
     * 开始全屏;
     * 屏幕切换之后会回调[VideoView.OnStateChangeListener.onScreenModeChanged]
     *
     * @param isLandscapeReversed 是否是反向横屏
     * @see VideoView.addOnStateChangeListener
     */
    fun startFullScreen(isLandscapeReversed: Boolean): Boolean

    /**
     * 结束全屏
     * 屏幕切换之后会回调[VideoView.OnStateChangeListener.onScreenModeChanged]
     */
    fun stopFullScreen(): Boolean

    /**
     * 开始VideoView全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与[.startFullScreen]方法的区别在于，此方法不会调用[android.app.Activity.setRequestedOrientation]改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    fun startVideoViewFullScreen(): Boolean

    /**
     * 结束VideoView的全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与[.startFullScreen]方法的区别在于，此方法不会调用[android.app.Activity.setRequestedOrientation]改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    fun stopVideoViewFullScreen(): Boolean

    /**
     * 开始小窗播放
     * 屏幕切换之后会回调[VideoView.OnStateChangeListener.onScreenModeChanged]
     */
    fun startTinyScreen()

    /**
     * 结束小窗播放
     * 屏幕切换之后会回调[VideoView.OnStateChangeListener.onScreenModeChanged]
     */
    fun stopTinyScreen()
    //**********[END]全屏、小窗口、自动横屏等 屏幕相关操作所需接口***********/
    /**
     * 设置界面比例（宽比高）模式
     *
     * @param aspectRatioType 类型
     */
    fun setScreenAspectRatioType(@AspectRatioType aspectRatioType: Int)

    /**
     * 截图
     *
     * @see Render.screenshot
     */
    fun screenshot(callback: ScreenShotCallback) {
        screenshot(false, callback)
    }

    /**
     * 截图
     *
     * @see Render.screenshot
     */
    fun screenshot(highQuality: Boolean, callback: ScreenShotCallback)

    /**
     * 设置静音
     *
     * @param isMute true:静音 false：相反
     */
    var isMute: Boolean

    /**
     * 设置播放控件旋转角度
     *
     * @param degree 角度 0-360
     */
    fun setRotation(@IntRange(from = 0, to = 360) degree: Int)

    /**
     * 获取缓冲网速：只有IJK播放器支持
     *
     * @return
     */
    val tcpSpeed: Long

    /**
     * 设置镜像旋转
     */
    fun setMirrorRotation(enable: Boolean)
}