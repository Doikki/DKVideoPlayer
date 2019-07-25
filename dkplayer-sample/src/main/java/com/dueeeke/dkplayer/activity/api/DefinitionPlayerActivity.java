package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.DefinitionController;
import com.dueeeke.dkplayer.widget.videoview.DefinitionVideoView;
import com.dueeeke.videoplayer.ijk.IjkPlayer;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;

import java.util.LinkedHashMap;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class DefinitionPlayerActivity extends AppCompatActivity {

    private DefinitionVideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_definition);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVideoView = findViewById(R.id.player);

        DefinitionController controller = new DefinitionController(this);
        controller.setTitle("韩雪：积极的悲观主义者");
//        mVideoView.setCustomMediaPlayer(new IjkPlayer(this) {
//            @Override
//            public void setOptions() {
//                //精准seek
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//            }
//        });

        mVideoView.setPlayerFactory(new PlayerFactory() {
            @Override
            public AbstractPlayer createPlayer() {
                return new IjkPlayer(DefinitionPlayerActivity.this) {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    }
                };
            }
        });

        LinkedHashMap<String, String> videos = new LinkedHashMap<>();
        videos.put("标清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_sd.flv");
        videos.put("高清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_hd.flv");
        videos.put("超清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_shd.flv");
        mVideoView.setDefinitionVideos(videos);
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
