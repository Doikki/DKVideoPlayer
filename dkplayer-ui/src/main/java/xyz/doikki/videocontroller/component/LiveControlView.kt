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
import xyz.doikki.videoplayer.DKVideoView
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

    private val mFullScreen: ImageView
    private val mBottomContainer: LinearLayout
    private val mPlayButton: ImageView

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

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            DKVideoView.STATE_IDLE, DKVideoView.STATE_START_ABORT, DKVideoView.STATE_PREPARING, DKVideoView.STATE_PREPARED, DKVideoView.STATE_ERROR, DKVideoView.STATE_PLAYBACK_COMPLETED -> visibility =
                GONE
            DKVideoView.STATE_PLAYING -> mPlayButton.isSelected = true
            DKVideoView.STATE_PAUSED -> mPlayButton.isSelected = false
            DKVideoView.STATE_BUFFERING, DKVideoView.STATE_BUFFERED -> mPlayButton.isSelected =
                controller?.playerControl?.isPlaying.orDefault()
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        when (screenMode) {
            DKVideoView.SCREEN_MODE_NORMAL -> mFullScreen.isSelected = false
            DKVideoView.SCREEN_MODE_FULL -> mFullScreen.isSelected = true
        }
        val activity = PlayerUtils.scanForActivity(context)
        val controller = controller
        if (activity != null && controller != null && controller.hasCutout == true) {
            val orientation = activity.requestedOrientation
            val cutoutHeight = controller.cutoutHeight
            when (orientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    mBottomContainer.setPadding(0, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    mBottomContainer.setPadding(cutoutHeight, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    mBottomContainer.setPadding(0, 0, cutoutHeight, 0)
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
        mFullScreen = findViewById(R.id.fullscreen)
        mFullScreen.setOnClickListener(this)
        mBottomContainer = findViewById(R.id.bottom_container)
        mPlayButton = findViewById(R.id.iv_play)
        mPlayButton.setOnClickListener(this)
        val refresh = findViewById<ImageView>(R.id.iv_refresh)
        refresh.setOnClickListener(this)
    }
}