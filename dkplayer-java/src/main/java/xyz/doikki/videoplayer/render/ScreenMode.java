package xyz.doikki.videoplayer.render;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import xyz.doikki.videoplayer.VideoView;

/**
 * 屏幕模式
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({VideoView.SCREEN_MODE_NORMAL, VideoView.SCREEN_MODE_FULL, VideoView.SCREEN_MODE_TINY})
public @interface ScreenMode {

    /**
     * 普通模式
     */
    int NORMAL = VideoView.SCREEN_MODE_NORMAL;

    /**
     * 全屏模式
     */
    int FULL = VideoView.SCREEN_MODE_FULL;

    /**
     * 小窗模式
     */
    int TINY = VideoView.SCREEN_MODE_TINY;
}
