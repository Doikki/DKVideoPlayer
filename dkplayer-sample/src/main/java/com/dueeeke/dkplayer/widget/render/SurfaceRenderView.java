package com.dueeeke.dkplayer.widget.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.render.IRenderView;
import com.dueeeke.videoplayer.render.MeasureHelper;

@SuppressLint("ViewConstructor")
public class SurfaceRenderView extends SurfaceView implements IRenderView, SurfaceHolder.Callback {
    private MeasureHelper mMeasureHelper;

    private AbstractPlayer mMediaPlayer;

    {
        mMeasureHelper = new MeasureHelper();
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    public SurfaceRenderView(Context context) {
        super(context);
    }

    @Override
    public void attachToPlayer(@NonNull AbstractPlayer player) {
        this.mMediaPlayer = player;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
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
        mMeasureHelper.setScreenScale(scaleType);
        requestLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Bitmap doScreenShot() {
        return null;
    }

    @Override
    public void release() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}