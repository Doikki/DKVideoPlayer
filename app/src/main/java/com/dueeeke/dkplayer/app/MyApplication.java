package com.dueeeke.dkplayer.app;

import android.app.Application;

import com.dueeeke.dkplayer.BuildConfig;
import com.dueeeke.videoplayer.util.L;
import com.squareup.leakcanary.LeakCanary;

/**
 * app
 * Created by Devlin_n on 2017/4/22.
 */

public class MyApplication extends Application{

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        //播放器日志
        L.setDebug(BuildConfig.DEBUG);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
