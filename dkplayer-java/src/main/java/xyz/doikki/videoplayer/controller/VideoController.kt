package xyz.doikki.videoplayer.controller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import xyz.doikki.videoplayer.*
import xyz.doikki.videoplayer.VideoView.PlayerState
import xyz.doikki.videoplayer.controller.component.ControlComponent
import xyz.doikki.videoplayer.util.*

/**
 * 控制器基类
 * 对外提供可以控制的方法
 */
open class VideoController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), VideoViewController {

    /**
     * 当前控制器中保存的所有控制组件
     */
    @JvmField
    protected val controlComponents = LinkedHashMap<ControlComponent, Boolean>()

    /**
     * 绑定的播放器
     */
    protected var player: VideoViewControl? = null

    /**
     * 当前播放器状态
     */
    @PlayerState
    private var playerState = VideoView.STATE_IDLE

    /**
     * 显示动画
     */
    private val showAnim by lazy {
        AlphaAnimation(0f, 1f).apply {
            duration = 300
        }
    }

    /**
     * 隐藏动画
     */
    private val hideAnim by lazy {
        AlphaAnimation(1f, 0f).apply {
            duration = 300
        }
    }

    /**
     * 控制器显示超时时间：即显示超过该时间后自动隐藏
     */
    private var defaultTimeout = 4000L

    /**
     * 自动隐藏的Runnable
     */
    private val fadeOut = Runnable { hide() }

    /**
     * 是否开始刷新进度
     */
    private var progressRefreshing = false

    /**
     * 刷新进度Runnable
     */
    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            val pos = updateProgress()
            val player = player ?: return
            if (player.isPlaying) {
                postDelayed(this, ((1000 - pos % 1000) / player.speed).toLong())
            } else {
                progressRefreshing = false
            }
        }
    }

    /**
     * 用户设置是否适配刘海屏
     */
    override var adaptCutout = GlobalConfig.isAdaptCutout

    /**
     * 是否有刘海, null未知 true有 false没有
     */
    override var hasCutout: Boolean? = null

    val playerControl: VideoViewControl? get() = player

    /**
     * 是否处于播放状态
     *
     * @return
     */
    protected val isInPlaybackState: Boolean
        get() = playerState != VideoView.STATE_ERROR
                && playerState != VideoView.STATE_IDLE
                && playerState != VideoView.STATE_PREPARING
                && playerState != VideoView.STATE_PREPARED
                && playerState != VideoView.STATE_START_ABORT
                && playerState != VideoView.STATE_PLAYBACK_COMPLETED

    protected val isInCompleteState: Boolean get() = playerState == VideoView.STATE_PLAYBACK_COMPLETED

    protected val isInErrorState: Boolean get() = playerState == VideoView.STATE_ERROR

    /**
     * 重要：此方法用于将[VideoView] 和控制器绑定
     */
    @CallSuper
    open fun setMediaPlayer(mediaPlayer: VideoViewControl) {
        player = mediaPlayer
        //绑定ControlComponent和Controller
        for ((component) in controlComponents) {
            component.onPlayerAttached(mediaPlayer)
        }
    }

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     */
    fun addControlComponent(vararg component: ControlComponent) {
        for (item in component) {
            addControlComponent(item, false)
        }
    }

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     *
     * @param isDissociate 是否为游离的控制组件，
     * 如果为 true ControlComponent 不会添加到控制器中，ControlComponent 将独立于控制器而存在，
     * 如果为 false ControlComponent 将会被添加到控制器中，并显示出来。
     * 为什么要让 ControlComponent 将独立于控制器而存在，假设有如下几种情况：
     * 情况一：
     * 如果在一个列表中控制器是复用的，但是控制器的某些部分是不能复用的，比如封面图，
     * 此时你就可以将封面图拆分成一个游离的 ControlComponent，并把这个 ControlComponent
     * 放在 item 的布局中，就可以实现每个item的封面图都是不一样，并且封面图可以随着播放器的状态显示和隐藏。
     * demo中演示的就是这种情况。
     * 情况二：
     * 假设有这样一种需求，播放器控制区域在显示区域的下面，此时你就可以通过自定义 ControlComponent
     * 并将 isDissociate 设置为 true 来实现这种效果。
     */
    fun addControlComponent(component: ControlComponent, isDissociate: Boolean) {
        controlComponents[component] = isDissociate
        component.attachController(this)
        val view = component.getView()
        if (view != null && !isDissociate) {
            addView(view, 0)
        }
    }

    /**
     * 移除某个控制组件
     */
    fun removeControlComponent(component: ControlComponent) {
        removeControlComponentView(component)
        controlComponents.remove(component)
    }

    /**
     * 移除所有控制组件
     */
    fun removeAllControlComponent() {
        for ((key) in controlComponents) {
            removeControlComponentView(key)
        }
        controlComponents.clear()
    }

    /**
     * 移除所有的游离控制组件
     * 关于游离控制组件的定义请看 [.addControlComponent] 关于 isDissociate 的解释
     */
    fun removeAllDissociateComponents() {
        controlComponents.removeAllByValue {
            it
        }
    }

    /**
     * 从当前控制器中移除添加的控制器view
     *
     * @param component
     */
    private fun removeControlComponentView(component: ControlComponent) {
        val view = component.getView() ?: return
        removeView(view)
    }

    override val isFullScreen: Boolean
        get() = invokeOnPlayerAttached { it.isFullScreen }.orDefault()

    /**
     * 横竖屏切换
     */
    override fun toggleFullScreen(): Boolean {
        return invokeOnPlayerAttached { it.toggleFullScreen() }.orDefault()
    }

    override fun startFullScreen(isLandscapeReversed: Boolean): Boolean {
        return invokeOnPlayerAttached { it.startFullScreen(isLandscapeReversed) }.orDefault()
    }

    override fun stopFullScreen(): Boolean {
        return invokeOnPlayerAttached { it.stopFullScreen() }.orDefault()
    }

    /**
     * 锁定
     */
    override var isLocked: Boolean = false
        set(value) {
            if (field != value) {
                notifyLockStateChanged(value)
            }
            field = value
        }

    /**
     * 设置当前[VideoView]界面模式：竖屏、全屏、小窗模式等
     * 是当[VideoView]修改视图之后，调用此方法向控制器同步状态
     */
    @CallSuper
    open fun setScreenMode(@VideoView.ScreenMode screenMode: Int) {
        notifyScreenModeChanged(screenMode)
    }

    /**
     * call by [VideoView],设置播放器当前播放状态
     */
    @SuppressLint("SwitchIntDef")
    @CallSuper
    fun setPlayerState(@PlayerState playState: Int) {
        playerState = playState
        for ((key) in controlComponents) {
            key.onPlayStateChanged(playState)
        }
        when (playState) {
            VideoView.STATE_IDLE -> {
                isLocked = false
                isShowing = false
                //由于游离组件是独立于控制器存在的，
                //所以在播放器release的时候需要移除
                removeAllDissociateComponents()
            }
            VideoView.STATE_PLAYBACK_COMPLETED -> {
                isLocked = false
                isShowing = false
            }
            VideoView.STATE_ERROR -> isShowing = false
        }
        onPlayerStateChanged(playState)
    }

    /**
     * 控制器是否处于显示状态
     */
    override var isShowing: Boolean = false

    /**
     * 显示播放视图
     */
    override fun show() {
        startFadeOut()
        if (isShowing) return
        handleVisibilityChanged(true, showAnim)
        isShowing = true
    }

    /**
     * 隐藏播放视图
     */
    override fun hide() {
        stopFadeOut()
        if (!isShowing) return
        handleVisibilityChanged(false, hideAnim)
        isShowing = false
    }

    /**
     * 设置自动隐藏倒计时持续的时间
     *
     * @param timeout 默认4000，比如大于0才会生效
     */
    override fun setFadeOutTime(@IntRange(from = 1) timeout: Int) {
        if (timeout > 0) {
            defaultTimeout = timeout.toLong()
        }
    }

    /**
     * 开始倒计时隐藏控制器
     */
    override fun startFadeOut() {
        //重新开始计时
        stopFadeOut()
        postDelayed(fadeOut, defaultTimeout)
    }

    /**
     * 移除控制器隐藏倒计时
     */
    override fun stopFadeOut() {
        removeCallbacks(fadeOut)
    }

    /**
     * 开始刷新进度，注意：需在STATE_PLAYING时调用才会开始刷新进度
     */
    override fun startUpdateProgress() {
        if (progressRefreshing) return
        post(progressUpdateRunnable)
        progressRefreshing = true
    }

    /**
     * 停止刷新进度
     */
    override fun stopUpdateProgress() {
        if (!progressRefreshing) return
        removeCallbacks(progressUpdateRunnable)
        progressRefreshing = false
    }

    /**
     * 刘海的高度
     */
    override var cutoutHeight: Int = 0

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        checkCutout()
    }

    /**
     * 检查是否需要适配刘海
     */
    private fun checkCutout() {
        if (!adaptCutout) return
        val activity = PlayerUtils.scanForActivity(context)
        if (activity != null && hasCutout == null) {
            hasCutout = CutoutUtil.allowDisplayToCutout(activity)
            if (hasCutout == true) {
                //竖屏下的状态栏高度可认为是刘海的高度
                cutoutHeight = PlayerUtils.getStatusBarHeightPortrait(activity).toInt()
            }
        }
        L.d("hasCutout: $hasCutout cutout height: $cutoutHeight")
    }

    /**
     * 中断开始播放流程
     * @return 返回是否要中断播放流程，false:不中断, true中断
     */
    open fun abortPlay(): Boolean {
        return false
    }

    /**
     * 播放和暂停
     */
    fun togglePlay() {
        invokeOnPlayerAttached {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.start()
            }
        }
    }

    /**
     * @return true:调用了重播方法，false则表示未处理任何
     */
    fun replay(resetPosition: Boolean = true): Boolean {
        return invokeOnPlayerAttached { it.replay(resetPosition); true }.orDefault(false)
    }

    //------------------------ start handle event change ------------------------//

    private fun handleVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (!isLocked) { //没锁住时才向ControlComponent下发此事件
            for ((component) in controlComponents) {
                component.onVisibilityChanged(isVisible, anim)
            }
        }
        onVisibilityChanged(isVisible, anim)
    }

    /**
     * 更新当前播放进度
     * @return 当前播放位置
     */
    private fun updateProgress(): Int {
        val player = player ?: return 0
        val position = player.currentPosition.toInt()
        val duration = player.duration.toInt()
        for ((component) in controlComponents) {
            component.onProgressChanged(duration, position)
        }
        onProgressChanged(duration, position)
        return position
    }

    /**
     * 通知界面模式发生改变
     *
     * @param screenMode
     */
    private fun notifyScreenModeChanged(@VideoView.ScreenMode screenMode: Int) {
        for ((component) in controlComponents) {
            component.onScreenModeChanged(screenMode)
        }
        setupCutoutOnScreenModeChanged(screenMode)
        onScreenModeChanged(screenMode)
    }

    /**
     * 在屏幕模式改变了的情况下，调整刘海屏
     *
     * @param screenMode
     */
    private fun setupCutoutOnScreenModeChanged(@VideoView.ScreenMode screenMode: Int) {
        when (screenMode) {
            VideoView.SCREEN_MODE_NORMAL, VideoView.SCREEN_MODE_TINY -> {
                if (hasCutout == true) {
                    CutoutUtil.adaptCutoutAboveAndroidP(context, false)
                }
            }
            VideoView.SCREEN_MODE_FULL -> {
                if (hasCutout == true) {
                    CutoutUtil.adaptCutoutAboveAndroidP(context, true)
                }
            }
        }
    }

    /**
     * 通知锁定状态发生了变化
     */
    private fun notifyLockStateChanged(isLocked: Boolean) {
        for ((component) in controlComponents) {
            component.onLockStateChanged(isLocked)
        }
        onLockStateChanged(isLocked)
    }


    /**
     * 子类重写此方法监听控制的显示和隐藏
     *
     * @param isVisible 是否可见
     * @param anim      显示/隐藏动画
     */
    protected open fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {}

    /**
     * 用于子类重写
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前播放位置
     */
    protected open fun onProgressChanged(duration: Int, position: Int) {}

    /**
     * 用于子类重写
     */
    protected open fun onScreenModeChanged(screenMode: Int) {}

    /**
     * 用于子类重写
     */
    protected open fun onPlayerStateChanged(@PlayerState playState: Int) {}

    /**
     * 用于子类重写
     */
    protected open fun onLockStateChanged(isLocked: Boolean) {}

    /**
     * 改变返回键逻辑，用于activity
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    //------------------------ end handle event change ------------------------//

    /**
     * 切换显示/隐藏状态
     */
    fun toggleShowState() {
        if (isShowing) {
            hide()
        } else {
            show()
        }
    }

    fun toggleLock() {
        isLocked = !isLocked
    }

    /**
     * 设置播放控件旋转角度
     *
     * @param degree 角度 0-360
     */
    fun setRotation(@IntRange(from = 0, to = 360) degree: Int) {
        invokeOnPlayerAttached { it.setRotation(degree) }
    }

    protected inline fun <R> invokeOnPlayerAttached(
        block: (VideoViewControl) -> R
    ): R {
        val player = player ?: throw RuntimeException("Set current VideoController to VideoView first.")
        return block.invoke(player)
    }

//    /**
//     * 横竖屏切换，根据适配宽高决定是否旋转屏幕
//     */
//    open fun toggleFullScreenByVideoSize(activity: Activity?) {
//        if (activity == null || activity.isFinishing) return
//        val size: IntArray = getVideoSize()
//        val width = size[0]
//        val height = size[1]
//        if (isFullScreen) {
//            stopVideoViewFullScreen()
//            if (width > height) {
//                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            }
//        } else {
//            startVideoViewFullScreen()
//            if (width > height) {
//                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            }
//        }
//    }
}