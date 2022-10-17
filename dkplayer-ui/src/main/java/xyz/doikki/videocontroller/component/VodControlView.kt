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
import xyz.doikki.videoplayer.DKVideoView
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

    private var mBottomContainer: LinearLayout? = null
    private var mFullScreen: ImageView? = null
    private var mTotalTime: TextView? = null
    private var mCurrTime: TextView? = null
    private var mPlayButton: ImageView? = null
    private var mVideoProgress: SeekBar? = null
    private var mBottomProgress: ProgressBar? = null

    /**
     * 是否正在拖动SeekBar
     */
    private var mTrackingTouch = false

    /**
     * 是否显示底部进度条，默认显示
     */
    var showBottomProgress = true

    @LayoutRes
    protected open val layoutId: Int = R.layout.dkplayer_layout_vod_control_view


    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (isVisible) {
            mBottomContainer?.let { bottomContainer ->
                bottomContainer.visibility = VISIBLE
                anim?.let {
                    bottomContainer.startAnimation(it)
                }
            }
            if (showBottomProgress) {
                mBottomProgress?.visibility = GONE
            }
        } else {
            mBottomContainer?.let { bottomContainer ->
                bottomContainer.visibility = GONE
                anim?.let {
                    bottomContainer.startAnimation(it)
                }
            }

            if (showBottomProgress) {
                mBottomProgress?.let { bottomProgress ->
                    bottomProgress.visibility = VISIBLE
                    val animation = AlphaAnimation(0f, 1f)
                    animation.duration = 300
                    bottomProgress.startAnimation(animation)
                }
            }
        }
    }

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            DKVideoView.STATE_IDLE, DKVideoView.STATE_PLAYBACK_COMPLETED -> {
                visibility = GONE
                mBottomProgress?.let {
                    it.progress = 0
                    it.secondaryProgress = 0
                }
                mVideoProgress?.let {
                    it.progress = 0
                    it.secondaryProgress = 0
                }
            }
            DKVideoView.STATE_START_ABORT, DKVideoView.STATE_PREPARING,
            DKVideoView.STATE_PREPARED, DKVideoView.STATE_ERROR -> visibility = GONE
            DKVideoView.STATE_PLAYING -> {
                mPlayButton?.isSelected = true
                if (showBottomProgress) {
                    if (mController?.isShowing.orDefault()) {
                        mBottomProgress?.visibility = GONE
                        mBottomContainer?.visibility = VISIBLE
                    } else {
                        mBottomContainer?.visibility = GONE
                        mBottomProgress?.visibility = VISIBLE
                    }
                } else {
                    mBottomContainer?.visibility = GONE
                }
                visibility = VISIBLE
                //开始刷新进度
                mController?.startUpdateProgress()
            }
            DKVideoView.STATE_PAUSED -> mPlayButton?.isSelected = false
            DKVideoView.STATE_BUFFERING -> {
                mPlayButton?.isSelected = player?.isPlaying.orDefault()
                // 停止刷新进度
                mController?.stopUpdateProgress()
            }
            DKVideoView.STATE_BUFFERED -> {
                mPlayButton?.isSelected = player?.isPlaying.orDefault()
                //开始刷新进度
                mController?.startUpdateProgress()
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        when (screenMode) {
            DKVideoView.SCREEN_MODE_NORMAL -> mFullScreen?.isSelected = false
            DKVideoView.SCREEN_MODE_FULL -> mFullScreen?.isSelected = true
        }
        val activity = this.activity ?: return
        val controller = mController ?: return
        val bottomContainer = mBottomContainer ?: return
        val bottomProgress = mBottomProgress ?: return
        if (controller.hasCutout()) {
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
            mController?.togglePlay()
        }
    }

    /**
     * 横竖屏切换
     */
    private fun toggleFullScreen() {
        mController?.toggleFullScreen()
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mTrackingTouch = true
        mController?.let {
            it.stopUpdateProgress()
            it.stopFadeOut()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        try {
            mController?.let { controller ->
                val player = this.player ?: return@let
                val duration = player.duration
                val newPosition = duration * seekBar.progress / mVideoProgress!!.max
                player.seekTo(newPosition.toInt().toLong())
                mTrackingTouch = false
                controller.startUpdateProgress()
                controller.startFadeOut()
            }
        } finally {
            mTrackingTouch = false
        }
    }


    override fun onProgressChanged(duration: Int, position: Int) {
        if (mTrackingTouch) {
            return
        }
        mVideoProgress?.let { seekBar ->
            if (duration > 0) {
                seekBar.isEnabled = true
                val pos = (position * 1.0 / duration * seekBar.max).toInt()
                seekBar.progress = pos
                mBottomProgress?.progress = pos
            } else {
                seekBar.isEnabled = false
            }
            val percent = player?.bufferedPercentage.orDefault()
            if (percent >= 95) { //解决缓冲进度不能100%问题
                seekBar.secondaryProgress = seekBar.max
                mBottomProgress?.secondaryProgress = mBottomProgress?.max.orDefault(100)
            } else {
                seekBar.secondaryProgress = percent * 10
                mBottomProgress?.secondaryProgress = percent * 10
            }
        }

        mTotalTime?.text = PlayerUtils.stringForTime(duration)
        mCurrTime?.text = PlayerUtils.stringForTime(position)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }
        mController?.playerControl?.let { player ->
            val duration = player.duration
            val newPosition = duration * progress / mVideoProgress?.max.orDefault(100)
            mCurrTime?.text = PlayerUtils.stringForTime(newPosition.toInt())
        }
    }

    init {
        visibility = GONE
        layoutInflater.inflate(layoutId, this)
        mFullScreen = findViewById(R.id.fullscreen)
        mFullScreen?.setOnClickListener(this)
        mBottomContainer = findViewById(R.id.bottom_container)
        mVideoProgress = findViewById(R.id.seekBar)
        mVideoProgress?.setOnSeekBarChangeListener(this)
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= 22) {
            mVideoProgress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        mTotalTime = findViewById(R.id.total_time)
        mCurrTime = findViewById(R.id.curr_time)
        mPlayButton = findViewById(R.id.iv_play)
        mPlayButton?.setOnClickListener(this)
        mBottomProgress = findViewById(R.id.bottom_progress)
    }
}