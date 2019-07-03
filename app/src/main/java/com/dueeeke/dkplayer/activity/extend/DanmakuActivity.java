package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.videoview.DanmukuVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 弹幕播放
 * Created by devlin on 17-6-11.
 */

public class DanmakuActivity extends AppCompatActivity {

    private DanmukuVideoView danmukuVideoView;
    private static final String URL_VOD = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmaku_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_danmu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        danmukuVideoView = findViewById(R.id.player);
        StandardVideoController standardVideoController = new StandardVideoController(this);
        standardVideoController.setTitle("网易公开课-如何掌控你的自由时间");
        danmukuVideoView.setVideoController(standardVideoController);
        danmukuVideoView.setUrl(URL_VOD);
        danmukuVideoView.start();

        danmukuVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PREPARED) {
                    simulateDanmu();
                }
            }
        });
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
        danmukuVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        danmukuVideoView.release();
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onBackPressed() {
        if (!danmukuVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void showDanMu(View view) {
        danmukuVideoView.showDanMu();
    }

    public void hideDanMu(View view) {
        danmukuVideoView.hideDanMu();
    }

    public void addDanmakuWithDrawable(View view) {
        danmukuVideoView.addDanmakuWithDrawable();
    }

    public void addDanmaku(View view) {
       danmukuVideoView.addDanmaku("这是一条文字弹幕~", true);
    }


    private Handler mHandler = new Handler();

    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                danmukuVideoView.addDanmaku("666666", false);
                mHandler.postDelayed(this, 100);
            }
        });
    }
}
