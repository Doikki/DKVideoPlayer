package xyz.doikki.videoplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import xyz.doikki.videoplayer.controller.VideoController
import xyz.doikki.videoplayer.controller.VideoViewControl
import xyz.doikki.videoplayer.internal.ScreenModeHandler
import xyz.doikki.videoplayer.internal.VideoViewContainer
import xyz.doikki.videoplayer.player.IPlayer
import xyz.doikki.videoplayer.player.PlayerFactory
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.Render
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.RenderFactory
import xyz.doikki.videoplayer.util.getActivityContext
import xyz.doikki.videoplayer.util.orDefault
import xyz.doikki.videoplayer.util.tryIgnore
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 播放器&播放视图  内部包含了对应的[IPlayer] 和  [Render]，因此由本类提供这两者的功能能力
 *  本类的数据目前是在内部提供了一个容器，让容器去添加Render和Controller，这样便于界面切换
 *
 * Created by Doikki on 2017/4/7.
 * update by luochao on 2022/9/16
 */
open class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), VideoViewControl, IPlayer.EventListener {

    /**
     * 播放器状态
     */
    @IntDef(
        STATE_ERROR,
        STATE_IDLE,
        STATE_PREPARING,
        STATE_PREPARED,
        STATE_PLAYING,
        STATE_PAUSED,
        STATE_PLAYBACK_COMPLETED,
        STATE_BUFFERING,
        STATE_BUFFERED,
        STATE_START_ABORT
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class PlayerState

    /**
     * 当前播放器的状态
     */
    var playerState = STATE_IDLE
        private set(state) {
            field = state
            notifyPlayerStateChanged()
        }

    /**
     * 屏幕模式
     */
    @IntDef(
        SCREEN_MODE_NORMAL,
        SCREEN_MODE_FULL,
        SCREEN_MODE_TINY
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ScreenMode

    /**
     * 当前屏幕模式：普通、全屏、小窗口
     */
    var screenMode = SCREEN_MODE_NORMAL
        private set(screenMode) {
            field = screenMode
            notifyScreenModeChanged()
        }

    /**
     * OnStateChangeListener集合，保存了所有开发者设置的监听器
     */
    private val stateChangedListeners = CopyOnWriteArrayList<OnStateChangeListener>()

    /**
     * 屏幕模式切换帮助类
     */
    private val screenModeHandler = ScreenModeHandler()

    /**
     * 真正承载播放器视图的容器
     */
    @JvmField
    internal val playerContainer: VideoViewContainer

    /**
     * 自定义播放器构建工厂，继承[PlayerFactory]实现自己的播放核心
     */
    var playerFactory = GlobalConfig.playerFactory

    /**
     * 播放器内核
     */
    protected var player: IPlayer? = null
        private set

    /**
     * 自定义Render，继承[RenderFactory]实现自己的渲染逻辑
     */
    var renderFactory: RenderFactory = GlobalConfig.renderFactory
        set(value) {
            field = value
            playerContainer.renderFactory = value
        }

    /**
     * 左声道音量
     */
    private var leftVolume = 1.0f

    /**
     * 右声道音量
     */
    private var rightVolume = 1.0f

    /**
     * 循环播放， 默认不循环播放
     */
    var looping = false
        set(value) {
            field = value
            player?.setLooping(value)
        }

    //--------- data sources ---------//
    /**
     * 当前播放视频的地址
     */
    private var url: String? = null

    /**
     * 当前视频地址的请求头
     */
    private var headers: MutableMap<String, String>? = null

    private var assetFileDescriptor: AssetFileDescriptor? = null

    /**
     * 当前正在播放视频的位置
     */
    private var pendingPosition = 0L


    private val activityContext: Activity get() = preferredActivity!!

    /**
     * 获取Activity，优先通过Controller去获取Activity
     */
    private val preferredActivity: Activity? get() = (videoController?.context ?: context).getActivityContext()

    /**
     * 设置[playerContainer]的背景色
     */
    fun setPlayerBackgroundColor(color: Int) {
        playerContainer.setBackgroundColor(color)
    }

    /**
     * 设置控制器，传null表示移除控制器
     */
    var videoController: VideoController? = null
        set(value) {
            field = value
            value?.setMediaPlayer(this)
            playerContainer.videoController = value
        }

    /**
     * 设置播放路径
     */
    open fun setDataSource(path: String) {
        setDataSource(path, null)
    }

    /**
     * 设置播放路径，带请求头
     */
    open fun setDataSource(path: String, headers: MutableMap<String, String>? = null) {
        assetFileDescriptor = null
        url = path
        this.headers = headers
    }

    /**
     * 设置 AssetFileDescriptor
     */
    open fun setDataSource(fd: AssetFileDescriptor) {
        url = null
        assetFileDescriptor = fd
    }

    // ---------- 播放流程 START ---------- //

    /**
     * 是否处于播放状态
     */
    protected val isInPlaybackState: Boolean
        get() = player != null
                && playerState != STATE_ERROR
                && playerState != STATE_IDLE
                && playerState != STATE_PREPARING
                && playerState != STATE_START_ABORT
                && playerState != STATE_PLAYBACK_COMPLETED

    /**
     * 是否处于未播放状态
     */
    protected val isInIdleState: Boolean
        get() = playerState == STATE_IDLE

    /**
     * 是否处于可播放但终止播放状态
     */
    private val isInStartingAbortState: Boolean
        get() = playerState == STATE_START_ABORT

    /**
     * 判断是否为本地数据源，包括 本地文件、Asset、raw
     */
    private val isLocalDataSource: Boolean
        get() {
            if (assetFileDescriptor != null) {
                return true
            }
            if (!url.isNullOrEmpty()) {
                val uri = Uri.parse(url)
                return ContentResolver.SCHEME_ANDROID_RESOURCE == uri.scheme
                        || ContentResolver.SCHEME_FILE == uri.scheme
                        || "rawresource" == uri.scheme
            }
            return false
        }

    /**
     * 创建播放器
     */
    protected open fun createMediaPlayer(): IPlayer {
        return playerFactory.create(context)
    }

    /**
     * 初始化之前的配置项
     */
    protected open fun onMediaPlayerCreated(mediaPlayer: IPlayer?) {}

    /**
     * 初始化之后的配置项
     */
    protected open fun setupMediaPlayerOptions() {
        // 重新设置循环播放
        looping = looping
        // 重新设置静音
        isMute = isMute
    }

    /**
     * 初始化播放器
     */
    protected open fun setupMediaPlayer() {
        player = createMediaPlayer().also {
            it.setEventListener(this)
            it.init()
            onMediaPlayerCreated(it)
        }
        setupMediaPlayerOptions()
    }

    /**
     * 设置播放数据
     * @return 播放数据是否设置成功
     */
    protected open fun prepareDataSource(): Boolean {
        if (assetFileDescriptor != null) {
            player!!.setDataSource(assetFileDescriptor!!)
            return true
        } else if (!url.isNullOrEmpty()) {
            player!!.setDataSource(url!!, headers)
            return true
        }
        return false
    }

    /**
     * 开始准备播放（直接播放）
     */
    protected open fun startPrepare(reset: Boolean) {
        if (reset) {
            player!!.reset()
            //重新设置option，media player reset之后，option会失效
            setupMediaPlayerOptions()
        }
        if (prepareDataSource()) {
            player!!.prepareAsync()
            playerState = STATE_PREPARING
            //这里的目的是为了满足历史版本触发通知的情况
            screenMode = screenMode
        }
    }

    /**
     * 第一次播放
     *
     * @return 是否成功开始播放
     */
    protected fun startPlay(): Boolean {
        //中止播放
        if (!isLocalDataSource && videoController?.abortPlay().orDefault()) {
            playerState = STATE_START_ABORT
            return false
        }
        setupMediaPlayer()
        playerContainer.attachPlayer(player!!)
        startPrepare(false)
        return true
    }

    /**
     * 播放状态下开始播放
     */
    protected open fun startInPlaybackState() {
        player!!.start()
        playerState = STATE_PLAYING
        playerContainer.keepScreenOn = true
    }

    /**
     * 开始播放，注意：调用此方法后必须调用[.release]释放播放器，否则会导致内存泄漏
     */
    override fun start() {
        if (isInIdleState || isInStartingAbortState) {
            startPlay()
        } else if (isInPlaybackState) {
            startInPlaybackState()
        }
    }

    // ---------- 播放流程 END ---------- //

    /**
     * 暂停播放
     */
    override fun pause() {
        player?.let { player ->
            if (isInPlaybackState && player.isPlaying()) {
                player.pause()
                playerState = STATE_PAUSED
                playerContainer.keepScreenOn = false
            }
        }
    }

    /**
     * 继续播放
     */
    open fun resume() {
        player?.let { player ->
            if (isInPlaybackState && !player.isPlaying()) {
                player.start()
                playerState = STATE_PLAYING
                playerContainer.keepScreenOn = true
            }
        }
    }

    /**
     * 释放播放器
     */
    open fun release() {
        if (!isInIdleState) {
            //释放播放器
            player?.release()
            //释放render
            playerContainer.release()
            //释放Assets资源
            assetFileDescriptor?.let {
                tryIgnore {
                    it.close()
                }
            }
            //重置播放进度
            pendingPosition = 0
            //切换转态
            playerState = STATE_IDLE
        }
    }

    /**
     * 播放时长
     */
    override val duration: Long
        get() = if (isInPlaybackState) player?.getDuration().orDefault() else 0

    /**
     * 播放位置
     */
    override val currentPosition: Long
        get() {
            if (isInPlaybackState) {
                pendingPosition = player?.getCurrentPosition().orDefault()
                return pendingPosition
            }
            return 0
        }

    /**
     * 缓冲进度
     */
    override val bufferedPercentage: Int
        get() = player?.getBufferedPercentage().orDefault()

    /**
     * 定位播放位置
     */
    override fun seekTo(position: Long) {
        pendingPosition = position
        if (isInPlaybackState) {
            player?.seekTo(position)
        }
    }

    /**
     * 是否处于播放状态
     */
    override val isPlaying: Boolean
        get() = isInPlaybackState && player?.isPlaying().orDefault()

    /**
     * 设置音量
     */
    fun setVolume(
        @FloatRange(from = 0.0, to = 1.0) leftVolume: Float,
        @FloatRange(from = 0.0, to = 1.0) rightVolume: Float
    ) {
        this.leftVolume = leftVolume
        this.rightVolume = rightVolume
        player?.setVolume(leftVolume, rightVolume)
    }

    /**
     * 重新播放
     *
     * @param resetPosition 是否从头开始播放
     */
    override fun replay(resetPosition: Boolean) {
        if (resetPosition) {
            pendingPosition = 0
        }
        startPrepare(true)
        playerContainer.attachPlayer(player!!)
    }

    /**
     * 播放速度
     */
    override var speed: Float
        get() = if (isInPlaybackState) player?.getSpeed().orDefault(1f) else 1f
        set(value) {
            if (isInPlaybackState) {
                player?.setSpeed(value)
            }
        }

    /**
     * 设置播放画面比例
     */
    override fun setScreenAspectRatioType(@AspectRatioType aspectRatioType: Int) {
        playerContainer.setScreenAspectRatioType(aspectRatioType)
    }

    /**
     * 截图
     */
    override fun screenshot(highQuality: Boolean, callback: ScreenShotCallback) {
        playerContainer.screenshot(highQuality, callback)
    }

    /**
     * 静音状态
     */
    override var isMute: Boolean = false
        set(value) {
            field = value
            player?.let { player ->
                val leftVolume = if (isMute) 0.0f else this.leftVolume
                val rightVolume = if (isMute) 0.0f else this.rightVolume
                player.setVolume(leftVolume, rightVolume)
            }
        }

    /**
     * 旋转视频画面
     *
     * @param degree 旋转角度
     */
    override fun setRotation(degree: Int) {
        playerContainer.setVideoRotation(degree)
    }

    /**
     * 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
     */
    override val videoSize: IntArray
        get() = playerContainer.videoSize

    /**
     * 获取缓冲速度
     */
    override val tcpSpeed: Long
        get() = player?.getTcpSpeed().orDefault()

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    override fun setMirrorRotation(enable: Boolean) {
        playerContainer.setVideoMirrorRotation(enable)
    }

    /**
     * 判断是否处于全屏状态（视图处于全屏）
     */
    override val isFullScreen: Boolean
        get() = screenMode == SCREEN_MODE_FULL

    /**
     * 当前是否处于小屏状态（视图处于小屏）
     */
    override val isTinyScreen: Boolean
        get() = screenMode == SCREEN_MODE_TINY

    /**
     * 横竖屏切换
     *
     * @return
     */
    override fun toggleFullScreen(): Boolean {
        return if (isFullScreen) {
            stopFullScreen()
        } else {
            startFullScreen()
        }
    }

    /**
     * 开始全屏
     */
    override fun startFullScreen(isLandscapeReversed: Boolean): Boolean {
        //设置界面横屏
        preferredActivity?.let { activity ->
            if (isLandscapeReversed) {
                if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    activity.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
            } else {
                if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
        return startVideoViewFullScreen()
    }

    /**
     * 停止全屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun stopFullScreen(): Boolean {
        preferredActivity?.let { activity ->
            if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        return stopVideoViewFullScreen()
    }

    /**
     * VideoView全屏
     */
    override fun startVideoViewFullScreen(): Boolean {
        if (isFullScreen) return false
        if (screenModeHandler.startFullScreen(activityContext, playerContainer)) {
            screenMode = SCREEN_MODE_FULL
            return true
        }
        return false
    }

    /**
     * VideoView退出全屏
     */
    override fun stopVideoViewFullScreen(): Boolean {
        if (!isFullScreen) return false
        if (screenModeHandler.stopFullScreen(activityContext, this, playerContainer)) {
            screenMode = SCREEN_MODE_NORMAL
            return true
        }
        return false
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && isFullScreen) {
            //重新获得焦点时保持全屏状态
            ScreenModeHandler.hideSystemBar(activityContext)
        }
    }

    /**
     * 开启小屏
     */
    override fun startTinyScreen() {
        if (isTinyScreen) return
        if (screenModeHandler.startTinyScreen(activityContext, playerContainer)) {
            screenMode = SCREEN_MODE_TINY
        }
    }

    /**
     * 退出小屏
     */
    override fun stopTinyScreen() {
        if (!isTinyScreen) return
        if (screenModeHandler.stopTinyScreen(this, playerContainer)) {
            screenMode = SCREEN_MODE_NORMAL
        }
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    override fun onPrepared() {
        playerState = STATE_PREPARED
        // 跳转到指定位置
        if (pendingPosition > 0) {
            player?.seekTo(pendingPosition)
        }
    }

    /**
     * 播放信息回调，播放中的缓冲开始与结束，开始渲染视频第一帧，视频旋转信息
     */
    override fun onInfo(what: Int, extra: Int) {
        when (what) {
            IPlayer.MEDIA_INFO_BUFFERING_START -> playerState = STATE_BUFFERING
            IPlayer.MEDIA_INFO_BUFFERING_END -> playerState = STATE_BUFFERED
            IPlayer.MEDIA_INFO_RENDERING_START -> {
                playerState = STATE_PLAYING
                playerContainer.keepScreenOn = true
            }
            IPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> setRotation(extra)
        }
    }

    /**
     * 视频播放出错回调
     */
    override fun onError(e: Throwable) {
        playerContainer.keepScreenOn = false
        playerState = STATE_ERROR
    }

    /**
     * 视频播放完成回调
     */
    override fun onCompletion() {
        playerContainer.keepScreenOn = false
        pendingPosition = 0
        playerState = STATE_PLAYBACK_COMPLETED
    }

    /**
     * 视频宽高回调
     */
    override fun onVideoSizeChanged(width: Int, height: Int) {
        playerContainer.onVideoSizeChanged(width, height)
    }

    /**
     * 通知播放器状态发生变化
     */
    private fun notifyPlayerStateChanged() {
        videoController?.setPlayerState(playerState)
        stateChangedListeners.forEach {
            it.onPlayerStateChanged(playerState)
        }
    }

    /**
     * 通知当前界面模式发生了变化
     */
    @CallSuper
    protected fun notifyScreenModeChanged() {
        //todo 既然通过通知对外发布了screenmode的改变，是否就不应该再主动
        videoController?.setScreenMode(screenMode)
        stateChangedListeners.forEach {
            it.onScreenModeChanged(screenMode)
        }
    }

    /**
     * 播放状态改变监听器
     * todo 目前VideoView对外可访问的回调过少，[IPlayer.EventListener]的回调太多对外不可见
     */
    interface OnStateChangeListener {

        fun onScreenModeChanged(@ScreenMode screenMode: Int) {}

        /**
         * 播放器播放状态发生了变化
         *
         * @param playState
         * todo 增加一个参数
         */
        fun onPlayerStateChanged(@PlayerState playState: Int) {}
    }

    /**
     * 添加一个播放状态监听器，播放状态发生变化时将会调用。
     */
    fun addOnStateChangeListener(listener: OnStateChangeListener) {
        stateChangedListeners.add(listener)
    }

    /**
     * 移除某个播放状态监听
     */
    fun removeOnStateChangeListener(listener: OnStateChangeListener) {
        stateChangedListeners.remove(listener)
    }

    /**
     * 移除所有播放状态监听
     */
    fun clearOnStateChangeListeners() {
        stateChangedListeners.clear()
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    fun onBackPressed(): Boolean {
        return playerContainer.onBackPressed()
    }

    companion object {
        /**
         * 播放出错
         */
        const val STATE_ERROR = -1

        /**
         * 闲置中
         */
        const val STATE_IDLE = 0

        /**
         * 准备中：处于已设置了播放数据源，但是播放器还未回调[IPlayer.EventListener.onPrepared]
         */
        const val STATE_PREPARING = 1

        /**
         * 已就绪
         */
        const val STATE_PREPARED = 2

        /**
         * 播放中
         */
        const val STATE_PLAYING = 3

        /**
         * 暂停中
         */
        const val STATE_PAUSED = 4

        /**
         * 播放结束
         */
        const val STATE_PLAYBACK_COMPLETED = 5

        /**
         * 缓冲中
         */
        const val STATE_BUFFERING = 6

        /**
         * 缓冲结束
         */
        const val STATE_BUFFERED = 7

        /**
         * 播放过程中停止继续播放：比如手机不允许在手机流量的时候进行播放（此时播放器处于已就绪未播放中状态）
         */
        const val STATE_START_ABORT = 8

        /**
         * 屏幕比例类型
         */
        const val SCREEN_ASPECT_RATIO_DEFAULT = AspectRatioType.DEFAULT_SCALE
        const val SCREEN_ASPECT_RATIO_SCALE_18_9 = AspectRatioType.SCALE_18_9
        const val SCREEN_ASPECT_RATIO_SCALE_16_9 = AspectRatioType.SCALE_16_9
        const val SCREEN_ASPECT_RATIO_SCALE_4_3 = AspectRatioType.SCALE_4_3
        const val SCREEN_ASPECT_RATIO_MATCH_PARENT = AspectRatioType.MATCH_PARENT
        const val SCREEN_ASPECT_RATIO_SCALE_ORIGINAL = AspectRatioType.SCALE_ORIGINAL
        const val SCREEN_ASPECT_RATIO_CENTER_CROP = AspectRatioType.CENTER_CROP

        /**
         * 普通模式
         */
        const val SCREEN_MODE_NORMAL = 10

        /**
         * 全屏模式
         */
        const val SCREEN_MODE_FULL = 11

        /**
         * 小窗模式
         */
        const val SCREEN_MODE_TINY = 12
    }

    init {

        //读取xml中的配置，并综合全局配置
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VideoView)
        looping = ta.getBoolean(R.styleable.VideoView_looping, false)
        val screenAspectRatioType =
            ta.getInt(R.styleable.VideoView_screenScaleType, GlobalConfig.screenAspectRatioType)
        val playerBackgroundColor =
            ta.getColor(R.styleable.VideoView_playerBackgroundColor, Color.BLACK)
        ta.recycle()

        //准备播放器容器
        playerContainer = VideoViewContainer(context)
        setPlayerBackgroundColor(playerBackgroundColor)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(playerContainer, params)
        playerContainer.setScreenAspectRatioType(screenAspectRatioType)
    }
}