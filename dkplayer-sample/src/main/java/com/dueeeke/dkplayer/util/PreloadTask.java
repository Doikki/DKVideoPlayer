package com.dueeeke.dkplayer.util;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.BufferedInputStream;
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
     * 是否执行过
     */
    private boolean mIsExecuted;

    @Override
    public void run() {

        //获取HttpProxyCacheServer的代理地址
        mProxyUrl = mCacheServer.getProxyUrl(mRawUrl);
        if (!mCacheServer.isCached(mRawUrl) && !mIsCanceled) {
            start();
        }
    }

    /**
     * 开始预加载
     */
    private void start() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mProxyUrl);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            int length;
            int read = -1;
            byte[] bytes = new byte[1024];
            while ((length = in.read(bytes)) != -1) {
                read += length;
                //预加载完成或者取消请求
                if (mIsCanceled || read >= 500 * 1024) {
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
        mIsCanceled = true;
        return mProxyUrl;
    }
}
