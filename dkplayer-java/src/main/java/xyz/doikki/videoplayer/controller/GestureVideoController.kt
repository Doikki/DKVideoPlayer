package xyz.doikki.videoplayer.controller

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.AttrRes
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.INVALIDATE_SEEK_POSITION
import xyz.doikki.videoplayer.controller.component.GestureControlComponent
import xyz.doikki.videoplayer.getActivityContext
import xyz.doikki.videoplayer.util.PlayerUtils
import kotlin.math.abs

/**
 * 包含手势操作的VideoController
 * Created by Doikki on 2018/1/6.
 */
abstract class GestureVideoController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : MediaController(context, attrs, defStyleAttr),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener,
    OnTouchListener {

    private val mGestureDetector: GestureDetector
    private val mAudioManager: AudioManager

    //竖屏模式是否启用手势操作
    private var mGestureInPortraitEnabled = false

    //是否启用手势操作
    private var mGestureEnabled = true

    //是否启用双击切换播放/暂停
    private var mDoubleTapTogglePlayEnabled = true

    /**
     * 设置是否可以滑动调节进度，默认可以
     */
    var seekEnabled = true

    //是否可以滑动：滑动调节音量或者亮度
    private var mCanSlide = false

    //待处理的seek position：通常由于手势滑动或者按键引起的位置变动
    protected var pendingSeekPosition: Int = INVALIDATE_SEEK_POSITION

    private var mStreamVolume = 0
    private var mBrightness = 0f

    private var mFirstTouch = false
    private var mChangePosition = false
    private var mChangeBrightness = false
    private var mChangeVolume = false

    init {
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mGestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    /**
     * 是否在竖屏模式下开始手势控制，默认关闭
     */
    fun setGestureInPortraitEnabled(enableInNormal: Boolean) {
        mGestureInPortraitEnabled = enableInNormal
    }

    /**
     * 是否开启手势控制，默认开启，关闭之后，手势调节进度，音量，亮度功能将关闭
     */
    fun setGestureEnabled(gestureEnabled: Boolean) {
        mGestureEnabled = gestureEnabled
    }

    /**
     * 是否开启双击播放/暂停，默认开启
     */
    fun setDoubleTapTogglePlayEnabled(enabled: Boolean) {
        mDoubleTapTogglePlayEnabled = enabled
    }

    override fun setScreenMode(@DKVideoView.ScreenMode playerState: Int) {
        super.setScreenMode(playerState)
        if (playerState == DKVideoView.SCREEN_MODE_NORMAL) {
            mCanSlide = mGestureInPortraitEnabled
        } else if (playerState == DKVideoView.SCREEN_MODE_FULL) {
            mCanSlide = true
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    /**
     * 手指按下的瞬间
     */
    override fun onDown(e: MotionEvent): Boolean {
        if (!isInPlaybackState //不处于播放状态
            || !mGestureEnabled //关闭了手势
            || PlayerUtils.isEdge(context, e)
        ) //处于屏幕边沿
            return true
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val activity = context.getActivityContext()
        mBrightness = activity?.window?.attributes?.screenBrightness ?: 0f
        mFirstTouch = true
        mChangePosition = false
        mChangeBrightness = false
        mChangeVolume = false
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (isInPlaybackState) {
            toggleShowState()
        }
        return true
    }

    /**
     * 双击
     */
    override fun onDoubleTap(e: MotionEvent): Boolean {
        if (mDoubleTapTogglePlayEnabled && !isLocked && isInPlaybackState) togglePlay()
        return true
    }

    /**
     * 在屏幕上滑动
     */
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (!isInPlaybackState //不处于播放状态
            || !mGestureEnabled //关闭了手势
            || !mCanSlide //关闭了滑动手势
            || isLocked //锁住了屏幕
            || PlayerUtils.isEdge(context, e1)
        ) //处于屏幕边沿
            return true
        val deltaX = e1.x - e2.x
        val deltaY = e1.y - e2.y
        if (mFirstTouch) {
            mChangePosition = abs(distanceX) >= abs(distanceY)
            if (!mChangePosition) {
                //半屏宽度
                val halfScreen = PlayerUtils.getScreenWidth(context, true) / 2
                if (e2.x > halfScreen) {
                    mChangeVolume = true
                } else {
                    mChangeBrightness = true
                }
            }
            if (mChangePosition) {
                //根据用户设置是否可以滑动调节进度来决定最终是否可以滑动调节进度
                mChangePosition = seekEnabled
            }
            if (mChangePosition || mChangeBrightness || mChangeVolume) {
                for ((component) in mControlComponents) {
                    if (component is GestureControlComponent) {
                        component.onStartSlide()
                    }
                }
            }
            mFirstTouch = false
        }
        if (mChangePosition) {
            slideToChangePosition(deltaX)
        } else if (mChangeBrightness) {
            slideToChangeBrightness(deltaY)
        } else if (mChangeVolume) {
            slideToChangeVolume(deltaY)
        }
        return true
    }

    /**
     * 滑动切换播放位置
     */
    protected fun slideToChangePosition(deltaX: Float) {
        invokeOnPlayerAttached {
            val width = measuredWidth
            val duration = it.duration.toInt()
            val currentPosition = it.currentPosition.toInt()
            var position = (-deltaX / width * 120000 + currentPosition).toInt()
            if (position > duration) position = duration
            if (position < 0) position = 0
            setPendingSeekPositionAndNotify(position, currentPosition, duration)
        }
    }


    protected fun slideToChangeBrightness(deltaY: Float) {
        val activity = PlayerUtils.scanForActivity(context) ?: return
        val window = activity.window
        val attributes = window.attributes
        val height = measuredHeight
        if (mBrightness == -1.0f) mBrightness = 0.5f
        var brightness = deltaY * 2 / height + mBrightness
        if (brightness < 0) {
            brightness = 0f
        }
        if (brightness > 1.0f) brightness = 1.0f
        val percent = (brightness * 100).toInt()
        attributes.screenBrightness = brightness
        window.attributes = attributes
        for ((component) in mControlComponents) {
            if (component is GestureControlComponent) {
                component.onBrightnessChange(percent)
            }
        }
    }

    protected fun slideToChangeVolume(deltaY: Float) {
        val streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val height = measuredHeight
        val deltaV = deltaY * 2 / height * streamMaxVolume
        var index = mStreamVolume + deltaV
        if (index > streamMaxVolume) index = streamMaxVolume.toFloat()
        if (index < 0) index = 0f
        val percent = (index / streamMaxVolume * 100).toInt()
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index.toInt(), 0)
        for ((component) in mControlComponents) {
            if (component is GestureControlComponent) {
                component.onVolumeChange(percent)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //滑动结束时事件处理
        if (!mGestureDetector.onTouchEvent(event)) {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    stopSlide()
                    handlePendingSeekPosition()
                }
                MotionEvent.ACTION_CANCEL -> {
                    stopSlide()
                    cancelPendingSeekPosition()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    protected fun setPendingSeekPositionAndNotify(
        position: Int,
        currentPosition: Int,
        duration: Int
    ) {
        for ((component) in mControlComponents) {
            if (component is GestureControlComponent) {
                component.onPositionChange(position, currentPosition, duration)
            }
        }
        pendingSeekPosition = position
    }

    protected open fun handlePendingSeekPosition() {
        invokeOnPlayerAttached { player ->
            if (pendingSeekPosition >= 0) {
                player.seekTo(pendingSeekPosition.toLong())
            }
        }
        pendingSeekPosition = INVALIDATE_SEEK_POSITION
    }

    protected open fun cancelPendingSeekPosition() {
        pendingSeekPosition = INVALIDATE_SEEK_POSITION
    }

    private fun stopSlide() {
        for ((component) in mControlComponents) {
            if (component is GestureControlComponent) {
                component.onStopSlide()
            }
        }
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    override fun onShowPress(e: MotionEvent) {}
    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }
}