package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

public class MultiPlayerActivity extends AppCompatActivity{

    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private IjkVideoView mPlayer1;
    private IjkVideoView mPlayer2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        mPlayer1 = findViewById(R.id.player_1);
        mPlayer1.setUrl(VOD_URL);

        mPlayer1.setPlayerConfig(new PlayerConfig.Builder().disableAudioFocus().build());
        StandardVideoController controller1 = new StandardVideoController(this);
        mPlayer1.setVideoController(controller1);

        mPlayer2 = findViewById(R.id.player_2);
        mPlayer2.setUrl(VOD_URL);
        mPlayer2.setPlayerConfig(new PlayerConfig.Builder().disableAudioFocus().build());
        StandardVideoController controller2 = new StandardVideoController(this);
        mPlayer2.setVideoController(controller2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer1.pause();
        mPlayer2.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer1.resume();
        mPlayer2.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer1.release();
        mPlayer2.release();
    }
}
