package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devlin_n.magic_player.player.IjkVideoView;

import static com.devlin_n.magic_player.player.IjkVideoView.ALERT_WINDOW_PERMISSION_CODE;

/**
 * 点播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class VodPlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;
    private static final String URL_VOD = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private static final String URL_AD = "http://gslb.miaopai.com/stream/FQXM04zrW1dcXGiPdJ6Q3KAq2Fpv4TLV.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("VOD");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));

//        List<VideoModel> videos = new ArrayList<>();
//        videos.add(new VideoModel(URL_AD, "广告", IjkVideoView.AD));
//        videos.add(new VideoModel(URL_VOD, "网易公开课-如何掌控你的自由时间", IjkVideoView.VOD));

        ijkVideoView
                .init()
                .autoRotate()
                .enableCache()
//                .setVideos(videos)
                .setUrl(URL_VOD)
                .setTitle("网易公开课-如何掌控你的自由时间")
                .setMediaController(IjkVideoView.VOD)
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
//        ijkVideoView.resume();
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALERT_WINDOW_PERMISSION_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(VodPlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                ijkVideoView.startFloatWindow();
            }
        }
    }

    public void startFloatWindow(View view) {
        ijkVideoView.startFloatWindow();
    }

    public void wide(View view) {
        ijkVideoView.setScreenType(IjkVideoView.SCREEN_TYPE_16_9);
    }

    public void tv(View view) {
        ijkVideoView.setScreenType(IjkVideoView.SCREEN_TYPE_4_3);
    }

    public void match(View view) {
        ijkVideoView.setScreenType(IjkVideoView.SCREEN_TYPE_MATCH_PARENT);
    }

    public void original(View view) {
        ijkVideoView.setScreenType(IjkVideoView.SCREEN_TYPE_ORIGINAL);
    }
}
