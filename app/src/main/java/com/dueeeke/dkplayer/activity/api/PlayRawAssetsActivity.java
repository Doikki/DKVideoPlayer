package com.dueeeke.dkplayer.activity.api;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

import java.io.IOException;

/**
 * 播放raw/assets视频
 */

public class PlayRawAssetsActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_raw_assets);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_raw_or_assets);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        //播放assets文件
        AssetManager am = getResources().getAssets();
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd("test.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideoView.setAssetFileDescriptor(afd);

        //播放raw
//        String url = "android.resource://" + getPackageName() + "/" + R.raw.movie;
//        mVideoView.setUrl(url);

        mVideoView.setVideoController(controller);
        mVideoView.start();
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
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
