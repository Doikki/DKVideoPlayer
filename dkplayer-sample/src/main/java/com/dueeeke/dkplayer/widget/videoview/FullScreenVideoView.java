package com.dueeeke.dkplayer.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.videocontroller.CutoutUtil;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 锁定全屏播放器
 * Created by xinyu on 2017/12/25.
 */

public class FullScreenVideoView extends VideoView {

    public FullScreenVideoView(@NonNull Context context) {
        this(context, null);
    }

    public FullScreenVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //适配刘海
        CutoutUtil.adaptCutoutAboveAndroidP(context, true);
    }

    /**
     * 直接开始全屏播放
     */
    public void startFullScreenDirectly() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        startFullScreen();
    }

    @Override
    protected boolean startPlay() {
        startFullScreenDirectly();
        return super.startPlay();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
