package com.dueeeke.dkplayer.widget.player;

import android.content.Context;

import com.dueeeke.videoplayer.exo.ExoMediaPlayer;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.Map;

/**
 * 包含边播边存功能的ExoMediaPlayer, 支持m3u8的缓存
 */
public class CacheExoMediaPlayer extends ExoMediaPlayer {

    private Cache mCache;

    public CacheExoMediaPlayer(Context context) {
        super(context);
    }

    public void setDataSource(String path, Map<String, String> headers, boolean isCache) {
        if (isCache) {
            if (mCache == null) {
                mCache = new SimpleCache(
                        new File(mAppContext.getExternalCacheDir(), "exo-video-cache"),//缓存目录
                        new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024),//缓存大小
                        new ExoDatabaseProvider(mAppContext));
            }
            mDataSourceFactory = getCacheDataSourceFactory();
        }

        super.setDataSource(path, headers);
    }

    private DataSource.Factory getCacheDataSourceFactory() {
        return new CacheDataSourceFactory(
                mCache,
                mDataSourceFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    public Cache getCache() {
        return mCache;
    }

    public void setCache(Cache cache) {
        mCache = cache;
    }
}
