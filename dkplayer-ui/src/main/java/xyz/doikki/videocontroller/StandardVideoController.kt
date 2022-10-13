package xyz.doikki.videocontroller

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.AttrRes
import xyz.doikki.videocontroller.component.*
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.controller.GestureVideoController
import xyz.doikki.videoplayer.orDefault
import xyz.doikki.videoplayer.toast
import xyz.doikki.videoplayer.util.PlayerUtils

/**
 * 直播/点播控制器
 * 注意：此控制器仅做一个参考，如果想定制ui，你可以直接继承GestureVideoController或者BaseVideoController实现
 * 你自己的控制器
 * Created by Doikki on 2017/4/7.
 */
open class StandardVideoController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : GestureVideoController(context, attrs, defStyleAttr), View.OnClickListener {

    protected val lockButton: ImageView
    protected val loadingIndicator: ProgressBar
    private var isBuffering = false

    init {
        val layoutId = getLayoutId()
        if (layoutId > 0)
            LayoutInflater.from(context).inflate(layoutId, this, true)
        lockButton = findViewById(R.id.lock)
        lockButton.setOnClickListener(this)
        loadingIndicator = findViewById(R.id.loading)
    }

    protected open fun getLayoutId(): Int {
        return R.layout.dkplayer_layout_standard_controller
    }

    /**
     * 快速添加各个组件
     * @param title  标题
     * @param isLive 是否为直播
     */
    fun addDefaultControlComponent(title: String?, isLive: Boolean) {
        val completeView = CompleteView(context)
        val errorView = ErrorView(context)
        val prepareView = PrepareView(context)
        prepareView.setClickStart()
        val titleView = TitleView(context)
        titleView.setTitle(title)
        addControlComponent(completeView, errorView, prepareView, titleView)
        if (isLive) {
            addControlComponent(LiveControlView(context))
        } else {
            addControlComponent(VodControlView(context))
        }
        addControlComponent(GestureView(context))
        seekEnabled = !isLive
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.lock) {
            toggleLock()
        }
    }



    override fun onLockStateChanged(isLocked: Boolean) {
        if (isLocked) {
            lockButton.isSelected = true
            toast(R.string.dkplayer_locked)
        } else {
            lockButton.isSelected = false
            toast(R.string.dkplayer_unlocked)
        }
    }

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (mPlayer?.isFullScreen.orDefault()) {
            if (isVisible) {
                if (lockButton.visibility == GONE) {
                    lockButton.visibility = VISIBLE
                    if (anim != null) {
                        lockButton.startAnimation(anim)
                    }
                }
            } else {
                lockButton.visibility = GONE
                if (anim != null) {
                    lockButton.startAnimation(anim)
                }
            }
        }
    }

    override fun onScreenModeChanged(screenMode: Int) {
        super.onScreenModeChanged(screenMode)
        when (screenMode) {
            DKVideoView.SCREEN_MODE_NORMAL -> {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                lockButton.visibility = GONE
            }
            DKVideoView.SCREEN_MODE_FULL -> if (isShowing) {
                lockButton.visibility = VISIBLE
            } else {
                lockButton.visibility = GONE
            }
        }
        if (mActivity != null && hasCutout()) {
            val orientation = mActivity!!.requestedOrientation
            val dp24 = PlayerUtils.dp2px(context, 24f)
            val cutoutHeight = cutoutHeight
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                val lblp = lockButton!!.layoutParams as LayoutParams
                lblp.setMargins(dp24, 0, dp24, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                val layoutParams = lockButton!!.layoutParams as LayoutParams
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                val layoutParams = lockButton!!.layoutParams as LayoutParams
                layoutParams.setMargins(dp24, 0, dp24, 0)
            }
        }
    }

    override fun onPlayerStateChanged(playState: Int) {
        super.onPlayerStateChanged(playState)
        when (playState) {
            DKVideoView.STATE_IDLE -> {
                lockButton!!.isSelected = false
                loadingIndicator!!.visibility = GONE
            }
            DKVideoView.STATE_PLAYING, DKVideoView.STATE_PAUSED, DKVideoView.STATE_PREPARED, DKVideoView.STATE_ERROR, DKVideoView.STATE_BUFFERED -> {
                if (playState == DKVideoView.STATE_BUFFERED) {
                    isBuffering = false
                }
                if (!isBuffering) {
                    loadingIndicator!!.visibility = GONE
                }
            }
            DKVideoView.STATE_PREPARING, DKVideoView.STATE_BUFFERING -> {
                loadingIndicator!!.visibility = VISIBLE
                if (playState == DKVideoView.STATE_BUFFERING) {
                    isBuffering = true
                }
            }
            DKVideoView.STATE_PLAYBACK_COMPLETED -> {
                loadingIndicator!!.visibility = GONE
                lockButton!!.visibility = GONE
                lockButton!!.isSelected = false
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (isLocked) {
            show()
            toast(R.string.dkplayer_lock_tip)
            return true
        }
        return invokeOnPlayerAttached {
            if (it.isFullScreen) {
                stopFullScreen()
            } else {
                super.onBackPressed()
            }
        } ?: super.onBackPressed()
//        return if (controlWrapper!!.isFullScreen) {
//            stopFullScreen()
//        } else super.onBackPressed()
    }
}