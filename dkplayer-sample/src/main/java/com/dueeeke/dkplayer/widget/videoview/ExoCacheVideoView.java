package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.widget.player.CacheExoMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.google.android.exoplayer2.upstream.cache.Cache;

/**
 * ExoPlayer的边播边存功能，支持m3u8
 */
public class ExoCacheVideoView extends VideoView<CacheExoMediaPlayer> {

    private boolean mIsCacheEnabled;

    public ExoCacheVideoView(Context context) {
        this(context, null);
    }

    public ExoCacheVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoCacheVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPlayerFactory(new PlayerFactory<CacheExoMediaPlayer>() {
            @Override
            public CacheExoMediaPlayer createPlayer() {
                return new CacheExoMediaPlayer(context);
            }
        });
    }


    @Override
    protected boolean prepareDataSource() {
        if (mIsCacheEnabled) {
            mMediaPlayer.setDataSource(mUrl, mHeaders, true);
            return true;
        }
        return super.prepareDataSource();
    }

    public void setCache(Cache cache) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setCache(cache);
        }
    }

    /**
     * 是否打开缓存，默认打开
     */
    public void setCacheEnabled(boolean isEnabled) {
        mIsCacheEnabled = isEnabled;
    }
}
