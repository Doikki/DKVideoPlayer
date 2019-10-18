package com.dueeeke.dkplayer.util;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 抖音预加载工具，实现AndroidVideoCache实现
 */
public class PreloadManager {

    private static PreloadManager sPreloadManager;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(4);

    /**
     * 保存正在预加载的{@link PreloadTask}
     */
    private HashMap<String, PreloadTask> mPreloadTasks = new HashMap<>();

    private boolean mIsStartPreload = true;

    private HttpProxyCacheServer mHttpProxyCacheServer;

    private PreloadManager(Context context) {
        mHttpProxyCacheServer = ProxyVideoCacheManager.getProxy(context);
    }

    public static PreloadManager getInstance(Context context) {
        if (sPreloadManager == null) {
            synchronized (PreloadManager.class) {
                if (sPreloadManager == null) {
                    sPreloadManager = new PreloadManager(context.getApplicationContext());
                }
            }
        }
        return sPreloadManager;
    }

    /**
     * 开始预加载
     *
     * @param rawUrl 原始视频地址
     */
    public void startPreload(String rawUrl, int position) {
        PreloadTask task = new PreloadTask();
        task.mRawUrl = rawUrl;
        task.mPosition = position;
        task.mCacheServer = mHttpProxyCacheServer;
        mPreloadTasks.put(rawUrl, task);

        if (mIsStartPreload)  {
            //开始预加载
            for (Map.Entry<String, PreloadTask> next : mPreloadTasks.entrySet()) {
                PreloadTask preloadTask = next.getValue();
                preloadTask.executeOn(mExecutorService);
            }
        }
    }

    /**
     * 暂停预加载
     */
    public void pausePreload(int position) {
        mIsStartPreload = false;
        cancelPreloadByPosition(position);
    }

    /**
     * 恢复预加载
     */
    public void resumePreload(int position) {
        mIsStartPreload = true;
        startPreloadByPosition(position);
    }

    /**
     * 取消掉在position之上的PreloadTask，并且从{@link #mPreloadTasks}移除
     * @param position 当前滑到的位置
     */
    private void cancelPreloadByPosition(int position) {
        Iterator<Map.Entry<String, PreloadTask>> iterator = mPreloadTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PreloadTask> next = iterator.next();
            PreloadTask task = next.getValue();
            if (task.mPosition < position) {
                task.cancel();
                iterator.remove();
            }
        }
    }

    /**
     * 开始在position之下的PreloadTask
     * @param position 当前滑到的位置
     */
    private void startPreloadByPosition(int position) {
        for (Map.Entry<String, PreloadTask> next : mPreloadTasks.entrySet()) {
            PreloadTask task = next.getValue();
            if (task.mPosition > position) {
                task.executeOn(mExecutorService);
            }
        }
    }

    /**
     * 通过原始地址取消预加载
     * @param rawUrl 原始地址
     */
    private String cancelPreloadByUrl(String rawUrl) {
        Iterator<Map.Entry<String, PreloadTask>> iterator = mPreloadTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PreloadTask> next = iterator.next();
            if (next.getKey().equals(rawUrl)) {
                PreloadTask task = next.getValue();
                iterator.remove();
                return task.cancel();
            }
        }
        return null;
    }

    /**
     * 取消所有的预加载
     */
    public void cancelAll() {
        Iterator<Map.Entry<String, PreloadTask>> iterator = mPreloadTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PreloadTask> next = iterator.next();
            PreloadTask task = next.getValue();
            task.cancel();
            iterator.remove();
        }
    }

    /**
     * 获取代理地址，获取不到就返回原始地址
     */
    public String getPlayUrl(String rawUrl) {
        String proxyUrl = cancelPreloadByUrl(rawUrl);
        if (proxyUrl == null) {
            return mHttpProxyCacheServer.getProxyUrl(rawUrl);
        } else {
            return rawUrl;
        }
    }
}
