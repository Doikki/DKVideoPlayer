package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

/**
 * 多开
 */
public class ParallelPlayActivity extends AppCompatActivity {

    private static final String VOD_URL = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallel_play);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        VideoView player1 = findViewById(R.id.player_1);
        player1.setUrl(VOD_URL);

        //必须设置
        player1.setEnableAudioFocus(false);
        StandardVideoController controller1 = new StandardVideoController(this);
        controller1.addDefaultControlComponent(getString(R.string.str_multi_player), false);
        player1.setVideoController(controller1);

        VideoView player2 = findViewById(R.id.player_2);
        player2.setUrl(VOD_URL);
        //必须设置
        player2.setEnableAudioFocus(false);
        StandardVideoController controller2 = new StandardVideoController(this);
        controller2.addDefaultControlComponent(getString(R.string.str_multi_player), false);
        player2.setVideoController(controller2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.instance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoViewManager.instance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoViewManager.instance().release();
    }

    @Override
    public void onBackPressed() {
        if (VideoViewManager.instance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
