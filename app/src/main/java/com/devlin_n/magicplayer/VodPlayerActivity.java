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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devlin_n.magic_player.player.IjkVideoView;
import com.devlin_n.magic_player.player.VideoModel;

import java.util.ArrayList;
import java.util.List;

import static com.devlin_n.magic_player.player.IjkVideoView.ALERT_WINDOW_PERMISSION_CODE;

/**
 * 点播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class VodPlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;
    private static final String URL = "http://pcvideoksyun.titan.mgtv.com/c1/2017/04/22_0/6E7799AADA98" +
            "CD56D35E8E19329FA376_20170422_1_1_1545_mp4/115877120D18FC6708FD3804EE654F67.m3u8?t=58fd302" +
            "a&pno=1050&sign=c81749459848c37f3e686d7e676693d0&win=600&srgid=25028&urgid=372&srgids=25028&" +
            "nid=25028&payload=usertoken%3Dhit%3D2%5Eruip%3D236214332&rdur=21600&limitrate=0&fid=6E7799A" +
            "ADA98CD56D35E8E19329FA376&ver=0x03&uuid=692f212ebc754fb7ad747c9ae22ade22&arange=0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("点播播放");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));

        ijkVideoView.getThumb().setImageResource(R.drawable.thumb);
        List<VideoModel> videoModels = new ArrayList<>();
        videoModels.add(new VideoModel("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high","广告时间",IjkVideoView.AD));
        videoModels.add(new VideoModel(URL,"这只是一个标题，-(゜ -゜)つロ 乾杯~",IjkVideoView.VOD));
        ijkVideoView
                .init()
                .autoRotate()
                .setVideos(videoModels);
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
}
