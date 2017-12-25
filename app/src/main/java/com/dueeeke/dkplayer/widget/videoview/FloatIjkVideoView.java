package com.dueeeke.dkplayer.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.dkplayer.interf.FloatMediaControl;
import com.dueeeke.dkplayer.BackgroundPlayService;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.Constants;
import com.dueeeke.videoplayer.util.KeyUtil;
import com.dueeeke.videoplayer.util.WindowUtil;

/**
 * 悬浮播放器
 * Created by xinyu on 2017/12/26.
 */

public class FloatIjkVideoView extends IjkVideoView implements FloatMediaControl{

    public FloatIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public FloatIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始画中画播放，点播视频会记录播放位置
     */
    @Override
    public void startFloatWindow() {

        if (FloatWindowManager.getInstance().checkPermission(getContext())) {
            startBackgroundService();
        } else {
            FloatWindowManager.getInstance().applyPermission(getContext());
        }
    }

    /**
     * 启动画中画播放的后台服务
     */
    private void startBackgroundService() {
        if (!isInPlaybackState()) return;
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.URL, mCurrentUrl);
        getCurrentPosition();
        intent.putExtra(KeyUtil.POSITION, getDuration() <= 0 ? 0 : mCurrentPosition);
        intent.putExtra(KeyUtil.ENABLE_CACHE, isCache);
        intent.putExtra(KeyUtil.ACTION, Constants.COMMAND_START);
        getContext().getApplicationContext().startService(intent);
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity != null) activity.finish();
    }

    /**
     * 关闭画中画
     */
    @Override
    public void stopFloatWindow() {
        Intent intent = new Intent(getContext(), BackgroundPlayService.class);
        intent.putExtra(KeyUtil.ACTION, Constants.COMMAND_STOP);
        getContext().getApplicationContext().startService(intent);
    }
}
