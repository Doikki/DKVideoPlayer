package com.dueeeke.dkplayer.util;

import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.dkplayer.app.MyApplication;
import com.dueeeke.videoplayer.util.L;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class PreloadTask implements Runnable {

    /**
     * 原始地址
     */
    public String mRawUrl;

    /**
     * 列表中的位置
     */
    public int mPosition;

    /**
     * VideoCache服务器
     */
    public HttpProxyCacheServer mCacheServer;

    /**
     * 代理地址
     */
    private String mProxyUrl;

    /**
     * 是否被取消
     */
    private boolean mIsCanceled;

    /**
     * 是否正在预加载
     */
    private boolean mIsExecuted;

    /**
     * 预加载的大小，每个视频预加载512KB
     */
    private static final int PRELOAD_LENGTH = 512 * 1024;

    @Override
    public void run() {
        if (!isPreloaded() && !mIsCanceled) {
            start();
        }
        mIsExecuted = false;
        mIsCanceled = false;
    }

    /**
     * 判断是否已经预加载
     */
    private boolean isPreloaded() {
        String cacheFilePath = ProxyVideoCacheManager.getCacheFilePath(MyApplication.getInstance(), mRawUrl);
        File cacheFile = new File(cacheFilePath);
        if (cacheFile.exists()) {
            if (cacheFile.length() >= 1024) {
                return true;
            } else {
                //这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete();
                return false;
            }
        }

        String tempCacheFilePath = ProxyVideoCacheManager.getTempCacheFilePath(MyApplication.getInstance(), mRawUrl);
        File tempCacheFile = new File(tempCacheFilePath);
        if (tempCacheFile.exists()) {
            return tempCacheFile.length() >= PRELOAD_LENGTH;
        }

        return false;
    }

    /**
     * 开始预加载
     */
    private void start() {
        L.i("开始预加载：" + mPosition);
        HttpURLConnection connection = null;
        try {
            //获取HttpProxyCacheServer的代理地址
            mProxyUrl = mCacheServer.getProxyUrl(mRawUrl);
            if (mIsCanceled) return;
            URL url = new URL(mProxyUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            int length;
            int read = -1;
            byte[] bytes = new byte[8 * 1024];
            while ((length = in.read(bytes)) != -1) {
                read += length;
                //预加载完成或者取消预加载
                if (mIsCanceled || read >= PRELOAD_LENGTH) {
                    L.i("结束预加载：" + mPosition);
                    connection.disconnect();
                    break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void executeOn(ExecutorService executorService) {
        if (mIsExecuted) return;
        mIsExecuted = true;
        executorService.submit(this);
    }

    public String cancel() {
        if (mIsExecuted) {
            mIsCanceled = true;
        }
        return mProxyUrl;
    }
}
