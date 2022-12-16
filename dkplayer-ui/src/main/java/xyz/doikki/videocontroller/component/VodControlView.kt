package xyz.doikki.videocontroller.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.LayoutRes
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.util.PlayerUtils
import xyz.doikki.videoplayer.util.orDefault

/**
 * 点播底部控制栏
 */
open class VodControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr),
    View.OnClickListener,
    OnSeekBarChangeListener {

    private var bottomContainer: LinearLayout? = null
    private var fullScreen: ImageView? = null
    private var totalTime: TextView? = null
    private var currTime: TextView? = null
    private var playButton: ImageView? = null
    private var videoProgress: SeekBar? = null
    private var bottomProgress: ProgressBar? = null

    /**
     * 是否正在拖动SeekBar
     */
    private var trackingTouch = false

    /**
     * 是否显示底部进度条，默认显示
     */
    var showBottomProgress = true

    @LayoutRes
    protected open val layoutId: Int = R.layout.dkplayer_layout_vod_control_view


    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (isVisible) {
            bottomContainer?.let { bottomContainer ->
                bottomContainer.visibility = VISIBLE
                anim?.let {
                    bottomContainer.startAnimation(it)
                }
            }
            if (showBottomProgress) {
                bottomProgress?.visibility = GONE
            }
        } else {
            bottomContainer?.let { bottomContainer ->
                bottomContainer.visibility = GONE
                anim?.let {
                    bottomContainer.startAnimation(it)
                }
            }

            if (showBottomProgress) {
                bottomProgress?.let { bottomProgress ->
                    bottomProgress.visibility = VISIBLE
                    val animation = AlphaAnimation(0f, 1f)
                    animation.duration = 300
                    bottomProgress.startAnimation(animation)
                }
            }
        }
    }

    override fun onPlayStateChanged(playState: Int, extras: HashMap<String, Any>) {
        when (playState) {
            VideoView.STATE_IDLE, VideoView.STATE_PLAYBACK_COMPLETED -> {
                visibility = GONE
                bottomProgress?.let {
                    it.progress = 0
                    it.secondaryProgress = 0
                }
                videoProgress?.let {
                    it.progress = 0
                    it.secondaryProgress = 0
                }
            }
            VideoView.STATE_START_ABORT, VideoView.STATE_PREPARING,
            VideoView.STATE_PREPARED, VideoView.STATE_ERROR -> visibility = GONE
            VideoView.STATE_PLAYING -> {
                playButton?.isSelected = true
                if (showBottomProgress) {
                    if (controller?.isShowing.orDefault()) {
                        bottomProgress?.visibility = GONE
                        bottomContainer?.visibility = VISIBLE
                    } else {
                        bottomContainer?.visibility = GONE
                        bottomProgress?.visibility = VISIBLE
                    }
                } else {
                    bottomContainer?.visibility = GONE
                }
                visibility = VISIBLE
                //开始刷新进度
                controller?.startUpdateProgress()
            }
            VideoView.STATE_PAUSED -> playButton?.isSelected = false
            VideoView.STATE_BUFFERING -> {
                playButton?.isSelected = player?.isPlaying.orDefault()
                // 停止刷新进度
                controller?.stopUpdateProgress()
            }
            VideoView.STATE_BUFFERED -> {
                playButton?.isSelected = player?.isPlaying.orDefault()
                //开始刷新进度
                controller?.startUpdateProgress()
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        when (screenMode) {
            VideoView.SCREEN_MODE_NORMAL -> fullScreen?.isSelected = false
            VideoView.SCREEN_MODE_FULL -> fullScreen?.isSelected = true
        }
        val activity = this.activity ?: return
        val controller = controller ?: return
        val bottomContainer = bottomContainer ?: return
        val bottomProgress = bottomProgress ?: return
        if (controller.hasCutout == true) {
            val orientation = activity.requestedOrientation
            val cutoutHeight = controller.cutoutHeight
            when (orientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    bottomContainer.setPadding(0, 0, 0, 0)
                    bottomProgress.setPadding(0, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    bottomContainer.setPadding(cutoutHeight, 0, 0, 0)
                    bottomProgress.setPadding(cutoutHeight, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    bottomContainer.setPadding(0, 0, cutoutHeight, 0)
                    bottomProgress.setPadding(0, 0, cutoutHeight, 0)
                }
            }
        }
    }


    override fun onLockStateChanged(isLocked: Boolean) {
        onVisibilityChanged(!isLocked, null)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.fullscreen) {
            toggleFullScreen()
        } else if (id == R.id.iv_play) {
            controller?.togglePlay()
        }
    }

    /**
     * 横竖屏切换
     */
    private fun toggleFullScreen() {
        controller?.toggleFullScreen()
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        trackingTouch = true
        controller?.let {
            it.stopUpdateProgress()
            it.stopFadeOut()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        try {
            controller?.let { controller ->
                val player = this.player ?: return@let
                val duration = player.duration
                val newPosition = duration * seekBar.progress / videoProgress!!.max
                player.seekTo(newPosition.toInt().toLong())
                trackingTouch = false
                controller.startUpdateProgress()
                controller.startFadeOut()
            }
        } finally {
            trackingTouch = false
        }
    }


    override fun onProgressChanged(duration: Int, position: Int) {
        if (trackingTouch) {
            return
        }
        videoProgress?.let { seekBar ->
            if (duration > 0) {
                seekBar.isEnabled = true
                val pos = (position * 1.0 / duration * seekBar.max).toInt()
                seekBar.progress = pos
                bottomProgress?.progress = pos
            } else {
                seekBar.isEnabled = false
            }
            val percent = player?.bufferedPercentage.orDefault()
            if (percent >= 95) { //解决缓冲进度不能100%问题
                seekBar.secondaryProgress = seekBar.max
                bottomProgress?.secondaryProgress = bottomProgress?.max.orDefault(100)
            } else {
                seekBar.secondaryProgress = percent * 10
                bottomProgress?.secondaryProgress = percent * 10
            }
        }

        totalTime?.text = PlayerUtils.stringForTime(duration)
        currTime?.text = PlayerUtils.stringForTime(position)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }
        controller?.playerControl?.let { player ->
            val duration = player.duration
            val newPosition = duration * progress / videoProgress?.max.orDefault(100)
            currTime?.text = PlayerUtils.stringForTime(newPosition.toInt())
        }
    }

    init {
        visibility = GONE
        layoutInflater.inflate(layoutId, this)
        fullScreen = findViewById(R.id.fullscreen)
        fullScreen?.setOnClickListener(this)
        bottomContainer = findViewById(R.id.bottom_container)
        videoProgress = findViewById(R.id.seekBar)
        videoProgress?.setOnSeekBarChangeListener(this)
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= 22) {
            videoProgress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        totalTime = findViewById(R.id.total_time)
        currTime = findViewById(R.id.curr_time)
        playButton = findViewById(R.id.iv_play)
        playButton?.setOnClickListener(this)
        bottomProgress = findViewById(R.id.bottom_progress)
    }
}