package xyz.doikki.videocontroller.component

import android.util.LruCache
import xyz.doikki.videoplayer.util.orDefault

class ProgressManagerImpl : ProgressManager {

    //保存100条记录
    private val mCache = LruCache<Int, Long?>(100)

    private fun generateKey(url: String): Int {
        return url.hashCode()
    }

    override fun saveProgress(url: String, progress: Long) {
        if (url.isEmpty())
            return
        if (progress == 0L) {
            clear(url)
            return
        }
        mCache.put(generateKey(url), progress)
    }

    override fun getSavedProgress(url: String): Long {
        return if (url.isEmpty()) 0 else mCache[generateKey(url)].orDefault()
    }

    override fun clear(url: String) {
        mCache.remove(generateKey(url))
    }

    override fun clearAll() {
        mCache.evictAll()
    }


}