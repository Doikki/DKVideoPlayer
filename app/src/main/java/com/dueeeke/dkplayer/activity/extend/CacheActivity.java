package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.videoview.CacheVideoView;
import com.dueeeke.videocontroller.StandardVideoController;

public class CacheActivity extends AppCompatActivity {

    private CacheVideoView mCacheVideoView;

    private static final String URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        mCacheVideoView = findViewById(R.id.player);
        mCacheVideoView.setUrl(URL);
        mCacheVideoView.setVideoController(new StandardVideoController(this));
        mCacheVideoView.start();

        //删除url对应默认缓存文件
//        VideoCacheManager.clearDefaultCache(this, URL);
        //清除缓存文件中的所有缓存
//        VideoCacheManager.clearAllCache(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCacheVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCacheVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCacheVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mCacheVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
