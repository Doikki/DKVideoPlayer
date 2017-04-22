package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devlin_n.magic_player.controller.AdController;
import com.devlin_n.magic_player.controller.IjkMediaController;
import com.devlin_n.magic_player.player.IjkVideoView;
import com.devlin_n.magic_player.player.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 点播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class VodPlayerActivity extends AppCompatActivity {

    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));

        List<VideoModel> videoModels = new ArrayList<>();
        IjkMediaController controller = new IjkMediaController(this);
        AdController adController = new AdController(this);
        videoModels.add(new VideoModel("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high","广告时间",adController));
        videoModels.add(new VideoModel("http://flv2.bn.netease.com/videolib3/1611/28/GbgsL3639/HD/movie_index.m3u8","这只是一个标题，-(゜ -゜)つロ 乾杯~",controller));
        ijkVideoView.setAutoPlay(true);
        ijkVideoView.setVideos(videoModels);

//        ijkVideoView.setAutoPlay(false);
//        ijkVideoView.setTitle("这是一个标题");
//        ijkVideoView.getThumb().setImageResource(R.drawable.thumb);
//        ijkVideoView.setUrl("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
//        IjkMediaController controller = new IjkMediaController(this);
//        controller.setAutoRotate(true);
//        ijkVideoView.setMediaController(controller);
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
        if (!ijkVideoView.backFromFullScreen()) {
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
