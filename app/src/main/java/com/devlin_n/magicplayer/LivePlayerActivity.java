package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devlin_n.magic_player.player.IjkVideoView;

/**
 * Created by Devlin_n on 2017/4/7.
 */

public class LivePlayerActivity extends AppCompatActivity {

    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));

//        List<VideoModel> videoModels = new ArrayList<>();
//        videoModels.add(new VideoModel("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high","美味1",IjkVideoView.VOD));
//        videoModels.add(new VideoModel("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high","美味2",IjkVideoView.VOD));
//        videoModels.add(new VideoModel("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high","美味3",IjkVideoView.VOD));
//        ijkVideoView.setVideos(videoModels);

//        ijkVideoView.setUrl("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
//        ijkVideoView.setTitle("这是一个标题");
//        ijkVideoView.setMediaController(IjkVideoView.VOD);

        ijkVideoView.setUrl("http://yao.hls.cutv.com/cutvlive/AxeFRth/hls/1xlF7t0_sd.m3u8");
        ijkVideoView.setTitle("深圳卫视");
        ijkVideoView.setMediaController(IjkVideoView.LIVE);
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
                Toast.makeText(LivePlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                ijkVideoView.startFloatScreen();
            }
        }
    }
}
