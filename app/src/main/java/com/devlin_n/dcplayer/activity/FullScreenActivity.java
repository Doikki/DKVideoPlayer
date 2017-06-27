package com.devlin_n.dcplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.videoplayer.controller.FullScreenController;
import com.devlin_n.videoplayer.player.IjkVideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity{

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ijkVideoView = new IjkVideoView(this);
        setContentView(ijkVideoView);
        ijkVideoView
                .autoRotate()
                .alwaysFullScreen()
//                .useAndroidMediaPlayer()
                .setTitle("这是一个标题")
                .setUrl("http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/HD/movie_index.m3u8")
                .setVideoController(new FullScreenController(this))
                .setScreenScale(IjkVideoView.SCREEN_SCALE_16_9)
                .start();
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
        ijkVideoView.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                ijkVideoView.startFloatWindow();
            } else {
                Toast.makeText(FullScreenActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
