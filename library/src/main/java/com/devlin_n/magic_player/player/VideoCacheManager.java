package com.devlin_n.magic_player.player;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.devlin_n.magic_player.util.StorageUtil;

import java.io.File;

public class VideoCacheManager {

    private static HttpProxyCacheServer sharedProxy;

    private VideoCacheManager() {
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        return sharedProxy == null ? (sharedProxy = newProxy(context)) : sharedProxy;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .build();
    }


    /**
     * 删除所有缓存文件
     * @return 返回缓存是否删除成功
     */
    public static boolean clearAllCache(Context context) {
        File cacheDirectory = StorageUtil.getIndividualCacheDirectory(context);
        return StorageUtil.deleteFiles(cacheDirectory);
    }

    /**
     * 删除url对应默认缓存文件
     * @return 返回缓存是否删除成功
     */
    public static boolean clearDefaultCache(Context context, String url) {
        Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        String name = md5FileNameGenerator.generate(url);
        String pathTmp = StorageUtil.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name + ".download";
        String path = StorageUtil.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name;
        return StorageUtil.deleteFile(pathTmp) &&
                StorageUtil.deleteFile(path);

    }
}