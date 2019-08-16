package com.dueeeke.dkplayer.app;

import android.app.Application;
import android.os.StrictMode;

import com.dueeeke.videoplayer.BuildConfig;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * app
 * Created by Devlin_n on 2017/4/22.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setLogEnabled(BuildConfig.DEBUG)
//                .setPlayerFactory(IjkPlayerFactory.create(this))
                .setPlayerFactory(ExoMediaPlayerFactory.create(this))
//                .setAutoRotate(true)
//                .setEnableMediaCodec(true)
//                .setUsingSurfaceView(true)
//                .setEnableParallelPlay(true)
//                .setEnableAudioFocus(false)
//                .setScreenScale(VideoView.SCREEN_SCALE_MATCH_PARENT)
                .build());

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());

        }
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
