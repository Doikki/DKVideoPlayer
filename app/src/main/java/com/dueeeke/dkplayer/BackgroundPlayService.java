package com.dueeeke.dkplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dueeeke.dkplayer.widget.controller.FloatController;
import com.dueeeke.dkplayer.widget.videoview.FloatIjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.dueeeke.videoplayer.util.Constants;
import com.dueeeke.videoplayer.util.KeyUtil;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.WindowUtil;
import com.dueeeke.videoplayer.widget.FloatView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */

public class BackgroundPlayService extends Service {
    private FloatIjkVideoView videoView;
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
        videoView = new FloatIjkVideoView(getApplicationContext());
        floatView.addView(videoView);
    }

    private void startPlay() {
        if (isCache) {
            PlayerConfig config = new PlayerConfig.Builder().enableCache().build();
            videoView.setPlayerConfig(config);
        }
        videoView.skipPositionWhenPlay(url, position);
        videoView.setVideoController(new FloatController(getApplicationContext()));
        videoView.start();
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
