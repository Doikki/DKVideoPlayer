package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.dkplayer.util.VideoCacheManager;
import com.dueeeke.videoplayer.player.VideoView;

import java.io.File;

public class CacheVideoView extends VideoView {

    protected HttpProxyCacheServer mCacheServer;
    protected int mBufferedPercentage;
    protected boolean mIsCacheEnabled = true; //默认打开缓存

    public CacheVideoView(@NonNull Context context) {
        super(context);
    }

    public CacheVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CacheVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean prepareDataSource() {
        if (mIsCacheEnabled && !isLocalDataSource()) { //本地数据源不能缓存
            mCacheServer = getCacheServer();
            String proxyPath = mCacheServer.getProxyUrl(mUrl);
            mCacheServer.registerCacheListener(cacheListener, mUrl);
            if (mCacheServer.isCached(mUrl)) {
                mBufferedPercentage = 100;
            }
            mMediaPlayer.setDataSource(proxyPath, mHeaders);
            return true;
        }
        return super.prepareDataSource();
    }

    public HttpProxyCacheServer getCacheServer() {
        return VideoCacheManager.getProxy(getContext().getApplicationContext());
    }

    /**
     * 是否打开缓存，默认打开
     */
    public void setCacheEnabled(boolean isEnabled) {
        mIsCacheEnabled = isEnabled;
    }

    /**
     * 开启缓存后，返回是缓存的进度
     */
    @Override
    public int getBufferedPercentage() {
        return mIsCacheEnabled ? mBufferedPercentage : super.getBufferedPercentage();
    }

    @Override
    public void release() {
        super.release();
        if (mCacheServer != null) {
            mCacheServer.unregisterCacheListener(cacheListener);
            mCacheServer = null;
        }
    }

    /**
     * 缓存监听
     */
    private CacheListener cacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            mBufferedPercentage = percentsAvailable;
        }
    };
}
