package com.dueeeke.dkplayer.activity.extend;

import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.interf.ControllerListener;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.dkplayer.widget.controller.AdController;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 广告
 * Created by Devlin_n on 2017/4/7.
 */

public class ADActivity extends BaseActivity<VideoView> {

    private static final String URL_VOD = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
//    private static final String URL_VOD = "http://baobab.wdjcdn.com/14564977406580.mp4";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";
    private static final String URL_AD = "https://gslb.miaopai.com/stream/IR3oMYDhrON5huCmf7sHCfnU5YKEkgO2.mp4";

    private StandardVideoController mStandardVideoController;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ad;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_ad;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);

        AdController adController = new AdController(this);
        adController.setControllerListener(new ControllerListener() {
            @Override
            public void onAdClick() {
                Toast.makeText(ADActivity.this, "广告点击跳转", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSkipAd() {
                playVideo();
            }
        });

        HttpProxyCacheServer cacheServer = ProxyVideoCacheManager.getProxy(this);
        String proxyUrl = cacheServer.getProxyUrl(URL_AD);
        mVideoView.setUrl(proxyUrl);
        mVideoView.setVideoController(adController);

        //监听播放结束
        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    playVideo();
                }
            }
        });

        mVideoView.start();
    }

    /**
     * 播放正片
     */
    private void playVideo() {
        mVideoView.release();
        //重新设置数据
        mVideoView.setUrl(URL_VOD);
        if (mStandardVideoController == null) {
            mStandardVideoController = new StandardVideoController(ADActivity.this);
        }
        mStandardVideoController.setTitle("正片标题");
        //更换控制器
        mVideoView.setVideoController(mStandardVideoController);
        //开始播放
        mVideoView.start();
    }
}
