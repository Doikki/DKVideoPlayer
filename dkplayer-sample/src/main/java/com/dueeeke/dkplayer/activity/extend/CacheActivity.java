package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.CacheVideoView;
import com.dueeeke.videocontroller.StandardVideoController;

public class CacheActivity extends BaseActivity<CacheVideoView> {

    private static final String URL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_cache;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_cache;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        mVideoView.setUrl(URL);
        mVideoView.setVideoController(new StandardVideoController(this));
        mVideoView.start();

        //删除url对应默认缓存文件
//        VideoCacheManager.clearDefaultCache(this, URL);
        //清除缓存文件中的所有缓存
//        VideoCacheManager.clearAllCache(this);
    }
}
