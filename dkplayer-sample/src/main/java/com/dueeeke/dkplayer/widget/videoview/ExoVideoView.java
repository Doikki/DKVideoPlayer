package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.widget.player.CustomExoMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;

import java.io.File;

public class ExoVideoView extends VideoView<CustomExoMediaPlayer> {

    private MediaSource mMediaSource;

    private boolean mIsCacheEnabled;
    private File mCacheDir;
    private long mMaxCacheSize;

    private LoadControl mLoadControl;
    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;

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
        if (mIsCacheEnabled) {
            boolean isSuccess = mMediaPlayer.setCacheDir(mCacheDir);
            if (!isSuccess) {
                L.e("Current dir is locked.");
            }
            mMediaPlayer.setMaxCacheSize(mMaxCacheSize);
        }
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
     * 是否打开缓存
     */
    public void setCacheEnabled(boolean isEnabled) {
        mIsCacheEnabled = isEnabled;
    }

    public void setCacheDir(File cacheDir) {
        mCacheDir = cacheDir;
    }

    public void setMaxCacheSize(long maxCacheSize) {
        mMaxCacheSize = maxCacheSize;
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
