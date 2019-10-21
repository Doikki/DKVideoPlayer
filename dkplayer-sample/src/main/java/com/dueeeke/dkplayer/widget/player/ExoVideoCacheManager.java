package com.dueeeke.dkplayer.widget.player;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class ExoVideoCacheManager {

    private static Cache sCache;

    private ExoVideoCacheManager() {
    }

    public static Cache getCache(Context context) {
        return sCache == null ? (sCache = newCache(context)) : sCache;
    }

    private static Cache newCache(Context context) {
        return new SimpleCache(
                new File(context.getExternalCacheDir(), "exo-video-cache"),//缓存目录
                new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024),//缓存大小，默认512M，使用LRU算法实现
                new ExoDatabaseProvider(context));
    }
}