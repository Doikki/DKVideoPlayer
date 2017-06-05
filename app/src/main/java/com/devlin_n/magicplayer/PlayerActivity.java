package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.devlin_n.magic_player.controller.MagicVideoController;
import com.devlin_n.magic_player.player.MagicVideoView;

/**
 * 播放其他链接
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private MagicVideoView magicVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Player");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        magicVideoView = (MagicVideoView) findViewById(R.id.magic_video_view);

        Intent intent = getIntent();
        if (intent != null) {
            MagicVideoController controller = new MagicVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.setLive(true);
            }
            magicVideoView
                    .autoRotate()
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
        magicVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        magicVideoView.resume();
        magicVideoView.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        magicVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!magicVideoView.onBackPressed()) {
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
//            if (FloatWindowManager.getInstance().checkPermission(this)) {
//                Toast.makeText(PlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
//            } else {
//                magicVideoView.startFloatWindow();
//            }
//        }
//    }

    public void startFloatWindow(View view) {
        magicVideoView.startFloatWindow();
    }

    public void wide(View view) {
        magicVideoView.setScreenType(MagicVideoView.SCREEN_TYPE_16_9);
    }

    public void tv(View view) {
        magicVideoView.setScreenType(MagicVideoView.SCREEN_TYPE_4_3);
    }

    public void match(View view) {
        magicVideoView.setScreenType(MagicVideoView.SCREEN_TYPE_MATCH_PARENT);
    }

    public void original(View view) {
        magicVideoView.setScreenType(MagicVideoView.SCREEN_TYPE_ORIGINAL);
    }
}
