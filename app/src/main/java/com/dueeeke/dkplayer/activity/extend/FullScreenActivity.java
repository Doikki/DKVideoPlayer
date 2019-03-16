package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.FullScreenController;
import com.dueeeke.dkplayer.widget.videoview.FullScreenIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity{

    private FullScreenIjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_fullscreen_directly);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ijkVideoView = new FullScreenIjkVideoView(this);
        setContentView(ijkVideoView);
        ijkVideoView.setUrl("http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv");
        FullScreenController controller = new FullScreenController(this);
        controller.setTitle("这是一个标题");
        ijkVideoView.setVideoController(controller);
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_16_9);
        ijkVideoView.start();

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
        if (!ijkVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }
}
