package com.dueeeke.dkplayer.util;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 抖音预加载工具，实现AndroidVideoCache实现
 */
public class PreloadManager {

    private static PreloadManager sPreloadManager;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(4);

    /**
     * 保存正在预加载的HttpURLConnection，用来取消请求
     */
    private  HashMap<String, HttpURLConnection> mConnections = new HashMap<>();

    private  HttpProxyCacheServer mHttpProxyCacheServer;

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
     * 获取HttpProxyCacheServer的代理地址
     */
    public String getProxyUrl(String rawUrl) {
        return mHttpProxyCacheServer.getProxyUrl(rawUrl);
    }


    /**
     * 开始预加载
     * @param rawUrl 原始视频地址
     */
    public void startPreload(String rawUrl) {
        String proxyUrl = getProxyUrl(rawUrl);
        //如果没有缓存过，加入线程池进行预加载操作
        if (!mHttpProxyCacheServer.isCached(rawUrl)) {
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    start(proxyUrl, rawUrl);
                }
            });
        }
    }

    /**
     * 真正开始预加载
     * @param proxyUrl 代理地址
     * @param rawUrl 原始地址
     */
    private void start(String proxyUrl, String rawUrl) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(proxyUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            mConnections.put(rawUrl, urlConnection);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int length;
            int read = -1;
            byte[] bytes = new byte[1024];
            while ((length = in.read(bytes)) != -1) {
                read += length;
                if (read >= 500 * 1024) {//预加载500KB
                    //预加载完成，取消请求，并且将其移除
                    urlConnection.disconnect();
                    mConnections.remove(rawUrl);
                    break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

    /**
     * 通过原始地址取消预加载
     * @param rawUrl 原始地址
     */
    public void cancelPreloadByUrl(String rawUrl) {
        HttpURLConnection connection = mConnections.get(rawUrl);
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 取消所有的预加载
     */
    public void cancelAll() {
        Set<Map.Entry<String, HttpURLConnection>> entries = mConnections.entrySet();
        for (Map.Entry<String, HttpURLConnection> entry: entries){
            String key = entry.getKey();
            cancelPreloadByUrl(key);
        }
    }
}
