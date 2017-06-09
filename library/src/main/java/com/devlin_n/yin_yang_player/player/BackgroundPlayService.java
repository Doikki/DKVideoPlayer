package com.devlin_n.yin_yang_player.player;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.devlin_n.yin_yang_player.controller.FloatController;
import com.devlin_n.yin_yang_player.util.KeyUtil;
import com.devlin_n.yin_yang_player.util.WindowUtil;
import com.devlin_n.yin_yang_player.widget.FloatView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */

public class BackgroundPlayService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private YinYangPlayer videoView;
    private String url;
    private FloatView floatView;
    private int position;
    private boolean isCache;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(KeyUtil.URL);
        position = intent.getIntExtra(KeyUtil.POSITION, 0);
        isCache = intent.getBooleanExtra(KeyUtil.ENABLE_CACHE, false);
        startPlay();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();
    }

    private void startPlay() {
        if (isCache) videoView.enableCache();
        videoView.useAndroidMediaPlayer().skipPositionWhenPlay(url, position).setVideoController(new FloatController(getApplicationContext())).start();
        wm.addView(floatView, wmParams);
    }


    private void initWindow() {
        wm = WindowUtil.getWindowManager(getApplicationContext());
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = WindowUtil.dip2px(getApplicationContext(), 250);
        wmParams.width = width;
        wmParams.height = width * 9 / 16;
        wmParams.x = WindowUtil.getScreenWidth(getApplicationContext()) - width;
        wmParams.y = WindowUtil.getScreenHeight(getApplicationContext(), false) / 2;
        floatView = new FloatView(getApplicationContext(), wm, wmParams);
        videoView = floatView.magicVideoView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) wm.removeView(floatView);
        videoView.release();
    }
}
