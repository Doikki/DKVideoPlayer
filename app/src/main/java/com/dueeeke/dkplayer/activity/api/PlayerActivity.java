package com.dueeeke.dkplayer.activity.api;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.dueeeke.videoplayer.util.L;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Player");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ijkVideoView = findViewById(R.id.player);

        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.setLive();
            }
            String title = intent.getStringExtra(IntentKeys.TITLE);
            controller.setTitle(title);
            ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                    .autoRotate()//自动旋转屏幕
//                    .enableMediaCodec()//启动硬解码
//                    .usingSurfaceView()//使用SurfaceView
//                    .setCustomMediaPlayer(new ExoMediaPlayer(this))
//                    .setCustomMediaPlayer(new AndroidMediaPlayer(this))
                    .build());
//            ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
            ijkVideoView.setUrl(intent.getStringExtra("url"));
            ijkVideoView.setVideoController(controller);
            ijkVideoView.start();

            //播放状态监听
            ijkVideoView.addOnVideoViewStateChangeListener(mOnVideoViewStateChangeListener);

        }
    }

    private OnVideoViewStateChangeListener mOnVideoViewStateChangeListener = new OnVideoViewStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case IjkVideoView.PLAYER_NORMAL://小屏
                    break;
                case IjkVideoView.PLAYER_FULL_SCREEN://全屏
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case IjkVideoView.STATE_IDLE:
                    break;
                case IjkVideoView.STATE_PREPARING:
                    break;
                case IjkVideoView.STATE_PREPARED:
                    //需在此时获取视频宽高
                    int[] videoSize = ijkVideoView.getVideoSize();
                    L.d("视频宽：" + videoSize[0]);
                    L.d("视频高：" + videoSize[1]);
                    break;
                case IjkVideoView.STATE_PLAYING:
                    break;
                case IjkVideoView.STATE_PAUSED:
                    break;
                case IjkVideoView.STATE_BUFFERING:
                    break;
                case IjkVideoView.STATE_BUFFERED:
                    break;
                case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case IjkVideoView.STATE_ERROR:
                    break;
            }
        }
    };

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

    public void screenScaleDefault(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
    }

    public void screenScale169(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_16_9);
    }

    public void screenScale43(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_4_3);
    }

    public void screenScaleOriginal(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_ORIGINAL);
    }

    public void screenScaleMatch(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_MATCH_PARENT);
    }

    public void screenScaleCenterCrop(View view) {
        ijkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_CENTER_CROP);
    }

    int i = 0;
    public void setMirrorRotate(View view) {
        ijkVideoView.setMirrorRotation(i % 2 == 0);
        i++;
    }

    public void setSpeed0_75(View view) {
        ijkVideoView.setSpeed(0.75f);
    }

    public void setSpeed0_5(View view) {
        ijkVideoView.setSpeed(0.5f);
    }

    public void setSpeed1_0(View view) {
        ijkVideoView.setSpeed(1.0f);
    }

    public void setSpeed1_5(View view) {
        ijkVideoView.setSpeed(1.5f);
    }

    public void setSpeed2_0(View view) {
        ijkVideoView.setSpeed(2.0f);
    }
}
