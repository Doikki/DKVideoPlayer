package com.dueeeke.dkplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.dkplayer.R;

/**
 * 直播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class LivePlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;
    private static final String URL = "http://ivi.bupt.edu.cn/hls/sztv.m3u8";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIVE");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = (IjkVideoView) findViewById(R.id.player);
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));

        StandardVideoController controller = new StandardVideoController(this);
        controller.setLive();
        Glide.with(this)
                .load("http://img.9ku.com/geshoutuji/singertuji/5/50815/50815_1.jpg")
                .asBitmap()
                .animate(R.anim.anim_alpha_in)
                .placeholder(android.R.color.darker_gray)
                .into(controller.getThumb());
        ijkVideoView
                .autoRotate()
//                .useAndroidMediaPlayer()
                .setUrl(URL)
                .setTitle("深圳卫视")
                .setVideoController(controller);
//                .start();
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
        ijkVideoView.stopFloatWindow();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                ijkVideoView.startFloatWindow();
            } else {
                Toast.makeText(LivePlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startFloatWindow(View view) {
        ijkVideoView.startFloatWindow();
    }
}
