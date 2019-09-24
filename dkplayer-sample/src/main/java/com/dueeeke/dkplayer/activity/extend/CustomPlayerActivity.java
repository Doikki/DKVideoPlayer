package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.player.CacheExoMediaPlayer;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;

/**
 * 自定义MediaPlayer
 */
public class CustomPlayerActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_custom_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.vv);

        mVideoView.setPlayerFactory(new PlayerFactory() {
            @Override
            public AbstractPlayer createPlayer() {
                return new CacheExoMediaPlayer(CustomPlayerActivity.this);
            }
        });

        mVideoView.setUrl("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
        mVideoView.setVideoController(new StandardVideoController(this));
        mVideoView.start();
    }
}
