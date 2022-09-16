package xyz.doikki.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;

import java.util.Map;

import xyz.doikki.dkplayer.widget.player.CustomExoMediaPlayer;
import xyz.doikki.videoplayer.exo.ExoMediaSourceHelper;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.MediaPlayerFactory;

public class ExoVideoView extends VideoView {

    private MediaSource mMediaSource;

    private boolean mIsCacheEnabled;

    private LoadControl mLoadControl;
    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;

    private final ExoMediaSourceHelper mHelper;

    public ExoVideoView(Context context) {
        super(context);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        //由于传递了泛型，必须将CustomExoMediaPlayer设置进来，否者报错
        setPlayerFactory(new MediaPlayerFactory<CustomExoMediaPlayer>() {
            @Override
            public CustomExoMediaPlayer create(Context context) {
                return new CustomExoMediaPlayer(context);
            }
        });
        mHelper = ExoMediaSourceHelper.getInstance(getContext());
    }

    private CustomExoMediaPlayer mediaPlayer(){
        return (CustomExoMediaPlayer)mMediaPlayer;
    }
    @Override
    protected void setInitOptions() {
        super.setInitOptions();
        mediaPlayer().setLoadControl(mLoadControl);
        mediaPlayer().setRenderersFactory(mRenderersFactory);
        mediaPlayer().setTrackSelector(mTrackSelector);
    }

    @Override
    protected boolean prepareDataSource() {
        if (mMediaSource != null) {
            mediaPlayer().setDataSource(mMediaSource);
            return true;
        }
        return false;
    }

    /**
     * 设置ExoPlayer的MediaSource
     */
    public void setMediaSource(MediaSource mediaSource) {
        mMediaSource = mediaSource;
    }

    @Override
    public void setUrl(String url, Map<String, String> headers) {
        mMediaSource = mHelper.getMediaSource(url, headers, mIsCacheEnabled);
    }

    /**
     * 是否打开缓存
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
