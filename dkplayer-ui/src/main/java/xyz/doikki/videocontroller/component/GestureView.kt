package xyz.doikki.videocontroller.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.controller.component.KeyControlComponent
import xyz.doikki.videoplayer.util.PlayerUtils

/**
 * 手势控制
 */
class GestureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr), KeyControlComponent {

    private val icon: ImageView
    private val progressPercent: ProgressBar
    private val textPercent: TextView
    private val centerContainer: LinearLayout

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
        controller?.hide()
        centerContainer.visibility = VISIBLE
        centerContainer.alpha = 1f
    }

    override fun onStopSlide() {
        centerContainer.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    centerContainer.visibility = GONE
                }
            })
            .start()
    }

    override fun onPositionChange(slidePosition: Int, currentPosition: Int, duration: Int) {
        progressPercent.visibility = GONE
        if (slidePosition > currentPosition) {
            icon.setImageResource(R.drawable.dkplayer_ic_action_fast_forward)
        } else if (slidePosition < currentPosition) {
            icon.setImageResource(R.drawable.dkplayer_ic_action_fast_rewind)
        } else {
            //相等的情况不处理，避免最大最小位置图标错乱
        }
        textPercent.text =
            "${PlayerUtils.stringForTime(slidePosition)}/${PlayerUtils.stringForTime(duration)}"
    }

    override fun onBrightnessChange(percent: Int) {
        progressPercent.visibility = VISIBLE
        icon.setImageResource(R.drawable.dkplayer_ic_action_brightness)
        textPercent.text = "$percent%"
        progressPercent.progress = percent
    }

    override fun onVolumeChange(percent: Int) {
        progressPercent.visibility = VISIBLE
        if (percent <= 0) {
            icon.setImageResource(R.drawable.dkplayer_ic_action_volume_off)
        } else {
            icon.setImageResource(R.drawable.dkplayer_ic_action_volume_up)
        }
        textPercent.text = "$percent%"
        progressPercent.progress = percent
    }

    override fun onPlayStateChanged(playState: Int) {
        visibility =
            if (playState == VideoView.STATE_IDLE || playState == VideoView.STATE_START_ABORT || playState == VideoView.STATE_PREPARING || playState == VideoView.STATE_PREPARED || playState == VideoView.STATE_ERROR || playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                GONE
            } else {
                VISIBLE
            }
    }

    init {
        visibility = GONE
        layoutInflater.inflate(R.layout.dkplayer_layout_gesture_control_view, this)
        icon = findViewById(R.id.iv_icon)
        progressPercent = findViewById(R.id.pro_percent)
        textPercent = findViewById(R.id.tv_percent)
        centerContainer = findViewById(R.id.center_container)
    }
}