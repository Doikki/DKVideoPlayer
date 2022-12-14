package xyz.doikki.videocontroller.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.util.isVisible
import xyz.doikki.videoplayer.util.orDefault

/**
 * 自动播放完成界面
 *
 *
 * update by luochao at 2022/9/28
 */
class CompleteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseControlComponent(context, attrs) {

    private val mStopFullscreen: ImageView

    /**
     * 设置播放结束按钮的文本（默认是“重新播放”）
     *
     * @param message
     */
    fun setCompleteText(message: CharSequence?) {
        val tv = findViewById<TextView>(R.id.tv_replay) ?: return
        tv.text = message
    }

    override fun onPlayStateChanged(playState: Int) {
        if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            visibility = VISIBLE
            mStopFullscreen.isVisible = controller?.isFullScreen.orDefault()
            bringToFront()
        } else {
            visibility = GONE
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        if (screenMode == VideoView.SCREEN_MODE_FULL) {
            mStopFullscreen.visibility = VISIBLE
        } else if (screenMode == VideoView.SCREEN_MODE_NORMAL) {
            mStopFullscreen.visibility = GONE
        }
        controller?.let { controller ->
            val activity = activity
            if (activity != null && controller.hasCutout == true) {
                val orientation = activity.requestedOrientation
                val cutoutHeight = controller.cutoutHeight
                val sflp = mStopFullscreen.layoutParams as LayoutParams
                when (orientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        sflp.setMargins(0, 0, 0, 0)
                    }
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        sflp.setMargins(cutoutHeight, 0, 0, 0)
                    }
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                        sflp.setMargins(0, 0, 0, 0)
                    }
                }
            }
        }
    }

    init {
        visibility = GONE
        layoutInflater.inflate(R.layout.dkplayer_layout_complete_view, this)
        //在xml中去除了一个布局层级，因此xml中的背景色改为代码设置在当前布局中
        setBackgroundColor(Color.parseColor("#33000000"))
        val replyAct = findViewById<View>(R.id.replay_layout)
        if (isTelevisionUiMode()) {
            replyAct.isClickable = true
            setViewInFocusMode(replyAct)
        } else {
            //防止touch模式下，事件穿透
            isClickable = true
        }

        replyAct.setOnClickListener { //重新播放
            controller?.replay(true)
        }
        mStopFullscreen = findViewById(R.id.stop_fullscreen)
        mStopFullscreen.setOnClickListener {
            controller?.let { controller ->
                if (controller.isFullScreen) {
                    val activity = activity
                    if (activity != null && !activity.isFinishing) {
                        controller.stopFullScreen()
                    }
                }
            }
        }
    }
}