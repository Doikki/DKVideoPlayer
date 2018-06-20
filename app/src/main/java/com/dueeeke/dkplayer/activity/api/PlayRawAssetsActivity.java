package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

/**
 * 播放raw/assets视频
 */

public class PlayRawAssetsActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_raw_assets);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_raw_or_assets);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                    .enableCache()//启用边播边存
//                .enableMediaCodec()//启动硬解码
//                .usingSurfaceView()//使用SurfaceView
                //ijkplayer不支持assets播放,需切换成MediaPlayer
//                .setCustomMediaPlayer(new AndroidMediaPlayer(this))
                .build());
        //播放assets文件
//        AssetManager am = getResources().getAssets();
//        AssetFileDescriptor afd = null;
//        try {
//            afd = am.openFd("test.mp4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ijkVideoView.setAssetFileDescriptor(afd);

        //播放raw
        String url = "android.resource://" + getPackageName() + "/" + R.raw.movie;
        ijkVideoView.setUrl(url);

        ijkVideoView.setVideoController(controller);
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
