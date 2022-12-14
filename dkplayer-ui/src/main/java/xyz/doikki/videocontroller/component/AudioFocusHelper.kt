package xyz.doikki.videocontroller.component

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Handler
import android.os.Looper
import xyz.doikki.videoplayer.VideoView
import java.lang.ref.WeakReference

/**
 * 音频焦点 帮助类
 */
class AudioFocusHelper(videoView: VideoView) : OnAudioFocusChangeListener {
    private val handler = Handler(Looper.getMainLooper())
    private val weakVideoView: WeakReference<VideoView>
    private val audioManager: AudioManager?
    private var startRequested = false
    private var pausedForLoss = false
    private var currentFocus = 0

    init {
        weakVideoView = WeakReference(videoView)
        audioManager =
            videoView.context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (currentFocus == focusChange) {
            return
        }
        //这里应该先改变状态，然后在post，否则在极短时间内存在理论上的多次post
        currentFocus = focusChange

        //由于onAudioFocusChange有可能在子线程调用，
        //故通过此方式切换到主线程去执行
        handler.post {
            try { //进行异常捕获，避免因为音频焦点导致crash
                handleAudioFocusChange(focusChange)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun handleAudioFocusChange(focusChange: Int) {
        val videoView = weakVideoView.get() ?: return
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                if (startRequested || pausedForLoss) {
                    videoView.start()
                    startRequested = false
                    pausedForLoss = false
                }
                if (!videoView.isMute) //恢复音量
                    videoView.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (videoView.isPlaying) {
                pausedForLoss = true
                videoView.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (videoView.isPlaying && !videoView.isMute) {
                videoView.setVolume(0.1f, 0.1f)
            }
        }
    }

    /**
     * Requests to obtain the audio focus
     * 请求音频焦点
     */
    fun requestFocus() {
        if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
            return
        }
        if (audioManager == null) {
            return
        }
        val status = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
            currentFocus = AudioManager.AUDIOFOCUS_GAIN
            return
        }
        startRequested = true
    }

    /**
     * Requests the system to drop the audio focus
     * 放弃音频焦点
     */
    fun abandonFocus() {
        if (audioManager == null) {
            return
        }
        startRequested = false
        audioManager.abandonAudioFocus(this)
    }
}