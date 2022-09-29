package xyz.doikki.videoplayer.render;

import android.graphics.Bitmap;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public interface RenderFunction {

    /**
     * 设置Surface监听
     *
     * @param listener
     */
    void setSurfaceListener(Render.SurfaceListener listener);

    /**
     * 设置视频旋转角度
     *
     * @param degree 角度值
     */
    void setVideoRotation(@IntRange(from = 0, to = 360) int degree);

    /**
     * 设置镜像旋转
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable){
        //默认不支持镜像旋转;只有TextureView才支持
    }

    /**
     * 设置视频宽高
     *
     * @param videoWidth  宽
     * @param videoHeight 高
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置界面比例模式
     *
     * @param scaleType 类型
     */
    void setAspectRatioType(@AspectRatioType int scaleType);

    /**
     * 截图
     */
    default void screenshot(@NonNull Render.ScreenShotCallback callback) {
        screenshot(false, callback);
    }

    /**
     * 截图
     *
     * @param highQuality 是否采用高质量，默认false；
     *                    如果设置为true，则{@link Render.ScreenShotCallback}返回的{@link Bitmap}采用{@link Bitmap.Config#ARGB_8888}配置，相反则采用{@link Bitmap.Config#RGB_565}
     * @param callback    回调
     * @see Bitmap.Config
     */
    void screenshot(boolean highQuality, @NonNull Render.ScreenShotCallback callback);

}
