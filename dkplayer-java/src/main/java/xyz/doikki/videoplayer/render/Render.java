package xyz.doikki.videoplayer.render;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videoplayer.AVPlayer;

public interface Render extends RenderFunction {

    /**
     * 获取真实的RenderView:用于挂在view tree上
     */
    View getView();

    /**
     * 关联到播放器中
     */
    void attachToPlayer(@NonNull AVPlayer player);

    /**
     * 释放资源
     */
    void release();

    interface SurfaceListener {

        /**
         * Invoked when a {@link Render}'s Surface is ready for use.
         *
         * @param surface The surface returned by getSurfaceTexture()
         */
        void onSurfaceAvailable(Surface surface);

        /**
         * Invoked when the {@link SurfaceTexture}'s buffers size changed.
         *
         * @param surface The surface returned by
         *                {@link android.view.TextureView#getSurfaceTexture()}
         * @param width   The new width of the surface
         * @param height  The new height of the surface
         */
        void onSurfaceSizeChanged(Surface surface, int width, int height);

        boolean onSurfaceDestroyed(Surface surface);

        void onSurfaceUpdated(Surface surface);
    }


    /**
     * 截图回调
     */
    interface ScreenShotCallback {

        /**
         * 截图结果
         *
         * @param bmp
         */
        void onScreenShotResult(@Nullable Bitmap bmp);
    }
}