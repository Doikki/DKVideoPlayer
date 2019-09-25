package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.widget.player.CustomExoMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.cache.Cache;

public class ExoVideoView extends VideoView<CustomExoMediaPlayer> {

    private MediaSource mMediaSource;

    private boolean mIsCacheEnabled;
    private LoadControl mLoadControl;
    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;

    private Cache mCache;

    public ExoVideoView(Context context) {
        this(context, null);
    }

    public ExoVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //必须设置，否者报错
        setPlayerFactory(new PlayerFactory<CustomExoMediaPlayer>() {
            @Override
            public CustomExoMediaPlayer createPlayer() {
                return new CustomExoMediaPlayer();
            }
        });
    }

    @Override
    protected void setInitOptions() {
        super.setInitOptions();
        mMediaPlayer.setLoadControl(mLoadControl);
        mMediaPlayer.setRenderersFactory(mRenderersFactory);
        mMediaPlayer.setTrackSelector(mTrackSelector);
        mMediaPlayer.setCache(mCache);
    }

    @Override
    protected boolean prepareDataSource() {
        if (mIsCacheEnabled) {
            mMediaPlayer.setDataSource(mUrl, mHeaders, true);
            return true;
        } else if (mMediaSource != null) {
            mMediaPlayer.setDataSource(mMediaSource);
            return true;
        }
        return super.prepareDataSource();
    }

    /**
     * 设置ExoPlayer的MediaSource
     */
    public void setMediaSource(MediaSource mediaSource) {
        this.mMediaSource = mediaSource;
    }

    /**
     * 缓存控制
     */
    public void setCache(Cache cache) {
        mCache = cache;
    }

    /**
     * 是否打开缓存，默认打开
     */
    public void setCacheEnabled(boolean isEnabled) {
        mIsCacheEnabled = isEnabled;
    }

    public void setLoadControl(LoadControl loadControl) {
        mLoadControl = loadControl;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        mRenderersFactory = renderersFactory;
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        mTrackSelector = trackSelector;
    }
}
