package xyz.doikki.videoplayer.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.VideoViewManager;

/**
 * 经查看{@link TextureView}源码，发现在{@link #onDetachedFromWindow()}的时候，
 * 会先回调{@link TextureView.SurfaceTextureListener#onSurfaceTextureDestroyed}并释放{@link SurfaceTexture},
 * 在需要{@link SurfaceTexture}时会重新构建并回调{@link TextureView.SurfaceTextureListener#onSurfaceTextureAvailable}
 */
@SuppressLint("ViewConstructor")
public class TextureViewRender extends TextureView implements Render, TextureView.SurfaceTextureListener {

    private final RenderLayoutMeasure mMeasureHelper;

    @Nullable
    private WeakReference<AVPlayer> mPlayerRef;

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private SurfaceListener mSurfaceListener;

    public TextureViewRender(Context context) {
        super(context);
    }

    public TextureViewRender(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextureViewRender(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mMeasureHelper = new RenderLayoutMeasure();
        setSurfaceTextureListener(this);
    }

    /**
     * 是否开启了渲染优化
     *
     * @return
     */
    private boolean isEnableRenderOptimization() {
        return VideoViewManager.isTextureRenderOptimization();
    }

    /**
     * 绑定播放器
     *
     * @param player
     */
    @Override
    public void attachToPlayer(@NonNull AVPlayer player) {
        try {
            if (mPlayerRef != null) {
                AVPlayer previousPlayer = mPlayerRef.get();
                if (previousPlayer != null && previousPlayer != player) {
                    //如果之前已绑定过播放器，并且与当前设置的播放器不相同，则将surface与之前的播放器解除绑定
                    previousPlayer.setSurface(null);
                    mPlayerRef = null;
                }
            }
            mPlayerRef = new WeakReference<>(player);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    /*************START Render 实现逻辑***********************/

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setSurfaceListener(SurfaceListener listener) {
        this.mSurfaceListener = listener;
    }

    @Override
    public void screenshot(boolean highQuality, @NonNull ScreenShotCallback callback) {
        if (!isAvailable()) {
            callback.onScreenShotResult(null);
            return;
        }

        if (highQuality) {
            callback.onScreenShotResult(getBitmap());
        } else {
            callback.onScreenShotResult(getBitmap(Utils.createShotBitmap(this, false)));
        }
    }

    /*************END Render 实现逻辑***********************/

    /*************START TextureView.SurfaceTextureListener 实现逻辑***********************/

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (isEnableRenderOptimization()) {
            //开启渲染优化
            if (mSurfaceTexture == null) {
                mSurfaceTexture = surfaceTexture;
                mSurface = new Surface(surfaceTexture);
                bindSurfaceToMediaPlayer(mSurface);
            } else {
                //在开启优化的情况下，使用最开始的那个渲染器
                setSurfaceTexture(mSurfaceTexture);
            }
        } else {
            mSurface = new Surface(surfaceTexture);
            bindSurfaceToMediaPlayer(mSurface);
        }
        notifySurfaceAvailable(mSurface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //清空释放
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceDestroyed(mSurface);
        }

        if (isEnableRenderOptimization()) {
            //如果开启了渲染优化，那mSurfaceTexture通常情况不可能为null（在onSurfaceTextureAvailable初次回调的时候被赋值了），
            // 所以这里通常返回的是false，返回值false会告诉父类不要释放SurfaceTexture
            return (mSurfaceTexture == null);
        } else {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceSizeChanged(mSurface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceUpdated(mSurface);
        }
    }

    private void bindSurfaceToMediaPlayer(Surface surface) {
        if (mPlayerRef == null)
            return;
        AVPlayer player = mPlayerRef.get();
        if (player == null)
            return;
        player.setSurface(surface);
    }

    private void notifySurfaceAvailable(Surface surface, int width, int height) {
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceAvailable(surface);
        }
    }

    /*************END TextureView.SurfaceTextureListener 实现逻辑***********************/

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotationDegree(degree);
        setRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        mMeasureHelper.setAspectRatioType(scaleType);
        requestLayout();
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
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }


}