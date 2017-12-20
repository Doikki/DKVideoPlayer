package com.devlin_n.videoplayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.devlin_n.videoplayer.controller.FloatController;
import com.devlin_n.videoplayer.util.Constants;
import com.devlin_n.videoplayer.util.KeyUtil;
import com.devlin_n.videoplayer.util.L;
import com.devlin_n.videoplayer.util.WindowUtil;
import com.devlin_n.videoplayer.widget.FloatView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */

public class BackgroundPlayService extends Service {
    private IjkVideoView videoView;
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
        String action = intent.getStringExtra(KeyUtil.ACTION);

        switch (action) {
            case Constants.COMMAND_START:
                L.d("start");
                url = intent.getStringExtra(KeyUtil.URL);
                position = intent.getIntExtra(KeyUtil.POSITION, 0);
                isCache = intent.getBooleanExtra(KeyUtil.ENABLE_CACHE, false);
                startPlay();
                Constants.IS_START_FLOAT_WINDOW = true;
                break;
            case Constants.COMMAND_STOP:
                L.d("stop");
                Constants.IS_START_FLOAT_WINDOW = false;
                if (floatView != null) floatView.removeFromWindow();
                videoView.release();
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int startX = WindowUtil.getScreenWidth(getApplicationContext()) - WindowUtil.dp2px(getApplicationContext(), 200);
        int startY = WindowUtil.getScreenHeight(getApplicationContext(), false) / 2;
        floatView = new FloatView(getApplicationContext(), startX, startY);
        videoView = new IjkVideoView(getApplicationContext());
        floatView.addView(videoView);
    }

    private void startPlay() {
        if (isCache) videoView.enableCache();
        videoView.skipPositionWhenPlay(url, position).setVideoController(new FloatController(getApplicationContext())).start();
        floatView.addToWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_START_FLOAT_WINDOW = false;
        if (floatView != null) floatView.removeFromWindow();
        videoView.release();
    }
}
