package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.dkplayer.util.VideoCacheManager;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.io.File;

public class CacheIjkVideoView extends IjkVideoView {

    protected HttpProxyCacheServer mCacheServer;
    protected int mBufferedPercentage;
    protected boolean mIsCacheEnabled = true; //默认打开缓存

    public CacheIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public CacheIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CacheIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void startPrepare(boolean needReset) {
        if (TextUtils.isEmpty(mCurrentUrl) && mAssetFileDescriptor == null) return;
        if (needReset) mMediaPlayer.reset();
        if (mAssetFileDescriptor != null) {
            mMediaPlayer.setDataSource(mAssetFileDescriptor);
        } else if (mIsCacheEnabled && !mCurrentUrl.startsWith("file://")) { //本地文件不能缓存
            mCacheServer = getCacheServer();
            String proxyPath = mCacheServer.getProxyUrl(mCurrentUrl);
            mCacheServer.registerCacheListener(cacheListener, mCurrentUrl);
            if (mCacheServer.isCached(mCurrentUrl)) {
                mBufferedPercentage = 100;
            }
            mMediaPlayer.setDataSource(proxyPath, mHeaders);
        } else {
            mMediaPlayer.setDataSource(mCurrentUrl, mHeaders);
        }
        mMediaPlayer.prepareAsync();
        setPlayState(STATE_PREPARING);
        setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : PLAYER_NORMAL);
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
            cacheListener = null;
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
