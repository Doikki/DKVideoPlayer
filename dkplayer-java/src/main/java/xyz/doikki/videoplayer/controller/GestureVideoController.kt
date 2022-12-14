package xyz.doikki.videoplayer.controller

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.AttrRes
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.util.INVALIDATE_SEEK_POSITION
import xyz.doikki.videoplayer.controller.component.GestureControlComponent
import xyz.doikki.videoplayer.util.getActivityContext
import xyz.doikki.videoplayer.util.PlayerUtils
import kotlin.math.abs

/**
 * 包含手势操作的VideoController
 * Created by Doikki on 2018/1/6.
 */
abstract class GestureVideoController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : VideoController(context, attrs, defStyleAttr),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener,
    OnTouchListener {

    private val gestureDetector by lazy {
        GestureDetector(context, this)
    }
    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 是否在竖屏模式下开始手势控制，默认关闭
     */
    var gestureInPortraitEnabled = false

    /**
     * 是否开启手势控制，默认开启，关闭之后，手势调节进度，音量，亮度功能将关闭
     */
    var gestureEnabled = true

    /**
     * 是否开启双击播放/暂停，默认开启
     */
    var doubleTapTogglePlayEnabled = true

    /**
     * 设置是否可以滑动调节进度，默认可以
     */
    var seekEnabled = true

    //是否可以滑动：滑动调节音量或者亮度
    private var canSlide = false

    //待处理的seek position：通常由于手势滑动或者按键引起的位置变动
    protected var pendingSeekPosition: Int = INVALIDATE_SEEK_POSITION

    private var streamVolume = 0
    private var brightness = 0f

    private var firstTouch = false
    private var changePosition = false
    private var changeBrightness = false
    private var changeVolume = false

    init {
        setOnTouchListener(this)
    }

    override fun setScreenMode(@VideoView.ScreenMode screenMode: Int) {
        super.setScreenMode(screenMode)
        if (screenMode == VideoView.SCREEN_MODE_NORMAL) {
            canSlide = gestureInPortraitEnabled
        } else if (screenMode == VideoView.SCREEN_MODE_FULL) {
            canSlide = true
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 手指按下的瞬间
     */
    override fun onDown(e: MotionEvent): Boolean {
        if (!isInPlaybackState //不处于播放状态
            || !gestureEnabled //关闭了手势
            || PlayerUtils.isEdge(context, e) //处于屏幕边沿
        )
            return true
        streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val activity = context.getActivityContext()
        brightness = activity?.window?.attributes?.screenBrightness ?: 0f
        firstTouch = true
        changePosition = false
        changeBrightness = false
        changeVolume = false
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
        if (doubleTapTogglePlayEnabled && !isLocked && isInPlaybackState) togglePlay()
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
            || !gestureEnabled //关闭了手势
            || !canSlide //关闭了滑动手势
            || isLocked //锁住了屏幕
            || PlayerUtils.isEdge(context, e1) //处于屏幕边沿
        )
            return true
        val deltaX = e1.x - e2.x
        val deltaY = e1.y - e2.y
        if (firstTouch) {
            changePosition = abs(distanceX) >= abs(distanceY)
            if (!changePosition) {
                //半屏宽度
                val halfScreen = PlayerUtils.getScreenWidth(context, true) / 2
                if (e2.x > halfScreen) {
                    changeVolume = true
                } else {
                    changeBrightness = true
                }
            }
            if (changePosition) {
                //根据用户设置是否可以滑动调节进度来决定最终是否可以滑动调节进度
                changePosition = seekEnabled
            }
            if (changePosition || changeBrightness || changeVolume) {
                for ((component) in controlComponents) {
                    if (component is GestureControlComponent) {
                        component.onStartSlide()
                    }
                }
            }
            firstTouch = false
        }
        if (changePosition) {
            slideToChangePosition(deltaX)
        } else if (changeBrightness) {
            slideToChangeBrightness(deltaY)
        } else if (changeVolume) {
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
        if (brightness == -1.0f) brightness = 0.5f
        var brightness = deltaY * 2 / height + brightness
        if (brightness < 0) {
            brightness = 0f
        }
        if (brightness > 1.0f) brightness = 1.0f
        val percent = (brightness * 100).toInt()
        attributes.screenBrightness = brightness
        window.attributes = attributes
        for ((component) in controlComponents) {
            if (component is GestureControlComponent) {
                component.onBrightnessChange(percent)
            }
        }
    }

    protected fun slideToChangeVolume(deltaY: Float) {
        val streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val height = measuredHeight
        val deltaV = deltaY * 2 / height * streamMaxVolume
        var index = streamVolume + deltaV
        if (index > streamMaxVolume) index = streamMaxVolume.toFloat()
        if (index < 0) index = 0f
        val percent = (index / streamMaxVolume * 100).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index.toInt(), 0)
        for ((component) in controlComponents) {
            if (component is GestureControlComponent) {
                component.onVolumeChange(percent)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //滑动结束时事件处理
        if (!gestureDetector.onTouchEvent(event)) {
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
        for ((component) in controlComponents) {
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
        for ((component) in controlComponents) {
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