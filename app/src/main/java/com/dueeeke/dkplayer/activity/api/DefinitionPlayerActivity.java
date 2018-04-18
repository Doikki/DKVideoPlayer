package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.DefinitionController;
import com.dueeeke.dkplayer.widget.videoview.DefinitionIjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

import java.util.LinkedHashMap;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class DefinitionPlayerActivity extends AppCompatActivity {

    private DefinitionIjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Multi-rate");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);

        DefinitionController controller = new DefinitionController(this);
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                .usingAndroidMediaPlayer()
                .build());

        LinkedHashMap<String, String> videos = new LinkedHashMap<>();
        videos.put("标清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_sd.flv");
        videos.put("高清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_hd.flv");
        videos.put("超清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_shd.flv");
        ijkVideoView.setDefinitionVideos(videos);
        ijkVideoView.setVideoController(controller);
        ijkVideoView.setTitle("韩雪：积极的悲观主义者");
        ijkVideoView.start();
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
}
