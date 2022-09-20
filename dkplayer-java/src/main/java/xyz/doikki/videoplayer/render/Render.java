package xyz.doikki.videoplayer.render;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videoplayer.AVPlayer;

public interface Render {

    /**
     * 获取真实的RenderView:用于挂在view tree上
     */
    View getView();

    /**
     * 设置Surface监听
     *
     * @param listener
     */
    void setSurfaceListener(SurfaceListener listener);

    /**
     * 设置视频旋转角度
     *
     * @param degree 角度值
     */
    void setVideoRotation(@IntRange(from = 0, to = 360) int degree);

    /**
     * 设置视频宽高
     *
     * @param videoWidth  宽
     * @param videoHeight 高
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 截图
     */
    default void screenshot(@NonNull ScreenShotCallback callback) {
        screenshot(false, callback);
    }

    /**
     * 截图
     *
     * @param highQuality 是否采用高质量，默认false；
     *                    如果设置为true，则{@link ScreenShotCallback}返回的{@link Bitmap}采用{@link Bitmap.Config#ARGB_8888}配置，相反则采用{@link Bitmap.Config#RGB_565}
     * @param callback    回调
     * @see Bitmap.Config
     */
    void screenshot(boolean highQuality, @NonNull ScreenShotCallback callback);

    /**
     * 关联到播放器中
     */
    void attachToPlayer(@NonNull AVPlayer player);


    /**
     * 设置screen scale type
     *
     * @param scaleType 类型
     */
    void setScaleType(int scaleType);

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