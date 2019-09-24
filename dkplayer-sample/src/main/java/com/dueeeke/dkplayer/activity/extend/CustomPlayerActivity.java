package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.ExoCacheVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.exo.ExoMediaPlayer;
import com.dueeeke.videoplayer.player.AbstractPlayer;

/**
 * 自定义MediaPlayer，有多种情形：
 * 第一：集成某个现成的MediaPlayer，对其功能进行扩张，此demo就演示了通过继承{@link ExoMediaPlayer}
 * 对其扩展边播边存的功能。
 * 第二：通过继承{@link AbstractPlayer}扩展一些其他的播放器。
 */
public class CustomPlayerActivity extends BaseActivity<ExoCacheVideoView> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_custom_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.vv);
        mVideoView.setUrl("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
        mVideoView.setVideoController(new StandardVideoController(this));
        mVideoView.start();
    }
}
