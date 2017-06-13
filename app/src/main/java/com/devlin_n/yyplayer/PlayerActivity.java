package com.devlin_n.yyplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devlin_n.library.FloatWindowManager;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;

/**
 * 播放其他链接
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private YinYangPlayer yinYangPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Player");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        yinYangPlayer = (YinYangPlayer) findViewById(R.id.magic_video_view);

        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.setLive(true);
            }
            yinYangPlayer
                    .autoRotate()
                    .useAndroidMediaPlayer()
                    .setUrl(intent.getStringExtra("url"))
                    .setVideoController(controller)
                    .start();
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
        yinYangPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        yinYangPlayer.resume();
        yinYangPlayer.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        yinYangPlayer.release();
    }


    @Override
    public void onBackPressed() {
        if (!yinYangPlayer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                yinYangPlayer.startFloatWindow();
            } else {
                Toast.makeText(PlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startFloatWindow(View view) {
        yinYangPlayer.startFloatWindow();
    }

    public void wide(View view) {
        yinYangPlayer.setScreenType(YinYangPlayer.SCREEN_TYPE_16_9);
    }

    public void tv(View view) {
        yinYangPlayer.setScreenType(YinYangPlayer.SCREEN_TYPE_4_3);
    }

    public void match(View view) {
        yinYangPlayer.setScreenType(YinYangPlayer.SCREEN_TYPE_MATCH_PARENT);
    }

    public void original(View view) {
        yinYangPlayer.setScreenType(YinYangPlayer.SCREEN_TYPE_ORIGINAL);
    }
}
