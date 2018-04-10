package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkMediaEngine;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class CustomMediaEngineActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("CustomPlayer");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


//        String url = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
        String url = "rtsp://ajj:12345678@218.21.217.122:65523/h264/ch40/sub/av_stream";
//        String url = "http://storage.gzstv.net/uploads/media/huangmeiyan/jr05-09.mp4";
        //测试concat,将项目根目录的other文件夹中的test.ffconcat文件复制到sd卡根目录测试
//        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String url = "file://" + absolutePath + File.separator + "test.ffconcat";

        ijkVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                    .enableCache()//启用边播边存
//                .enableMediaCodec()//启动硬解码
//                .usingAndroidMediaPlayer()//使用AndroidMediaPlayer
//                .usingSurfaceView()//使用SurfaceView
                .setCustomMediaEngine(new IjkMediaEngine() {
                    @Override
                    public void initPlayer() {
                        super.initPlayer();
                        //支持concat
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                                "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
                        //支持rtsp
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                    }
                })
                .build());
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
}
