package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.player.AndroidMediaPlayer;
import com.dueeeke.dkplayer.player.ExoMediaPlayer;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.IjkPlayer;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

/**
 * 多播放器切换
 * Created by Devlin_n on 2017/4/7.
 */

public class SwitchPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private IjkVideoView ijkVideoView;
    private StandardVideoController mController;
    private static final String URL = "http://gslb.miaopai.com/stream/FQXM04zrW1dcXGiPdJ6Q3KAq2Fpv4TLV.mp4";
//    private static final String URL = "http://vfile.hshan.com/2018/1524/9156/4430/152491564430.ssm/152491564430.m3u8";
//    private static final String URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("多种播放器切换");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);
        findViewById(R.id.btn_ijk).setOnClickListener(this);
        findViewById(R.id.btn_media).setOnClickListener(this);
        findViewById(R.id.btn_exo).setOnClickListener(this);

        mController = new StandardVideoController(this);
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                .usingSurfaceView()//使用SurfaceView
                .build());
        ijkVideoView.setUrl(URL);
        ijkVideoView.setVideoController(mController);
        ijkVideoView.start();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        AbstractPlayer player = null;
        switch (id) {
            case R.id.btn_ijk:
                player = new IjkPlayer();
                break;
            case R.id.btn_media:
                player = new AndroidMediaPlayer();
                break;
            case R.id.btn_exo:
                player = new ExoMediaPlayer(this);
                break;
        }

        ijkVideoView.release();
        ijkVideoView.setUrl(URL);
        ijkVideoView.setVideoController(mController);

        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                .usingSurfaceView()//使用SurfaceView
                .setCustomMediaPlayer(player)
//                .setLooping()
                .build());
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
