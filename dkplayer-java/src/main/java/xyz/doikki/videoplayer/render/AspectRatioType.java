package xyz.doikki.videoplayer.render;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 视频区域的显示比例
 * <p>
 * {@link AspectRatioType#SCALE} & {@link AspectRatioType#SCALE_ORIGINAL}& {@link AspectRatioType#CENTER_CROP} 都是按照图像比例进行缩放或者裁剪
 * {@link AspectRatioType#SCALE}
 * {@link AspectRatioType#MATCH_PARENT} 是占满父级控件，所以不保证图像比例，可能会导致图像变形
 */
@IntDef({AspectRatioType.SCALE,
        AspectRatioType.SCALE_16_9,
        AspectRatioType.SCALE_18_9,
        AspectRatioType.SCALE_4_3,
        AspectRatioType.SCALE_ORIGINAL,
        AspectRatioType.MATCH_PARENT, AspectRatioType.CENTER_CROP})
@Retention(RetentionPolicy.SOURCE)
public @interface AspectRatioType {

    /**
     * 将图像等比例缩放，适配(图像的)最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面（上下或者左右）可能会与父级控件留有空隙
     * 默认采用该模式
     *
     * @see android.widget.ImageView.ScaleType#FIT_CENTER
     */
    int SCALE = 0;

    /**
     * 按4:3 缩放（可能导致上下或者左右留有黑边）
     */
    int SCALE_4_3 = 1;

    /**
     * 按16:9 缩放（可能导致上下或者左右留有黑边）
     */
    int SCALE_16_9 = 2;

    /**
     * 按18:9 缩放（可能导致上下或者左右留有黑边）
     */
    int SCALE_18_9 = 3;

    /**
     * 按照图像原始大小（不超过显示区域）显示；（可能导致上下或者左右甚至四周留有黑边）
     * 当图像比显示区域大时，该模式效果类似于{@link #SCALE}
     * 当图像比显示区域小时，会以图片原始大小显示
     */
    int SCALE_ORIGINAL = 4;

    /**
     * 以裁剪模式将图片等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全
     * 重点：铺满、四周不留空隙，但图像可能被裁剪
     *
     * @see android.widget.ImageView.ScaleType#CENTER_CROP 类似该效果
     */
    int CENTER_CROP = 10;

    /**
     * 铺满父级容器:即会将视频拉伸铺满控件大小（该方式可能会造成视频扭曲）
     * 即包含本View的Parent Group是多大就多大
     *
     * @see android.widget.ImageView.ScaleType#FIT_XY
     */
    int MATCH_PARENT = 20;

}
