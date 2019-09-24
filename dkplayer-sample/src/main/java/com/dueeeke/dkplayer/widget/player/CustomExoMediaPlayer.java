package com.dueeeke.dkplayer.widget.player;


import android.content.Context;

import com.dueeeke.videoplayer.exo.ExoMediaPlayer;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.Map;

/**
 * 自定义ExoMediaPlayer，目前扩展了诸如边播边存，以及可以直接设置Exo自己的MediaSource。
 */
public class CustomExoMediaPlayer extends ExoMediaPlayer {

    private Cache mCache;

    public CustomExoMediaPlayer(Context context) {
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
            mMediaSourceHelper.setDataSourceFactory(getCacheDataSourceFactory());
        }

        super.setDataSource(path, headers);
    }

    private DataSource.Factory getCacheDataSourceFactory() {
        return new CacheDataSourceFactory(
                mCache,
                mMediaSourceHelper.getDataSourceFactory(),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    public Cache getCache() {
        return mCache;
    }

    public void setCache(Cache cache) {
        mCache = cache;
    }

    public void setDataSource(MediaSource dataSource) {
        mMediaSource = dataSource;
    }
}
