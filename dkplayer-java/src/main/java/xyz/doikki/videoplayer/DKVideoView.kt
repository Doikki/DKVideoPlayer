package xyz.doikki.videoplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.net.Uri
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import xyz.doikki.videoplayer.controller.MediaController
import xyz.doikki.videoplayer.controller.VideoViewControl
import xyz.doikki.videoplayer.player.AudioFocusHelper
import xyz.doikki.videoplayer.player.ScreenModeHandler
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.Render
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.RenderFactory
import xyz.doikki.videoplayer.render.ScreenMode
import xyz.doikki.videoplayer.util.L
import xyz.doikki.videoplayer.util.PlayerUtils
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 播放器&播放视图  内部包含了对应的[DKPlayer] 和  [Render]，因此由本类提供这两者的功能能力
 *  本类的数据目前是在内部提供了一个容器，让容器去添加Render和Controller，这样便于界面切换
 *
 * Created by Doikki on 2017/4/7.
 *
 *
 * update by luochao on 2022/9/16
 *
 * @see .setScreenAspectRatioType
 * @see .screenshot 截屏
 *
 * @see .setMute
 * @see .isMute
 */
open class DKVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), VideoViewControl, DKPlayer.EventListener {

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
     * 控制器
     */
    @JvmField
    protected var mVideoController: MediaController? = null

    /**
     * 真正承载播放器视图的容器
     */
    @JvmField
    protected val playerContainer: FrameLayout

    /**
     * 播放器内核
     */
    @JvmField
    protected var mPlayer: DKPlayer? = null

    /**
     * 自定义播放器构建工厂
     */
    private var mPlayerFactory: DKPlayerFactory<out DKPlayer>? = null

    /**
     * 当前播放器的状态
     */
    @PlayerState
    var currentPlayState = STATE_IDLE
        private set

    /**
     * 渲染视图
     */
    private var mRender: Render? = null

    /**
     * 自定义Render工厂
     */
    protected var mRenderFactory: RenderFactory? = null

    /**
     * 渲染视图纵横比
     */
    @AspectRatioType
    protected var mScreenAspectRatioType = SCREEN_ASPECT_RATIO_DEFAULT

    /**
     * 当前屏幕模式：普通、全屏、小窗口
     */
    @ScreenMode
    protected var mScreenMode = ScreenMode.NORMAL

    /**
     * 屏幕模式切换帮助类
     */
    private var mScreenModeHandler: ScreenModeHandler

    /**
     * 是否静音
     */
    private var mMute = false

    /**
     * 左声道音量
     */
    private var mLeftVolume = 1.0f

    /**
     * 右声道音量
     */
    private var mRightVolume = 1.0f

    /**
     * 是否循环播放
     */
    private var mLooping = false

    /**
     * 视频画面大小
     */
    protected val mVideoSize = intArrayOf(0, 0)

    /**
     * 进度管理器，设置之后播放器会记录播放进度，以便下次播放恢复进度
     */
    protected var mProgressManager: ProgressManager?

    /**
     * 监听系统中音频焦点改变，见[.setEnableAudioFocus]
     */
    protected var mEnableAudioFocus: Boolean

    /**
     * 音频焦点管理帮助类
     */
    protected var mAudioFocusHelper: AudioFocusHelper? = null

    /**
     * OnStateChangeListener集合，保存了所有开发者设置的监听器
     */
    private val mPlayerStateChangedListeners = CopyOnWriteArrayList<OnStateChangeListener>()

    //--------- data sources ---------//
    /**
     * 当前播放视频的地址
     */
    protected var mUrl: String? = null

    /**
     * 当前视频地址的请求头
     */
    protected var mHeaders: Map<String, String>? = null

    protected var mAssetFileDescriptor: AssetFileDescriptor? = null

    /**
     * 当前正在播放视频的位置
     */
    protected var mCurrentPosition: Long = 0

    /**
     * [.mPlayerContainer]背景色，默认黑色
     */
    private val mPlayerBackgroundColor: Int

    /**
     * 判断是否为本地数据源，包括 本地文件、Asset、raw
     */
    private val isLocalDataSource: Boolean
        get() {
            if (mAssetFileDescriptor != null) {
                return true
            }
            if (!mUrl.isNullOrEmpty()) {
                val uri = Uri.parse(mUrl)
                return ContentResolver.SCHEME_ANDROID_RESOURCE == uri.scheme || ContentResolver.SCHEME_FILE == uri.scheme || "rawresource" == uri.scheme
            }
            return false
        }

    /**
     * 是否显示移动网络提示，可在Controller中配置
     * 非本地数据源并且控制器需要显示网络提示
     */
    private val showNetworkWarning: Boolean
        get() = !isLocalDataSource && mVideoController?.showNetWarning().orDefault()

    /**
     * 获取播放器名字
     * @return
     */
    val playerName: String
        get() {
            val className = mPlayerFactory.orDefault(DKManager.playerFactory).javaClass.name
            return className.substring(className.lastIndexOf("."))
        }

    /**
     * 获取渲染视图的名字
     * @return
     */
    val renderName: String
        get() {
            val className = mRenderFactory.orDefault(DKManager.renderFactory).javaClass.name
            return className.substring(className.lastIndexOf("."))
        }

    /**
     * 第一次播放
     *
     * @return 是否成功开始播放
     */
    protected fun startPlay(): Boolean {
        //如果要显示移动网络提示则不继续播放
        if (showNetworkWarning) {
            //中止播放
            setPlayState(STATE_START_ABORT)
            return false
        }
        //todo 此处存在问题就是如果在中途修改了mEnableAudioFocus为false，并且之前已初始化了mAudioFocusHelper，则会导致问题，不过该问题并不太可能出现
        //监听音频焦点改变
        if (mEnableAudioFocus) {
            mAudioFocusHelper = AudioFocusHelper(this)
        }
        mCurrentPosition = getSavedPlayedProgress()
        setupMediaPlayer()
        setupRenderView()
        startPrepare(false)
        return true
    }

    /**
     * 创建播放器
     */
    protected open fun createMediaPlayer(): DKPlayer {
        return DKManager.createMediaPlayer(context, mPlayerFactory)
    }

    /**
     * 初始化播放器
     */
    protected open fun setupMediaPlayer() {
        mPlayer = createMediaPlayer().also {
            it.setEventListener(this)
            it.init()
            onMediaPlayerCreate(mPlayer)
        }
        setupMediaPlayerOptions()
    }

    /**
     * 初始化之前的配置项
     */
    protected open fun onMediaPlayerCreate(mediaPlayer: DKPlayer?) {}

    /**
     * 初始化之后的配置项
     */
    protected open fun setupMediaPlayerOptions() {
        setLooping(mLooping)
        isMute = mMute
    }

    private fun createAndPrepareRenderView(): Render {
        return DKManager.createRender(context, mRenderFactory).also {
            it.attachPlayer(mPlayer!!)
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            playerContainer.addView(it.view, 0, params)
        }
    }

    /**
     * 初始化视频渲染View
     */
    private fun setupRenderView() {
        mRender?.let {
            playerContainer.removeView(it.view)
            it.release()
        }
        mRender = DKManager.createRender(context, mRenderFactory).also {
            it.attachPlayer(mPlayer!!)
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            playerContainer.addView(it.view, 0, params)
        }
    }

    /**
     * 开始准备播放（直接播放）
     */
    protected open fun startPrepare(reset: Boolean) {
        if (reset) {
            mPlayer?.reset()
            //重新设置option，media player reset之后，option会失效
            setupMediaPlayerOptions()
        }
        if (prepareDataSource()) {
            mPlayer!!.prepareAsync()
            setPlayState(STATE_PREPARING)
            screenMode =
                if (isFullScreen) ScreenMode.FULL else if (isTinyScreen) ScreenMode.TINY else ScreenMode.NORMAL
        }
    }

    /**
     * 设置播放数据
     * @return 播放数据是否设置成功
     */
    protected open fun prepareDataSource(): Boolean {
        if (mAssetFileDescriptor != null) {
            mPlayer!!.setDataSource(mAssetFileDescriptor!!)
            return true
        } else if (!mUrl.isNullOrEmpty()) {
            mPlayer!!.setDataSource(mUrl!!, mHeaders)
            return true
        }
        return false
    }

    /**
     * 播放状态下开始播放
     */
    protected open fun startInPlaybackState() {
        mPlayer!!.start()
        setPlayState(STATE_PLAYING)
        if (mAudioFocusHelper != null && !isMute) {
            mAudioFocusHelper!!.requestFocus()
        }
        playerContainer!!.keepScreenOn = true
    }

    /**
     * 继续播放
     */
    open fun resume() {
        if (isInPlaybackState
            && !mPlayer!!.isPlaying()
        ) {
            mPlayer!!.start()
            setPlayState(STATE_PLAYING)
            if (mAudioFocusHelper != null && !isMute) {
                mAudioFocusHelper!!.requestFocus()
            }
            playerContainer!!.keepScreenOn = true
        }
    }

    /**
     * 释放播放器
     */
    open fun release() {
        if (!isInIdleState) {
            //释放播放器
            if (mPlayer != null) {
                mPlayer!!.release()
                mPlayer = null
            }
            //释放renderView
            if (mRender != null) {
                playerContainer!!.removeView(mRender!!.view)
                mRender!!.release()
                mRender = null
            }
            //释放Assets资源
            if (mAssetFileDescriptor != null) {
                try {
                    mAssetFileDescriptor!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            //关闭AudioFocus监听
            if (mAudioFocusHelper != null) {
                mAudioFocusHelper!!.abandonFocus()
                mAudioFocusHelper = null
            }
            //关闭屏幕常亮
            keepScreenOn(false)
            //保存播放进度
            saveCurrentPlayedProgress()
            //重置播放进度
            mCurrentPosition = 0
            //切换转态
            setPlayState(STATE_IDLE)
        }
    }

    /**
     * 是否处于播放状态
     */
    protected val isInPlaybackState: Boolean
        protected get() = mPlayer != null && currentPlayState != STATE_ERROR && currentPlayState != STATE_IDLE && currentPlayState != STATE_PREPARING && currentPlayState != STATE_START_ABORT && currentPlayState != STATE_PLAYBACK_COMPLETED

    /**
     * 是否处于未播放状态
     */
    protected val isInIdleState: Boolean
        protected get() = currentPlayState == STATE_IDLE

    /**
     * 是否处于可播放但终止播放状态
     */
    private val isInStartingAbortState: Boolean
        private get() = currentPlayState == STATE_START_ABORT

    /**
     * 重新播放
     *
     * @param resetPosition 是否从头开始播放
     */
    override fun replay(resetPosition: Boolean) {
        if (resetPosition) {
            mCurrentPosition = 0
        }
        setupRenderView()
        startPrepare(true)
    }

    /*************START 代理MediaPlayer的方法 */
    fun setDataSource(path: String) {
        setDataSource(path, null)
    }

    open fun setDataSource(path: String, headers: Map<String, String>?) {
        mAssetFileDescriptor = null
        mUrl = path
        mHeaders = headers
    }

    open fun setDataSource(fd: AssetFileDescriptor) {
        mUrl = null
        mAssetFileDescriptor = fd
    }

    /**
     * 开始播放，注意：调用此方法后必须调用[.release]释放播放器，否则会导致内存泄漏
     */
    override fun start() {
        if (isInIdleState
            || isInStartingAbortState
        ) {
            startPlay()
        } else if (isInPlaybackState) {
            startInPlaybackState()
        }
    }

    override fun pause() {
        if (isInPlaybackState && mPlayer!!.isPlaying()) {
            mPlayer!!.pause()
            setPlayState(STATE_PAUSED)
            if (mAudioFocusHelper != null && !isMute) {
                mAudioFocusHelper!!.abandonFocus()
            }
            keepScreenOn(false)
        }
    }

    override fun getDuration(): Long {
        return if (isInPlaybackState) {
            mPlayer!!.getDuration()
        } else 0
    }

    override fun getCurrentPosition(): Long {
        if (isInPlaybackState) {
            mCurrentPosition = mPlayer!!.getCurrentPosition()
            return mCurrentPosition
        }
        return 0
    }

    override fun getBufferedPercentage(): Int {
        return if (mPlayer != null) mPlayer!!.getBufferedPercentage() else 0
    }

    override fun seekTo(pos: Long) {
        if (isInPlaybackState) {
            mPlayer!!.seekTo(pos)
        } else {
            L.w("当前播放器未处于播放中，忽略seek")
        }
    }

    override fun isPlaying(): Boolean {
        return isInPlaybackState && mPlayer!!.isPlaying()
    }

    fun setVolume(
        @FloatRange(from = 0.0, to = 1.0) leftVolume: Float,
        @FloatRange(from = 0.0, to = 1.0) rightVolume: Float
    ) {
        mLeftVolume = leftVolume
        mRightVolume = rightVolume
        if (mPlayer != null) {
            mPlayer!!.setVolume(leftVolume, rightVolume)
        }
    }
    /*************END AVPlayerFunction  */
    /*************START VideoViewControl  */
    /**
     * 判断是否处于全屏状态（视图处于全屏）
     */
    override fun isFullScreen(): Boolean {
        return mScreenMode == SCREEN_MODE_FULL
    }

    /**
     * 当前是否处于小屏状态（视图处于小屏）
     */
    override fun isTinyScreen(): Boolean {
        return mScreenMode == SCREEN_MODE_TINY
    }

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
        val activity = preferredActivity
        if (isLandscapeReversed) {
            if (activity != null && activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
        } else {
            if (activity != null && activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
        return startVideoViewFullScreen()
    }

    /**
     * 停止全屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun stopFullScreen(): Boolean {
        val activity = preferredActivity
        if (activity != null && activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        return stopVideoViewFullScreen()
    }

    /**
     * VideoView全屏
     */
    override fun startVideoViewFullScreen(): Boolean {
        if (isFullScreen) return false
        if (mScreenModeHandler!!.startFullScreen(preferredActivity!!, playerContainer)) {
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
        if (mScreenModeHandler!!.stopFullScreen(preferredActivity, playerContainer)) {
            screenMode = SCREEN_MODE_NORMAL
            return true
        }
        return false
    }

    /**
     * 开启小屏
     */
    override fun startTinyScreen() {
        if (isTinyScreen) return
        if (mScreenModeHandler!!.startTinyScreen(preferredActivity, playerContainer)) {
            screenMode = SCREEN_MODE_TINY
        }
    }

    /**
     * 退出小屏
     */
    override fun stopTinyScreen() {
        if (!isTinyScreen) return
        if (mScreenModeHandler!!.stopTinyScreen(playerContainer)) {
            screenMode = SCREEN_MODE_NORMAL
        }
    }
    /**
     * 获取当前播放器屏幕显示模式
     */
    /**
     * 设置当前界面模式
     */
    var screenMode: Int
        get() = mScreenMode
        private set(screenMode) {
            mScreenMode = screenMode
            notifyScreenModeChanged(screenMode)
        }

    /**
     * 通知当前界面模式发生了变化
     */
    @CallSuper
    protected fun notifyScreenModeChanged(@ScreenMode screenMode: Int) {
        if (mVideoController != null) {
            mVideoController!!.setScreenMode(screenMode)
        }
        val listeners: List<OnStateChangeListener> = mPlayerStateChangedListeners
        if (listeners.isEmpty()) return
        for (l in listeners) {
            l.onScreenModeChanged(screenMode)
        }
    }

    override fun setScreenAspectRatioType(@AspectRatioType aspectRatioType: Int) {
        mScreenAspectRatioType = aspectRatioType
        if (mRender != null) {
            mRender!!.setAspectRatioType(aspectRatioType)
        }
    }
    /*************START VideoViewControl  */

    /*************START VideoController  */
    override fun screenshot(highQuality: Boolean, callback: ScreenShotCallback) {
        if (mRender != null) {
            mRender!!.screenshot(highQuality, callback)
            return
        }
        callback.onScreenShotResult(null)
    }

    /**
     * 设置静音
     *
     * @param isMute true:静音 false：相反
     */
    override fun setMute(isMute: Boolean) {
        mMute = isMute
        if (mPlayer != null) {
            val leftVolume = if (isMute) 0.0f else mLeftVolume
            val rightVolume = if (isMute) 0.0f else mRightVolume
            mPlayer!!.setVolume(leftVolume, rightVolume)
        }
    }

    /**
     * 是否处于静音状态
     */
    override fun isMute(): Boolean {
        return mMute
    }

    /**
     * 旋转视频画面
     *
     * @param degree 旋转角度
     */
    override fun setRotation(degree: Int) {
        if (mRender != null) {
            mRender!!.setVideoRotation(degree)
        }
    }
    /*************START VideoController  */
    /*************START AVPlayer#EventListener 实现逻辑 */
    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    override fun onPrepared() {
        setPlayState(STATE_PREPARED)
        if (!isMute && mAudioFocusHelper != null) {
            mAudioFocusHelper!!.requestFocus()
        }
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition)
        }
    }

    /**
     * 播放信息回调，播放中的缓冲开始与结束，开始渲染视频第一帧，视频旋转信息
     */
    override fun onInfo(what: Int, extra: Int) {
        when (what) {
            DKPlayer.MEDIA_INFO_BUFFERING_START -> setPlayState(STATE_BUFFERING)
            DKPlayer.MEDIA_INFO_BUFFERING_END -> setPlayState(STATE_BUFFERED)
            DKPlayer.MEDIA_INFO_RENDERING_START -> {
                setPlayState(STATE_PLAYING)
                keepScreenOn(true)
            }
            DKPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> if (mRender != null) mRender!!.setVideoRotation(
                extra
            )
        }
    }

    /**
     * 视频播放出错回调
     */
    override fun onError(e: Throwable) {
        keepScreenOn(false)
        setPlayState(STATE_ERROR)
    }

    /**
     * 视频播放完成回调
     */
    override fun onCompletion() {
        keepScreenOn(false)
        mCurrentPosition = 0
        //播放完成，清除进度
        savePlayedProgress(0)
        setPlayState(STATE_PLAYBACK_COMPLETED)
    }
    /*************END AVPlayer#EventListener 实现逻辑 */
    /**
     * 设置进度管理器，用于保存播放进度
     */
    fun setProgressManager(progressManager: ProgressManager?) {
        mProgressManager = progressManager
    }

    /**
     * 保持屏幕常亮
     *
     * @param isOn
     */
    private fun keepScreenOn(isOn: Boolean) {
        playerContainer!!.keepScreenOn = isOn
    }

    /**
     * 向Controller设置播放状态，用于控制Controller的ui展示
     */
    protected fun setPlayState(@PlayerState playState: Int) {
        currentPlayState = playState
        notifyPlayerStateChanged()
    }

    /**
     * 通知播放器状态发生变化
     */
    private fun notifyPlayerStateChanged() {
        val playState = currentPlayState
        if (mVideoController != null) {
            mVideoController!!.setPlayerState(playState)
        }
        val listeners: List<OnStateChangeListener> = mPlayerStateChangedListeners
        if (listeners.isEmpty()) return
        for (listener in mPlayerStateChangedListeners) {
            listener.onPlayerStateChanged(playState)
        }
    }

    /**
     * 获取缓冲速度
     */
    override fun getTcpSpeed(): Long {
        return if (mPlayer != null) mPlayer!!.getTcpSpeed() else 0
    }

    /**
     * 设置播放速度
     */
    override fun setSpeed(speed: Float) {
        if (isInPlaybackState) {
            mPlayer!!.setSpeed(speed)
        }
    }

    override fun getSpeed(): Float {
        return if (isInPlaybackState) {
            mPlayer!!.getSpeed()
        } else 1f
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    fun skipPositionWhenPlay(position: Int) {
        mCurrentPosition = position.toLong()
    }

    /**
     * 循环播放， 默认不循环播放
     */
    fun setLooping(looping: Boolean) {
        mLooping = looping
        mPlayer?.setLooping(looping)
    }

    /**
     * 是否开启AudioFocus监听， 默认开启，用于监听其它地方是否获取音频焦点，如果有其它地方获取了
     * 音频焦点，此播放器将做出相应反应，具体实现见[AudioFocusHelper]
     */
    fun setEnableAudioFocus(enableAudioFocus: Boolean) {
        mEnableAudioFocus = enableAudioFocus
    }

    /**
     * 自定义播放核心，继承[DKPlayerFactory]实现自己的播放核心
     */
    fun setPlayerFactory(playerFactory: DKPlayerFactory<out DKPlayer>) {
        mPlayerFactory = playerFactory
    }

    /**
     * 自定义RenderView，继承[RenderFactory]实现自己的RenderView
     */
    fun setRenderViewFactory(renderViewFactory: RenderFactory) {
        mRenderFactory = renderViewFactory
    }

    /**
     * 设置[.mPlayerContainer]的背景色
     */
    fun setPlayerBackgroundColor(color: Int) {
        playerContainer!!.setBackgroundColor(color)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && isFullScreen) {
            //重新获得焦点时保持全屏状态
            ScreenModeHandler.hideSystemBar(preferredActivity)
        }
    }

    /**
     * 获取Activity，优先通过Controller去获取Activity
     */
    protected val preferredActivity: Activity?
        protected get() {
            var activity: Activity? = null
            if (mVideoController != null) {
                activity = PlayerUtils.scanForActivity(mVideoController!!.context)
            }
            if (activity == null) {
                activity = PlayerUtils.scanForActivity(context)
            }
            return activity
        }

    override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        mVideoSize[0] = videoWidth
        mVideoSize[1] = videoHeight
        if (mRender != null) {
            mRender!!.setAspectRatioType(mScreenAspectRatioType)
            mRender!!.setVideoSize(videoWidth, videoHeight)
        }
    }

    /**
     * 设置控制器，传null表示移除控制器
     */
    fun setVideoController(mediaController: MediaController?) {
        playerContainer!!.removeView(mVideoController)
        mVideoController = mediaController
        if (mediaController != null) {
            mediaController.setMediaPlayer(this)
            val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            playerContainer!!.addView(mVideoController, params)
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    override fun setMirrorRotation(enable: Boolean) {
        mRender?.setMirrorRotation(enable)
    }

    /**
     * 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
     */
    override fun getVideoSize(): IntArray {
        //是否适合直接返回该变量,存在被外层修改的可能？是否应该 return new int[]{mVideoSize[0], mVideoSize[1]}
        return mVideoSize
    }

    /**
     * 播放状态改变监听器
     * todo 目前VideoView对外可访问的回调过少，[DKPlayer.EventListener]的回调太多对外不可见
     */

    interface OnStateChangeListener {

        fun onScreenModeChanged(@ScreenMode screenMode: Int) {}

        /**
         * 播放器播放状态发生了变化
         *
         * @param playState
         */
        fun onPlayerStateChanged(@PlayerState playState: Int) {}
    }

    /**
     * 添加一个播放状态监听器，播放状态发生变化时将会调用。
     */
    fun addOnStateChangeListener(listener: OnStateChangeListener) {
        mPlayerStateChangedListeners.add(listener)
    }

    /**
     * 移除某个播放状态监听
     */
    fun removeOnStateChangeListener(listener: OnStateChangeListener) {
        mPlayerStateChangedListeners.remove(listener)
    }

    /**
     * 移除所有播放状态监听
     */
    fun clearOnStateChangeListeners() {
        mPlayerStateChangedListeners.clear()
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    fun onBackPressed(): Boolean {
        return mVideoController != null && mVideoController!!.onBackPressed()
    }

    override fun onSaveInstanceState(): Parcelable? {
        L.d("onSaveInstanceState: currentPosition=$mCurrentPosition")
        //activity切到后台后可能被系统回收，故在此处进行进度保存
        saveCurrentPlayedProgress()
        return super.onSaveInstanceState()
    }//读取播放进度

    /**
     * 获取已保存的当前播放进度
     *
     * @return
     */
    fun getSavedPlayedProgress(): Long {
        if (mUrl.isNullOrEmpty())
            return 0;
        return mProgressManager?.getSavedProgress(mUrl!!).orDefault()
    }

    fun savePlayedProgress(position: Long) {
        if (mUrl.isNullOrEmpty())
            return
        mProgressManager?.let {
            L.d("saveProgress: $position")
            it.saveProgress(mUrl!!, position)
        } ?: L.w("savePlayedProgress is ignored,ProgressManager is null.")
    }

    /**
     * 保存当前播放位置
     * 只会在已存在播放的情况下才会保存
     */
    private fun saveCurrentPlayedProgress() {
        val position = mCurrentPosition
        if (position <= 0) return
        savePlayedProgress(position)
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
         * 准备中：处于已设置了播放数据源，但是播放器还未回调[DKPlayer.EventListener.onPrepared]
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
        const val SCREEN_ASPECT_RATIO_DEFAULT = AspectRatioType.SCALE
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
        const val SCREEN_MODE_TINY = 22
    }

    init {
        mProgressManager = DKManager.progressManager
        //读取xml中的配置，并综合全局配置

        val ta = context.obtainStyledAttributes(attrs, R.styleable.DKVideoView)
        mEnableAudioFocus = ta.getBoolean(
            R.styleable.DKVideoView_enableAudioFocus,
            DKManager.isAudioFocusEnabled
        )
        mLooping = ta.getBoolean(R.styleable.DKVideoView_looping, false)
        mScreenAspectRatioType =
            ta.getInt(R.styleable.DKVideoView_screenScaleType, DKManager.screenAspectRatioType)
        mPlayerBackgroundColor =
            ta.getColor(R.styleable.DKVideoView_playerBackgroundColor, Color.BLACK)
        ta.recycle()

        //准备播放器容器
        playerContainer = FrameLayout(context).also {
            it.setBackgroundColor(mPlayerBackgroundColor)
        }
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(playerContainer, params)
        mScreenModeHandler = ScreenModeHandler(this)
    }
}