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
import com.dueeeke.videoplayer.render.IRenderView;

/**
 * TikTok专用RenderView，横屏视频默认显示，竖屏视频居中裁剪
 */
@SuppressLint("ViewConstructor")
public class TikTokRenderView extends TextureView implements IRenderView, TextureView.SurfaceTextureListener {
    private TikTokMeasureHelper mMeasureHelper;
    private SurfaceTexture mSurfaceTexture;

    @Nullable
    private AbstractPlayer mMediaPlayer;
    private Surface mSurface;

    public TikTokRenderView(Context context) {
        super(context);
    }

    {
        mMeasureHelper = new TikTokMeasureHelper();
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


    private static class TikTokMeasureHelper {

        private int mVideoWidth;

        private int mVideoHeight;

        private int mVideoRotationDegree;

        public void setVideoRotation(int videoRotationDegree) {
            mVideoRotationDegree = videoRotationDegree;
        }

        public void setVideoSize(int width, int height) {
            mVideoWidth = width;
            mVideoHeight = height;
        }


        public int[] doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) { // 软解码时处理旋转信息，交换宽高
                widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
                heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
                widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            }

            int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
            int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (mVideoHeight > mVideoWidth) { //竖屏视频
                    if (mVideoWidth * height > width * mVideoHeight) {
                        width = height * mVideoWidth / mVideoHeight;
                    } else {
                        height = width * mVideoHeight / mVideoWidth;
                    }
                } else { //横屏视频
                    int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
                    int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
                    int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

                    if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                        // the size is fixed
                        width = widthSpecSize;
                        height = heightSpecSize;

                        // for compatibility, we adjust size based on aspect ratio
                        if (mVideoWidth * height < width * mVideoHeight) {
                            //Log.i("@@@", "image too wide, correcting");
                            width = height * mVideoWidth / mVideoHeight;
                        } else if (mVideoWidth * height > width * mVideoHeight) {
                            //Log.i("@@@", "image too tall, correcting");
                            height = width * mVideoHeight / mVideoWidth;
                        }
                    } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                        // only the width is fixed, adjust the height to match aspect ratio if possible
                        width = widthSpecSize;
                        height = width * mVideoHeight / mVideoWidth;
                        if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            height = heightSpecSize;
                        }
                    } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                        // only the height is fixed, adjust the width to match aspect ratio if possible
                        height = heightSpecSize;
                        width = height * mVideoWidth / mVideoHeight;
                        if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            width = widthSpecSize;
                        }
                    } else {
                        // neither the width nor the height are fixed, try to use actual video size
                        width = mVideoWidth;
                        height = mVideoHeight;
                        if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // too tall, decrease both width and height
                            height = heightSpecSize;
                            width = height * mVideoWidth / mVideoHeight;
                        }
                        if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // too wide, decrease both width and height
                            width = widthSpecSize;
                            height = width * mVideoHeight / mVideoWidth;
                        }
                    }

                }
            }
            return new int[]{width, height};
        }
    }
}