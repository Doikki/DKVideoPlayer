package xyz.doikki.videocontroller

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import xyz.doikki.videoplayer.controller.component.KeyControlComponent
import xyz.doikki.videoplayer.loopKeyWhen
import kotlin.math.ceil

open class TVVideoController @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : StandardVideoController(context, attrs) {

    /**
     * 是否已经触发过pending seek的意图
     */
    private var mHasDispatchPendingSeek: Boolean = false

    /**
     * 当前待seek的位置
     */
    private var mCurrentPendingSeekPosition: Int = 0

    /**
     * 按键seek的系数，
     */
    private var mKeySeekRatio: Float = 1f

    private val seekCalculator: PendingSeekCalculator = DurationSamplingSeekCalculator()

    init {
        //设置可以获取焦点
        isFocusable = true
        isFocusableInTouchMode = true
        descendantFocusability = FOCUS_BEFORE_DESCENDANTS
    }

    /**
     * 设置按键seek的系数
     * @param ratio >0
     */
    fun setKeySeekRatio(ratio: Float) {
        require(ratio > 0) {
            "ratio must be greater than 0."
        }
        mKeySeekRatio = ratio
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.d(
            "Controller2",
            "now=${System.currentTimeMillis()} downTime=${event.downTime} eventTime=${event.eventTime} repeatCount=${event.repeatCount}"
        )
        val keyCode = event.keyCode
        val uniqueDown = (event.repeatCount == 0 && event.action == KeyEvent.ACTION_DOWN)
        when (keyCode) {
            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_MENU -> {//返回键&菜单键逻辑
                if (uniqueDown && isShowing) {
                    //如果当前显示了控制器，则隐藏；
                    hide()
                    return true
                }
                //否则如果当前处于全屏则退出全屏
                return super.onBackPressed()
            }
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_DPAD_CENTER
            -> {//播放/暂停切换键
                if (uniqueDown) {
                    //如果是第一次按下Ok键/播放暂停键/空格键 等，则在播放和暂停之间进行切换
                    togglePlay()
                    show()
                }
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {//播放键
                if (uniqueDown && !isInPlaybackState) {//没有在播放中，则开始播放
                    invokeOnPlayerAttached(showToast = false) { player ->
                        player.start()
                    }
                    show()
                }
                return true
            }
            KeyEvent.KEYCODE_MEDIA_STOP,
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {//暂停键
                if (uniqueDown && isInPlaybackState) {
                    invokeOnPlayerAttached(showToast = false) { player ->
                        player.pause()
                    }
                    show()
                }
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_MUTE,
            KeyEvent.KEYCODE_CAMERA
            -> {//系统功能键
                // don't show the controls for volume adjustment
                //系统会显示对应的UI
                return super.dispatchKeyEvent(event)
            }
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_LEFT -> {//左右键，做seek行为
                if (!canPressKeyToSeek()) {//不允许拖动
                    if (mHasDispatchPendingSeek) {
                        cancelPendingSeekPosition(event)
                    }
                    return true
                }
                if (uniqueDown && !isShowing) {
                    //第一次按下down并且当前控制器没有显示的情况下，只显示控制器
                    show()
                    return true
                }
                //后续的逻辑存在以下几种情况：
                //1、第一次按下down，并且控制已经显示，此时应该做seek动作
                //2、执行up（存在可能已经有seek动作，或者没有seek动作：即按下down之后，立马执行了up）
                //3、第N次按下down（n >1 ）

                if (event.action == KeyEvent.ACTION_UP && !mHasDispatchPendingSeek) {
                    //按下down之后执行了up，相当于只按了一次方向键，
                    // 并且没有执行过pending行为（即单次按键的时候控制器还未显示，控制器已经显示的情况下单次按键是有效的行为），不做seek动作
                    return true
                }
                handlePendingKeySeek(event)
                return true
            }
            else -> {
                show()
                return super.dispatchKeyEvent(event)
            }
        }
    }

    /**
     * 处理按键拖动
     */
    private fun handlePendingKeySeek(event: KeyEvent) {
        invokeOnPlayerAttached(showToast = false) { player ->
            val duration = player.duration.toInt()
            val currentPosition = player.currentPosition.toInt()

            if (event.action == KeyEvent.ACTION_DOWN) {
                if (!mHasDispatchPendingSeek) {
                    mHasDispatchPendingSeek = true
                    mCurrentPendingSeekPosition = currentPosition
                    mControlComponents.loopKeyWhen<KeyControlComponent> {
                        it.onStartLeftOrRightKeyPressed(event)
                    }
                    seekCalculator.prepareCalculate(event, currentPosition, duration, width)
                }

                val previousPosition = mCurrentPendingSeekPosition
                val incrementTimeMs =
                    seekCalculator.calculateIncrement(event, previousPosition, duration, width)
                mCurrentPendingSeekPosition = (mCurrentPendingSeekPosition + incrementTimeMs)
                    .coerceAtLeast(0)
                    .coerceAtMost(duration)

                setPendingSeekPositionAndNotify(
                    mCurrentPendingSeekPosition,
                    previousPosition,
                    duration
                )
                Log.d(
                    "TVController",
                    "action=${event.action}  eventTime=${event.eventTime - event.downTime} increment=${incrementTimeMs} previousPosition=${previousPosition} newPosition=${mCurrentPendingSeekPosition}"
                )
            }

            if (event.action == KeyEvent.ACTION_UP && mHasDispatchPendingSeek) {
                Log.d(
                    "TVController",
                    "开始执行seek行为: pendingSeekPosition=${pendingSeekPosition}"
                )
                handlePendingSeekPosition(event)
            }
        }
    }

    /*private fun handleKeySeekPosition(event: KeyEvent) {
        invokeOnPlayerAttached { player ->
            if (event.repeatCount == 1) {//第一次执行seek
                for ((component) in mControlComponents) {
                    if (component is KeyControlComponent) {
                        component.onStartLeftOrRightKeyPressed(event)
                    }
                }
            }
            //方向系数
            val flag = if (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 1 else -1
            val pressedTime = event.eventTime - event.downTime
            val duration = player.duration.toInt()
            val currentPosition = player.currentPosition.toInt()

            val x = ceil(pressedTime / 1000.0).coerceAtLeast(1.0)//将事件时间转换成秒
            val y = (x.pow(SEEK_INCREMENT_POW) * 1000 + SEEK_INCREMENT_STEP_OFFSET_MS).toLong()
                .coerceAtMost(MAX_SEEK_INCREMENT_INTERVAL_MS)
            val newPosition =
                (currentPosition + flag * y).coerceAtMost(duration.toLong()).coerceAtLeast(0)
                    .toInt()
            setPendingSeekPositionAndNotify(newPosition, currentPosition, duration)
            Log.d(
                "TVController",
                "key pressed time:$pressedTime x=${x} y=${y} newPosition=${newPosition}"
            )
            if (event.action == KeyEvent.ACTION_UP) {
                for ((component) in mControlComponents) {
                    if (component is KeyControlComponent) {
                        component.onStopLeftOrRightKeyPressed(event)
                    }
                }
                handlePendingSeekPosition()
            }
        }
    }*/

    /**
     * 取消seek
     */
    private fun cancelPendingSeekPosition(event: KeyEvent) {
        cancelPendingSeekPosition()
        mControlComponents.loopKeyWhen<KeyControlComponent> {
            it.onCancelLeftOrRightKeyPressed(event)
        }
    }

    /**
     * 执行seek
     */
    private fun handlePendingSeekPosition(event: KeyEvent) {
        //先做stop，再seek，避免loading指示器和seek指示器同时显示
        mControlComponents.loopKeyWhen<KeyControlComponent> {
            it.onStopLeftOrRightKeyPressed(event)
        }
        handlePendingSeekPosition()
    }

    override fun handlePendingSeekPosition() {
        super.handlePendingSeekPosition()
        mHasDispatchPendingSeek = false
        mCurrentPendingSeekPosition = 0
    }

    override fun cancelPendingSeekPosition() {
        super.cancelPendingSeekPosition()
        mHasDispatchPendingSeek = false
        mCurrentPendingSeekPosition = 0
    }

    /**
     * 是否能够响应按键seek
     */
    private fun canPressKeyToSeek(): Boolean {
        return isInPlaybackState && seekEnabled
    }


    abstract class PendingSeekCalculator {

        /**
         * 对外设置的用于控制的缩放系数
         */
        var seekRatio: Float = 1f

        /**
         * seek动作前做准备
         */
        abstract fun prepareCalculate(
            event: KeyEvent,
            currentPosition: Int,
            duration: Int,
            viewWidth: Int
        )

        /**
         * 返回本次seek的增量
         */
        abstract fun calculateIncrement(
            event: KeyEvent,
            currentPosition: Int,
            duration: Int,
            viewWidth: Int
        ): Int

        abstract fun reset()

    }


    class DurationSamplingSeekCalculator : PendingSeekCalculator() {

        /**
         * 增量最大倍数:相当于用户按住方向键一直做seek多少s之后达到最大的seek步长
         */
        private val maxIncrementFactor: Float = 16f

        /**
         * 最大的时间增量：默认为时长的百分之一，最小1000
         */
        private var maxIncrementTimeMs: Int = 0

        /**
         * 最小时间增量:最小1000
         */
        private var minIncrementTimeMs: Int = 0

        /**
         * 最少seek多少次seek完整个时长，默认500次，一次事件大概需要50毫秒，所以大致需要25s事件，也就是说一个很长的视频，最快25s seek完，但是由于是采用不断加速的形式，因此实际时间远大于25s
         */
        private val leastSeekCount = 500

        override fun reset() {
            //假设一个场景：设定两个变量 s = 面条的长度（很长很长）  c = 一个人最快吃多少口可以吃完。
            // 假定1s时间内一个人能够吃 20口
            //则一个人吃一口的最大长度 umax = s / c    假定一个系数f   这个人吃一口的最小长度 umin = umax / f
            // 现在这个人从umin的速度开始吃，时间作为系数（不超过f），那么这个人吃完s需要多少时间？

            //假定  s = 7200000 c = 500  f = 16
            maxIncrementTimeMs = 0
            minIncrementTimeMs = 0
        }

        override fun prepareCalculate(
            event: KeyEvent,
            currentPosition: Int,
            duration: Int,
            viewWidth: Int
        ) {
            maxIncrementTimeMs = duration / leastSeekCount
            minIncrementTimeMs = (maxIncrementTimeMs / maxIncrementFactor).toInt()
        }

        override fun calculateIncrement(
            event: KeyEvent,
            currentPosition: Int,
            duration: Int,
            viewWidth: Int
        ): Int {
            //方向系数
            val flag = if (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 1 else -1
            val eventTime = event.eventTime - event.downTime
            val factor =
                ceil(eventTime / 1000f).coerceAtMost(maxIncrementFactor) //时间转换成秒，作为系数,不超过最大的倍数
            //本次偏移距离
            return (factor * minIncrementTimeMs * seekRatio).toInt().coerceAtLeast(1000) * flag
        }

    }

//    class BasedOnWidthSeekCalculator : PendingSeekCalculator() {
//
//        /**
//         * 每次按键偏移的距离
//         */
//        val deltaPixelsStep = 4f
//
//        /**
//         * 最大倍数
//         */
//        val maxDeltaPixelsRatio: Float = 16f
//
//        /**
//         * 每次偏移的最小时间ms
//         */
//        val minOffsetTimeMs = 1000
//
//        override fun prepareCalculate(
//            event: KeyEvent,
//            currentPosition: Int,
//            duration: Int,
//            viewWidth: Int
//        ) {
//
//        }
//
//        override fun calculateIncrement(
//            event: KeyEvent,
//            currentPosition: Int,
//            duration: Int,
//            viewWidth: Int
//        ): Int {
//            //方向系数
//            val flag = if (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 1 else -1
//
//
//            val eventTime = event.eventTime - event.downTime
//            val scale = ceil(eventTime / 1000f).coerceAtMost(maxDeltaPixelsRatio) //时间转换成秒，作为系数
//
//            //本次偏移距离
//            val incrementOffset =
//                ().coerceAtMost(maxIncrementDeltaPixels)
//
//            //本次增加的偏移时间 至少minOffsetTimeMs
//            val incrementTimeMs =
//                (scale * deltaPixelsStep / viewWidth * seekRatio * duration).toInt()
//                    .coerceAtLeast(minOffsetTimeMs) * flag
//        }
//
//
//    }
}