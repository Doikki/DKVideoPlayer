package com.dueeeke.dkplayer.activity.api;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Player");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);

        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.setLive();
            }
            ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                    .autoRotate()//自动旋转屏幕
//                    .enableCache()//启用边播边存
//                .enableMediaCodec()//启动硬解码
//                .usingAndroidMediaPlayer()//使用AndroidMediaPlayer
//                .usingSurfaceView()//使用SurfaceView
                    .build());
            ijkVideoView.setUrl(intent.getStringExtra("url"));
            ijkVideoView.setVideoController(controller);
            ijkVideoView.start();
        }
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
        ijkVideoView.resume();
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

    public void screenScaleDefault(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
    }

    public void screenScale169(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_16_9);
    }

    public void screenScale43(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_4_3);
    }

    public void screenScaleOriginal(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_ORIGINAL);
    }

    public void screenScaleMatch(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_MATCH_PARENT);
    }

    public void screenScaleCenterCrop(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
    }
}
