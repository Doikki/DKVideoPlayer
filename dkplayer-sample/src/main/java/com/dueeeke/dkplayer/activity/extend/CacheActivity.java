package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.ProxyCacheVideoView;
import com.dueeeke.videocontroller.StandardVideoController;

public class CacheActivity extends BaseActivity<ProxyCacheVideoView> {

    private static final String URL = "https://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4";

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
