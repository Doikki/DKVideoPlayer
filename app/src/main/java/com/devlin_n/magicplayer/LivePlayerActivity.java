package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devlin_n.magic_player.player.IjkVideoView;

import static android.os.Build.VERSION_CODES.M;
import static com.devlin_n.magic_player.player.IjkVideoView.ALERT_WINDOW_PERMISSION_CODE;

/**
 * 直播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class LivePlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;
    private static final String URL = "http://live.hcs.cmvideo.cn:8088/wd-hunanhd-1200/index.m3u8?msi" +
            "sdn=3000000000000&mdspid=&spid=699017&netType=5&sid=2201064496&pid=2028595851&timestamp=" +
            "20170424095254&Channel_ID=0116_22300109-91000-20300&ProgramID=603996975&ParentNodeID=-99&" +
            "preview=1&playseek=000000-000600&client_ip=211.159.219.164&assertID=2201064496&SecurityKey" +
            "=20170424095254&encrypt=73bfa49aef2e1d3349dbe119642163e7";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("直播播放");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));

        ijkVideoView
                .init()
                .autoRotate()
                .setUrl(URL)
                .setTitle("湖南卫视")
                .setMediaController(IjkVideoView.LIVE)
                .start();
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

    /**
     * 用户返回
     */
    @RequiresApi(api = M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALERT_WINDOW_PERMISSION_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(LivePlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                ijkVideoView.startFloatWindow();
            }
        }
    }

    public void startFloatWindow(View view) {
        ijkVideoView.startFloatWindow();
    }
}
