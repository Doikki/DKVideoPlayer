package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.interf.ControllerListener;
import com.dueeeke.dkplayer.widget.controller.AdController;
import com.dueeeke.dkplayer.widget.videoview.CacheIjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 广告
 * Created by Devlin_n on 2017/4/7.
 */

public class ADActivity extends AppCompatActivity {

    private CacheIjkVideoView ijkVideoView;
    private static final String URL_VOD = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
//    private static final String URL_VOD = "http://baobab.wdjcdn.com/14564977406580.mp4";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";
    private static final String URL_AD = "https://gslb.miaopai.com/stream/IR3oMYDhrON5huCmf7sHCfnU5YKEkgO2.mp4";

    private StandardVideoController mStandardVideoController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_ad);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);

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

        ijkVideoView.setUrl(URL_AD);
        ijkVideoView.setCacheEnabled(true);
        ijkVideoView.setVideoController(adController);

        //监听播放结束
        ijkVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == IjkVideoView.STATE_PLAYBACK_COMPLETED) {
                    playVideo();
                }
            }
        });

        ijkVideoView.start();
    }

    /**
     * 播放正片
     */
    private void playVideo() {
        ijkVideoView.release();
        //重新设置数据
        ijkVideoView.setUrl(URL_VOD);
        ijkVideoView.setCacheEnabled(false);
        if (mStandardVideoController == null) {
            mStandardVideoController = new StandardVideoController(ADActivity.this);
        }
        mStandardVideoController.setTitle("正片标题");
        //更换控制器
        ijkVideoView.setVideoController(mStandardVideoController);
        //开始播放
        ijkVideoView.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
