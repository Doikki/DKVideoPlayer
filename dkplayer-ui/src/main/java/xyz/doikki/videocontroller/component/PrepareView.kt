package xyz.doikki.videocontroller.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.VideoView

/**
 * 准备播放界面
 */
class PrepareView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr) {

    private val thumb: ImageView
    private val startPlay: ImageView
    private val loading: ProgressBar
    private val netWarning: FrameLayout

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
            VideoView.STATE_PREPARING -> {
                bringToFront()
                visibility = VISIBLE
                startPlay.visibility = GONE
                netWarning.visibility = GONE
                loading.visibility = VISIBLE
            }
            VideoView.STATE_PLAYING, VideoView.STATE_PAUSED, VideoView.STATE_ERROR, VideoView.STATE_BUFFERING, VideoView.STATE_BUFFERED, VideoView.STATE_PLAYBACK_COMPLETED ->
                visibility = GONE
            VideoView.STATE_IDLE -> {
                visibility = VISIBLE
                bringToFront()
                loading.visibility = GONE
                netWarning.visibility = GONE
                startPlay.visibility = VISIBLE
                thumb.visibility = VISIBLE
            }
            VideoView.STATE_START_ABORT -> {
                visibility = VISIBLE
                netWarning.visibility = VISIBLE
                netWarning.bringToFront()
            }
        }
    }

    init {
        layoutInflater.inflate(R.layout.dkplayer_layout_prepare_view, this)
        thumb = findViewById(R.id.thumb)
        startPlay = findViewById(R.id.start_play)
        loading = findViewById(R.id.loading)
        netWarning = findViewById(R.id.net_warning_layout)
        val btnInWarning = findViewById<View>(R.id.status_btn)
        if (isTelevisionUiMode()) {
            setViewInFocusMode(startPlay)
            setViewInFocusMode(btnInWarning)
        }
        btnInWarning.setOnClickListener {
            netWarning.visibility = GONE
            GlobalConfig.isPlayOnMobileNetwork = true
            controller?.playerControl?.start()
        }
    }
}