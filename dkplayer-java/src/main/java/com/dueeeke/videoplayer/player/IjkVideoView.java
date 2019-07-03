package com.dueeeke.videoplayer.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 由于IjkPlayer已经不再更新，后续不再基于IjkPlayer进行开发，而是将本框架打造成一个通用的播放器框架
 * 保留此类目的仅为向下兼容，后续版本将会移除
 * @deprecated 使用 {@link VideoView} 代替
 */
@Deprecated
public class IjkVideoView extends VideoView {

    public IjkVideoView(@NonNull Context context) {
        super(context);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}