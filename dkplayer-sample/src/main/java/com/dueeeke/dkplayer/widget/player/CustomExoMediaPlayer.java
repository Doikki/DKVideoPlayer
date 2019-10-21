package com.dueeeke.dkplayer.widget.player;


import com.dueeeke.videoplayer.exo.ExoMediaPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;

import java.util.Map;

/**
 * 自定义ExoMediaPlayer，目前扩展了诸如边播边存，以及可以直接设置Exo自己的MediaSource。
 */
public class CustomExoMediaPlayer extends ExoMediaPlayer {

    private Cache mCache;

    public void setDataSource(String path, Map<String, String> headers, boolean isCache) {
        if (isCache) {
            mMediaSourceHelper.setDataSourceFactory(getCacheDataSourceFactory());
        }
        super.setDataSource(path, headers);
    }

    private DataSource.Factory getCacheDataSourceFactory() {
        if (mCache == null) {
            mCache = ExoVideoCacheManager.getCache(mAppContext);
        }
        return new CacheDataSourceFactory(
                mCache,
                mMediaSourceHelper.getDataSourceFactory(),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    public void setDataSource(MediaSource dataSource) {
        mMediaSource = dataSource;
    }
}
