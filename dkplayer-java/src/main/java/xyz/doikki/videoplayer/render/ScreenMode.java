package xyz.doikki.videoplayer.render;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import xyz.doikki.videoplayer.DKVideoView;

/**
 * 屏幕模式
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({DKVideoView.SCREEN_MODE_NORMAL, DKVideoView.SCREEN_MODE_FULL, DKVideoView.SCREEN_MODE_TINY})
public @interface ScreenMode {

    /**
     * 普通模式
     */
    int NORMAL = DKVideoView.SCREEN_MODE_NORMAL;

    /**
     * 全屏模式
     */
    int FULL = DKVideoView.SCREEN_MODE_FULL;

    /**
     * 小窗模式
     */
    int TINY = DKVideoView.SCREEN_MODE_TINY;
}
