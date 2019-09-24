package com.dueeeke.dkplayer.activity.extend;

import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.PlayerFactory;

/**
 * 多播放器切换
 * Created by Devlin_n on 2017/4/7.
 */

public class SwitchPlayerActivity extends BaseActivity implements View.OnClickListener {

    private StandardVideoController mController;
    private static final String URL = "http://cdnxdc.tanzi88.com/XDC/dvideo/2017/12/29/fc821f9a8673d2994f9c2cb9b27233a3.mp4";
//    private static final String URL = "http://zaixian.jingpin88.com/20180430/IGBXbalb/index.m3u8";
//    private static final String URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_switch_player;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_switch_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        findViewById(R.id.btn_ijk).setOnClickListener(this);
        findViewById(R.id.btn_media).setOnClickListener(this);
        findViewById(R.id.btn_exo).setOnClickListener(this);

        mController = new StandardVideoController(this);
        mVideoView.setUrl(URL);
        mVideoView.setVideoController(mController);
        mVideoView.start();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        PlayerFactory factory = null;
        switch (id) {
            case R.id.btn_ijk:
                factory = IjkPlayerFactory.create(this);
                break;
            case R.id.btn_media:
                factory = AndroidMediaPlayerFactory.create(this);
                break;
            case R.id.btn_exo:
                factory = ExoMediaPlayerFactory.create(this);
                break;
        }

        mVideoView.release();
        mVideoView.setUrl(URL);
        mVideoView.setVideoController(mController);
        mVideoView.setPlayerFactory(factory);
        mVideoView.start();

    }

}
