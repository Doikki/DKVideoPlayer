package com.dueeeke.dkplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.dkplayer.R;

/**
 * 播放其他链接
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Player");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = (IjkVideoView) findViewById(R.id.player);

        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.setLive();
            }
            ijkVideoView
                    .autoRotate()
//                    .useAndroidMediaPlayer()
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
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Toast.makeText(PlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
