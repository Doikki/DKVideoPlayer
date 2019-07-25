package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.RotateInFullscreenController;
import com.dueeeke.dkplayer.widget.videoview.RotateVideoView;

public class RotateInFullscreenActivity extends AppCompatActivity{

    private RotateVideoView mVideoView;
    private RotateInFullscreenController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_extend_rotate_in_fullscreen);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_rotate_in_fullscreen);
        mVideoView = findViewById(R.id.player);
        mController = new RotateInFullscreenController(this);
        mVideoView.setVideoController(mController);
//        mVideoView.setPlayerConfig(new VideoViewConfig.Builder().enableMediaCodec().build());
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String url = "file://"  + path + File.separator + "test.mp4";
//        String url = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
        String url = "https://aweme.snssdk.com/aweme/v1/play/?video_id=374e166692ee4ebfae030ceae117a9d0&line=0&ratio=720p&media_type=4&vr_type=0";
        mVideoView.setUrl(url);
        mVideoView.start();
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

        if (mVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
