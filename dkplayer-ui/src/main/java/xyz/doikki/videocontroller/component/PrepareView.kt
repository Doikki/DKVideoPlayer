package xyz.doikki.videocontroller.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.DKPlayerConfig
import xyz.doikki.videoplayer.DKVideoView

/**
 * 准备播放界面
 */
class PrepareView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr) {

    private val mThumb: ImageView
    private val mStartPlay: ImageView
    private val mLoading: ProgressBar
    private val mNetWarning: FrameLayout

    /**
     * 设置点击此界面开始播放
     */
    fun setClickStart() {
        if (isTelevisionUiMode()) {
            setViewInFocusMode(this)
        }
        setOnClickListener { controller?.playerControl?.start() }
    }

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            DKVideoView.STATE_PREPARING -> {
                bringToFront()
                visibility = VISIBLE
                mStartPlay.visibility = GONE
                mNetWarning.visibility = GONE
                mLoading.visibility = VISIBLE
            }
            DKVideoView.STATE_PLAYING, DKVideoView.STATE_PAUSED, DKVideoView.STATE_ERROR, DKVideoView.STATE_BUFFERING, DKVideoView.STATE_BUFFERED, DKVideoView.STATE_PLAYBACK_COMPLETED ->
                visibility = GONE
            DKVideoView.STATE_IDLE -> {
                visibility = VISIBLE
                bringToFront()
                mLoading.visibility = GONE
                mNetWarning.visibility = GONE
                mStartPlay.visibility = VISIBLE
                mThumb.visibility = VISIBLE
            }
            DKVideoView.STATE_START_ABORT -> {
                visibility = VISIBLE
                mNetWarning.visibility = VISIBLE
                mNetWarning.bringToFront()
            }
        }
    }

    init {
        layoutInflater.inflate(R.layout.dkplayer_layout_prepare_view, this)
        mThumb = findViewById(R.id.thumb)
        mStartPlay = findViewById(R.id.start_play)
        mLoading = findViewById(R.id.loading)
        mNetWarning = findViewById(R.id.net_warning_layout)
        val btnInWarning = findViewById<View>(R.id.status_btn)
        if (isTelevisionUiMode()) {
            setViewInFocusMode(mStartPlay)
            setViewInFocusMode(btnInWarning)
        }
        btnInWarning.setOnClickListener {
            mNetWarning.visibility = GONE
            DKPlayerConfig.isPlayOnMobileNetwork = true
            controller?.playerControl?.start()
        }
    }
}