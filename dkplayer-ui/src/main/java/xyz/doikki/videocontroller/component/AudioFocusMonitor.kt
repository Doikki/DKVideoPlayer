package xyz.doikki.videocontroller.component

import android.view.View
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.controller.VideoController
import xyz.doikki.videoplayer.controller.VideoViewControl
import xyz.doikki.videoplayer.controller.component.ControlComponent

/**
 * 音频焦点监听，需要此功能时将此ControlComponent添加到controller中即可
 * Created by Doikki on 2022/10/17.
 */
class AudioFocusMonitor : ControlComponent {

    private var audioFocusHelper: AudioFocusHelper? = null
    private var controller: VideoController? = null

    override fun attachController(controller: VideoController) {
        this.controller = controller
    }

    override fun onPlayerAttached(player: VideoViewControl) {
        super.onPlayerAttached(player)
        audioFocusHelper = AudioFocusHelper(player as VideoView)
    }

    override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        val player = controller?.playerControl ?: return
        if (player.isMute) return
        audioFocusHelper?.let {
            when (playState) {
                VideoView.STATE_PLAYING, VideoView.STATE_PREPARED -> {
                    it.requestFocus()
                }
                VideoView.STATE_PAUSED, VideoView.STATE_IDLE -> {
                    it.abandonFocus()
                }
            }
        }

    }

    override fun getView(): View? = null
}