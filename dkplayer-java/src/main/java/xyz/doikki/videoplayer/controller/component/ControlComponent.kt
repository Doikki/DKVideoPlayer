package xyz.doikki.videoplayer.controller.component

import android.view.View
import android.view.animation.Animation
import xyz.doikki.videoplayer.DKManager
import xyz.doikki.videoplayer.DKVideoView.ScreenMode
import xyz.doikki.videoplayer.controller.MediaController
import xyz.doikki.videoplayer.controller.VideoViewControl

/**
 * 控制器中的控制组件
 */
interface ControlComponent {

    /**
     * 将 控制器 传递到当前 ControlComponent 中
     */
    fun attachController(controller: MediaController)

    /**
     * 绑定了播放器:用于先绑定controller之后，再绑定的播放器情况
     */
    fun onPlayerAttached(player: VideoViewControl) {}

    /**
     * 如果 ControlComponent 是 View，返回当前控件（this）即可；如果不是，返回null
     */
    fun getView(): View?

    /**
     * 是否采用焦点模式：用于处理Component内部控件的操作模式
     *
     * @return
     */
    fun isTelevisionUiMode(): Boolean {
        return DKManager.isTelevisionUiMode
    }

    /**
     * 播放器界面模式发生了变化；如果你只是单纯的想监听此状态，建议使用 [DKVideoView.addOnStateChangeListener]监听
     *
     * @param screenMode 播放器界面模式：竖屏、全屏、小窗口
     */
    fun onScreenModeChanged(@ScreenMode screenMode: Int) {}

    /**
     * 回调控制器是否被锁定，锁定后会产生如下影响：
     * 无法响应滑动手势，双击事件，点击显示和隐藏控制UI，跟随重力感应切换横竖屏
     *
     * @param isLocked true:锁定
     */
    fun onLockStateChanged(isLocked: Boolean) {}

    /**
     * 回调控制器显示和隐藏状态，
     * 此方法可用于控制 ControlComponent 中的控件的跟随手指点击显示和隐藏
     *
     * @param isVisible true 代表要显示， false 代表要隐藏
     * @param anim      显示和隐藏的动画，是一个补间Alpha动画
     */
    fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {}

    /**
     * 回调播放器的播放器状态变更；如果只是单纯的想监听状态变更，可以通过[DKVideoView.addOnStateChangeListener]方法增加监听
     *
     * @param playState 播放状态
     */
    fun onPlayStateChanged(playState: Int) {}

    /**
     * 回调播放进度，1秒回调一次
     *
     * @param duration 视频总时长
     * @param position 播放进度
     */
    fun onProgressChanged(duration: Int, position: Int) {}
}