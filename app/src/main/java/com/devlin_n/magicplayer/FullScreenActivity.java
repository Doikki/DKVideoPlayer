package com.devlin_n.magicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devlin_n.magic_player.controller.MagicVideoController;
import com.devlin_n.magic_player.player.MagicVideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity{

    private MagicVideoView magicVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        magicVideoView = new MagicVideoView(this);
        setContentView(magicVideoView);
        magicVideoView
                .autoRotate()
                .alwaysFullScreen()
                .setTitle("这是一个标题")
                .setUrl("http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/HD/movie_index.m3u8")
                .setVideoController(new MagicVideoController(this))
                .setScreenType(MagicVideoView.SCREEN_TYPE_16_9)
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        magicVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        magicVideoView.resume();
        magicVideoView.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        magicVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!magicVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }

//    /**
//     * 用户返回
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == ALERT_WINDOW_PERMISSION_CODE) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(FullScreenActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
//            } else {
//                magicVideoView.startFloatWindow();
//            }
//        }
//    }
}
