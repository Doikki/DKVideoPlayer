package com.devlin_n.dcplayer.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * app
 * Created by Devlin_n on 2017/4/22.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
