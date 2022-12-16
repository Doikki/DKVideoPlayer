package xyz.doikki.videocontroller.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.util.PlayerUtils
import xyz.doikki.videoplayer.util.orDefault

/**
 * 直播底部控制栏
 *
 *
 * 此控件不适配TV模式
 */
class LiveControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr), View.OnClickListener {

    private val fullScreen: ImageView
    private val bottomContainer: LinearLayout
    private val playButton: ImageView

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (isVisible) {
            if (visibility == GONE) {
                visibility = VISIBLE
                anim?.let { startAnimation(it) }
            }
        } else {
            if (visibility == VISIBLE) {
                visibility = GONE
                anim?.let { startAnimation(it) }
            }
        }
    }

    override fun onPlayStateChanged(playState: Int, extras: HashMap<String, Any>) {
        when (playState) {
            VideoView.STATE_IDLE, VideoView.STATE_START_ABORT, VideoView.STATE_PREPARING, VideoView.STATE_PREPARED, VideoView.STATE_ERROR, VideoView.STATE_PLAYBACK_COMPLETED -> visibility =
                GONE
            VideoView.STATE_PLAYING -> playButton.isSelected = true
            VideoView.STATE_PAUSED -> playButton.isSelected = false
            VideoView.STATE_BUFFERING, VideoView.STATE_BUFFERED -> playButton.isSelected =
                controller?.playerControl?.isPlaying.orDefault()
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        when (screenMode) {
            VideoView.SCREEN_MODE_NORMAL -> fullScreen.isSelected = false
            VideoView.SCREEN_MODE_FULL -> fullScreen.isSelected = true
        }
        val activity = PlayerUtils.scanForActivity(context)
        val controller = controller
        if (activity != null && controller != null && controller.hasCutout == true) {
            val orientation = activity.requestedOrientation
            val cutoutHeight = controller.cutoutHeight
            when (orientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    bottomContainer.setPadding(0, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    bottomContainer.setPadding(cutoutHeight, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    bottomContainer.setPadding(0, 0, cutoutHeight, 0)
                }
            }
        }
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        onVisibilityChanged(!isLocked, null)
    }

    override fun onClick(v: View) {
        controller?.let { controller ->
            when (v.id) {
                R.id.fullscreen -> {
                    controller.toggleFullScreen()
                }
                R.id.iv_play -> {
                    controller.togglePlay()
                }
                R.id.iv_refresh -> {
                    controller.replay(true)
                }
                else -> {}
            }
        }

    }

    init {
        visibility = GONE
        layoutInflater.inflate(R.layout.dkplayer_layout_live_control_view, this)
        fullScreen = findViewById(R.id.fullscreen)
        fullScreen.setOnClickListener(this)
        bottomContainer = findViewById(R.id.bottom_container)
        playButton = findViewById(R.id.iv_play)
        playButton.setOnClickListener(this)
        val refresh = findViewById<ImageView>(R.id.iv_refresh)
        refresh.setOnClickListener(this)
    }
}