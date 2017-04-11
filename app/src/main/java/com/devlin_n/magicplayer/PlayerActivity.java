package com.devlin_n.magicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devlin_n.magic_player.IjkMediaController;
import com.devlin_n.magic_player.IjkVideoView;

/**
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ijkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
        ijkVideoView.setAutoPlay(true);
        ijkVideoView.setUrl("http://videofile2.cutv.com/mg/010002_t/2017/03/20/G18/G18ijiiikjmriikmropy2o_cug.mp4.m3u8");
        IjkMediaController ijkMediaController = new IjkMediaController(this);
        ijkMediaController.setLive(false);
        ijkVideoView.setIjkMediaController(ijkMediaController);
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        ijkVideoView.pause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        ijkVideoView.start();
//    }

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
}
