package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.devlin_n.magic_player.controller.IjkMediaController;
import com.devlin_n.magic_player.player.IjkVideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity{

    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private IjkVideoView ijkVideoView;
    private IjkMediaController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ijkVideoView = new IjkVideoView(this);
        ijkVideoView.setAutoPlay(true);
        ijkVideoView.setTitle("这是一个标题");
//        ijkVideoView.getThumb().setImageResource(R.drawable.thumb);
        ijkVideoView.setUrl("http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/HD/movie_index.m3u8");
        controller = new IjkMediaController(this);
        controller.setAutoRotate(false);
        ijkVideoView.setMediaController(controller);
        setContentView(ijkVideoView);
        controller.startFullScreenDirectly();
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
        if (!controller.lockBack()){
            super.onBackPressed();
        }
    }

    /**
     * 用户返回
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALERT_WINDOW_PERMISSION_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(FullScreenActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                ijkVideoView.startFloatWindow();
            }
        }
    }
}
