package com.dueeeke.dkplayer.widget.player;


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
    private File mCacheDir;
    private long mMaxCacheSize;

    public void setDataSource(String path, Map<String, String> headers, boolean isCache) {
        if (isCache) {
            mMediaSourceHelper.setDataSourceFactory(getCacheDataSourceFactory());
        }
        super.setDataSource(path, headers);
    }

    private DataSource.Factory getCacheDataSourceFactory() {
        if (mCache == null) {
            mCache = getCache();
        }
        return new CacheDataSourceFactory(
                mCache,
                mMediaSourceHelper.getDataSourceFactory(),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    private Cache getCache() {
        if (mCacheDir == null) {
            mCacheDir = new File(mAppContext.getExternalCacheDir(), "exo-video-cache");
        }
        if (mMaxCacheSize <= 0) {
            mMaxCacheSize = 512 * 1024 * 1024;//512M
        }
        return new SimpleCache(
                mCacheDir,//缓存目录
                new LeastRecentlyUsedCacheEvictor(mMaxCacheSize),//缓存大小，使用LRU算法实现
                new ExoDatabaseProvider(mAppContext));
    }

    public boolean setCacheDir(File dir) {
        if (!SimpleCache.isCacheFolderLocked(dir)) {
            mCacheDir = dir;
            return true;
        }
        return false;
    }

    public void setMaxCacheSize(long bytes) {
        mMaxCacheSize = bytes;
    }

    public void setDataSource(MediaSource dataSource) {
        mMediaSource = dataSource;
    }

    @Override
    public void release() {
        super.release();
        if (mCache != null) {
            mCache.release();
        }
    }
}
