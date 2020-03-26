package com.dueeeke.dkplayer.widget.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.render.IRenderView;
import com.dueeeke.videoplayer.render.MeasureHelper;

/**
 * TikTok专用RenderView，横屏视频默认显示，竖屏视频居中裁剪
 */
@SuppressLint("ViewConstructor")
public class TikTokRenderView extends TextureView implements IRenderView, TextureView.SurfaceTextureListener {
    private MeasureHelper mMeasureHelper;
    private SurfaceTexture mSurfaceTexture;

    @Nullable
    private AbstractPlayer mMediaPlayer;
    private Surface mSurface;

    public TikTokRenderView(Context context) {
        super(context);
    }

    {
        mMeasureHelper = new MeasureHelper();
        setSurfaceTextureListener(this);
    }

    @Override
    public void attachToPlayer(@NonNull AbstractPlayer player) {
        this.mMediaPlayer = player;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            if (videoHeight > videoWidth) {
                //竖屏视频
                mMeasureHelper.setScreenScale(VideoView.SCREEN_SCALE_CENTER_CROP);
            } else {
                //横屏视频
                mMeasureHelper.setScreenScale(VideoView.SCREEN_SCALE_DEFAULT);
            }
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        // not support
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Bitmap doScreenShot() {
        return getBitmap();
    }

    @Override
    public void release() {
        if (mSurface != null)
            mSurface.release();

        if (mSurfaceTexture != null)
            mSurfaceTexture.release();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture != null) {
            setSurfaceTexture(mSurfaceTexture);
        } else {
            mSurfaceTexture = surfaceTexture;
            mSurface = new Surface(surfaceTexture);
            if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(mSurface);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}