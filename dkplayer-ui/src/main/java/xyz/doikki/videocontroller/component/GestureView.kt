package xyz.doikki.videocontroller.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.*
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.controller.ControlWrapper
import xyz.doikki.videoplayer.controller.component.KeyControlComponent
import xyz.doikki.videoplayer.util.PlayerUtils
import xyz.doikki.videoplayer.util.layoutInflater

/**
 * 手势控制
 */
class GestureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), KeyControlComponent {

    private var mControlWrapper: ControlWrapper? = null
    private val mIcon: ImageView
    private val mProgressPercent: ProgressBar
    private val mTextPercent: TextView
    private val mCenterContainer: LinearLayout

    override fun attach(controlHolder: ControlWrapper) {
        mControlWrapper = controlHolder
    }

    override fun getView(): View? {
        return this
    }

    override fun onStartLeftOrRightKeyPressedForSeeking(event: KeyEvent) {
        onStartSlide()
    }

    override fun onStopLeftOrRightKeyPressedForSeeking(event: KeyEvent) {
        onStopSlide()
    }

    override fun onCancelLeftOrRightKeyPressedForSeeking(keyEvent: KeyEvent) {
        onStopSlide()
    }

    override fun onStartSlide() {
        mControlWrapper?.hide()
        mCenterContainer.visibility = VISIBLE
        mCenterContainer.alpha = 1f
    }

    override fun onStopSlide() {
        mCenterContainer.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mCenterContainer.visibility = GONE
                }
            })
            .start()
    }

    override fun onPositionChange(slidePosition: Int, currentPosition: Int, duration: Int) {
        mProgressPercent.visibility = GONE
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_forward)
        } else if (slidePosition < currentPosition) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_rewind)
        } else {
            //相等的情况不处理，避免最大最小位置图标错乱
        }
        mTextPercent.text =
            "${PlayerUtils.stringForTime(slidePosition)}/${PlayerUtils.stringForTime(duration)}"
    }

    override fun onBrightnessChange(percent: Int) {
        mProgressPercent.visibility = VISIBLE
        mIcon.setImageResource(R.drawable.dkplayer_ic_action_brightness)
        mTextPercent.text = "$percent%"
        mProgressPercent.progress = percent
    }

    override fun onVolumeChange(percent: Int) {
        mProgressPercent.visibility = VISIBLE
        if (percent <= 0) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_off)
        } else {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_up)
        }
        mTextPercent.text = "$percent%"
        mProgressPercent.progress = percent
    }

    override fun onPlayStateChanged(playState: Int) {
        visibility =
            if (playState == DKVideoView.STATE_IDLE || playState == DKVideoView.STATE_START_ABORT || playState == DKVideoView.STATE_PREPARING || playState == DKVideoView.STATE_PREPARED || playState == DKVideoView.STATE_ERROR || playState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
                GONE
            } else {
                VISIBLE
            }
    }

    init {
        visibility = GONE
        layoutInflater.inflate(R.layout.dkplayer_layout_gesture_control_view, this)
        mIcon = findViewById(R.id.iv_icon)
        mProgressPercent = findViewById(R.id.pro_percent)
        mTextPercent = findViewById(R.id.tv_percent)
        mCenterContainer = findViewById(R.id.center_container)
    }
}