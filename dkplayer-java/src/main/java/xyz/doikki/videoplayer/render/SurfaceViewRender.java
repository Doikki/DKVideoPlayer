package xyz.doikki.videoplayer.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.PixelCopy;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.util.L;

public class SurfaceViewRender extends SurfaceView implements Render, SurfaceHolder.Callback {

    private final RenderLayoutMeasure mMeasureHelper;

    private AVPlayer mMediaPlayer;

    private SurfaceListener mSurfaceListener;

    public SurfaceViewRender(Context context) {
        super(context);
    }

    public SurfaceViewRender(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceViewRender(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mMeasureHelper = new RenderLayoutMeasure();
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void attachToPlayer(@NonNull AVPlayer player) {
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
    public void setSurfaceListener(SurfaceListener listener) {
        this.mSurfaceListener = listener;
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotationDegree(degree);
        setRotation(degree);
    }

    @Override
    public void setAspectRatioType(int scaleType) {
        mMeasureHelper.setAspectRatioType(scaleType);
        requestLayout();
    }

    @Override
    public void screenshot(boolean highQuality, @NonNull ScreenShotCallback callback) {
        if (Build.VERSION.SDK_INT >= 24) {
            Bitmap bmp = Utils.createShotBitmap(this, highQuality);
            HandlerThread handlerThread = new HandlerThread("PixelCopier");
            handlerThread.start();
            PixelCopy.request(this, bmp, copyResult -> {
                try {
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback.onScreenShotResult(bmp);
                    }
                    handlerThread.quitSafely();
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (bmp != null && !bmp.isRecycled())
                        bmp.recycle();
                    callback.onScreenShotResult(null);
                }

            }, new Handler());
        } else {
            callback.onScreenShotResult(null);
            L.w("SurfaceView not support screenshot when Build.VERSION.SDK_INT < Build.VERSION_CODES.N");
        }
    }

    @Override
    public View getView() {
        return this;
    }


    @Override
    public void release() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceAvailable(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(holder);
        }

        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceSizeChanged(holder.getSurface(), width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceDestroyed(holder.getSurface());
        }
    }
}