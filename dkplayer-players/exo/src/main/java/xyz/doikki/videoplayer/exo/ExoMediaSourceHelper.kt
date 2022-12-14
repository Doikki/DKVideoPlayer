package xyz.doikki.videoplayer.exo

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File
import java.util.*

class ExoMediaSourceHelper private constructor(context: Context) {
    private val mUserAgent: String
    private val mAppContext: Context
    private var mHttpDataSourceFactory: HttpDataSource.Factory? = null
    private var mCache: Cache? = null

    init {
        mAppContext = context.applicationContext
        mUserAgent = Util.getUserAgent(mAppContext, mAppContext.applicationInfo.name)
    }

    fun getMediaSource(uri: String): MediaSource {
        return getMediaSource(uri, null, false)
    }

    fun getMediaSource(uri: String, headers: MutableMap<String, String>?): MediaSource {
        return getMediaSource(uri, headers, false)
    }

    fun getMediaSource(uri: String, isCache: Boolean): MediaSource {
        return getMediaSource(uri, null, isCache)
    }

    fun getMediaSource(
        uri: String,
        headers: MutableMap<String, String>?,
        isCache: Boolean
    ): MediaSource {
        val contentUri = Uri.parse(uri)
        if ("rtmp" == contentUri.scheme) {
            return ProgressiveMediaSource.Factory(RtmpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(contentUri))
        } else if ("rtsp" == contentUri.scheme) {
            return RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(contentUri))
        }
        val contentType = inferContentType(uri)
        val factory: DataSource.Factory = if (isCache) {
            cacheDataSourceFactory
        } else {
            dataSourceFactory
        }
        if (mHttpDataSourceFactory != null) {
            setHeaders(headers)
        }
        return when (contentType) {
            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(contentUri))
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(contentUri))
            C.CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(contentUri))
            else -> ProgressiveMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(contentUri))
        }
    }

    private fun inferContentType(fileName: String): Int {
        var fileName = fileName
        fileName = fileName.lowercase(Locale.getDefault())
        return if (fileName.contains(".mpd")) {
            C.CONTENT_TYPE_DASH
        } else if (fileName.contains(".m3u8")) {
            C.CONTENT_TYPE_HLS
        } else {
            C.CONTENT_TYPE_OTHER
        }
    }

    private val cacheDataSourceFactory: DataSource.Factory
        private get() {
            if (mCache == null) {
                mCache = newCache()
            }
            return CacheDataSource.Factory()
                .setCache(mCache!!)
                .setUpstreamDataSourceFactory(dataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }

    private fun newCache(): Cache {
        return SimpleCache(
            File(mAppContext.externalCacheDir, "exo-video-cache"),  //缓存目录
            LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024),  //缓存大小，默认512M，使用LRU算法实现
            StandaloneDatabaseProvider(mAppContext)
        )
    }

    /**
     * Returns a new DataSource factory.
     *
     * @return A new DataSource factory.
     */
    private val dataSourceFactory: DataSource.Factory
        private get() = DefaultDataSource.Factory(mAppContext, httpDataSourceFactory)

    /**
     * Returns a new HttpDataSource factory.
     *
     * @return A new HttpDataSource factory.
     */
    private val httpDataSourceFactory: DataSource.Factory
        private get() {
            if (mHttpDataSourceFactory == null) {
                mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent(mUserAgent)
                    .setAllowCrossProtocolRedirects(true)
            }
            return mHttpDataSourceFactory!!
        }

    private fun setHeaders(headers: MutableMap<String, String>?) {
        if (headers != null && headers.size > 0) {
            //如果发现用户通过header传递了UA，则强行将HttpDataSourceFactory里面的userAgent字段替换成用户的
            if (headers.containsKey("User-Agent")) {
                val value = headers.remove("User-Agent")
                if (!TextUtils.isEmpty(value)) {
                    try {
                        val userAgentField =
                            mHttpDataSourceFactory!!.javaClass.getDeclaredField("userAgent")
                        userAgentField.isAccessible = true
                        userAgentField[mHttpDataSourceFactory] = value
                    } catch (e: Exception) {
                        //ignore
                    }
                }
            }
            mHttpDataSourceFactory!!.setDefaultRequestProperties(headers)
        }
    }

    fun setCache(cache: Cache?) {
        mCache = cache
    }

    companion object {
        @Volatile
        private var sInstance: ExoMediaSourceHelper? = null
        @JvmStatic
        fun getInstance(context: Context): ExoMediaSourceHelper {
            if (sInstance == null) {
                synchronized(ExoMediaSourceHelper::class.java) {
                    if (sInstance == null) {
                        sInstance = ExoMediaSourceHelper(context)
                    }
                }
            }
            return sInstance!!
        }
    }
}