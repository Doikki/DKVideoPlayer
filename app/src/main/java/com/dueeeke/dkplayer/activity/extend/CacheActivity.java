package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.videoview.CacheIjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;

public class CacheActivity extends AppCompatActivity {

    private CacheIjkVideoView mCacheIjkVideoView;

    private static final String URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        mCacheIjkVideoView = findViewById(R.id.player);
        mCacheIjkVideoView.setUrl(URL);
        mCacheIjkVideoView.setVideoController(new StandardVideoController(this));
        mCacheIjkVideoView.start();

        //删除url对应默认缓存文件
//        VideoCacheManager.clearDefaultCache(this, URL);
        //清除缓存文件中的所有缓存
//        VideoCacheManager.clearAllCache(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCacheIjkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCacheIjkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCacheIjkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mCacheIjkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
