package xyz.doikki.dkplayer.widget.component

import android.util.LruCache
import android.view.View
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.controller.VideoController
import xyz.doikki.videoplayer.controller.VideoViewControl
import xyz.doikki.videoplayer.controller.component.ControlComponent
import xyz.doikki.videoplayer.util.L
import xyz.doikki.videoplayer.util.orDefault

/**
 * 保存进度
 * Created by Doikki on 2022/12/16.
 */
class ProgressManager : ControlComponent {

    companion object {
        //保存100条记录
        private val cache = LruCache<Int, Long?>(100)
    }


    private var controller: VideoController? = null

    private var url: String? = null

    private fun generateKey(url: String): Int {
        return url.hashCode()
    }

    override fun attachController(controller: VideoController) {
        this.controller = controller
    }

    override fun onPlayerAttached(player: VideoViewControl) {

    }

    override fun getView(): View? = null

    override fun onPlayStateChanged(playState: Int, extras: HashMap<String, Any>) {
        val url = extras[VideoView.EXT_DATA_SOURCE] as? String ?: return
        this.url = url
        val player = controller?.playerControl ?: return
        when (playState) {
            VideoView.STATE_PREPARING -> {
                val progress = getSavedProgress(url)
                L.d("get progress $progress")
                player.seekTo(progress)
            }
            VideoView.STATE_PLAYBACK_COMPLETED -> {
                L.d("clear progress")
                clear(url)
            }
        }
    }

    override fun onProgressChanged(duration: Long, position: Long) {
        url?.let {
            L.d("save progress $position")
            saveProgress(it, position)
        }
    }


    private fun saveProgress(url: String, progress: Long) {
        if (url.isEmpty())
            return
        if (progress == 0L) {
            clear(url)
            return
        }
        cache.put(generateKey(url), progress)
    }

    private fun getSavedProgress(url: String): Long {
        return if (url.isEmpty()) 0 else cache[generateKey(url)].orDefault()
    }

    private fun clear(url: String) {
        cache.remove(generateKey(url))
    }

    fun clearAll() {
        cache.evictAll()
    }

}