package com.dueeeke.dkplayer.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 锁定全屏播放器
 * Created by xinyu on 2017/12/25.
 */

public class FullScreenIjkVideoView extends IjkVideoView {

    public FullScreenIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public FullScreenIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 直接开始全屏播放
     */
    public void startFullScreenDirectly() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startFullScreen();
    }

    @Override
    protected void startPlay() {
        startFullScreenDirectly();
        super.startPlay();
    }

    @Override
    protected void onOrientationPortrait(Activity activity) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
