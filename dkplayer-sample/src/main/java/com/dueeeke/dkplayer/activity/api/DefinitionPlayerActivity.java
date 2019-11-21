package com.dueeeke.dkplayer.activity.api;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.component.DefinitionControlView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videoplayer.player.AndroidMediaPlayer;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.LinkedHashMap;

/**
 * 清晰度切换
 * Created by dueeeke on 2017/4/7.
 */

public class DefinitionPlayerActivity extends BaseActivity<VideoView<AndroidMediaPlayer>> implements DefinitionControlView.OnRateSwitchListener {

    private StandardVideoController mController;
    private DefinitionControlView mDefinitionControlView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_layout_common;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_definition;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);

        mController = new StandardVideoController(this);
        addControlComponents();

        LinkedHashMap<String, String> videos = new LinkedHashMap<>();
        videos.put("标清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_sd.flv");
        videos.put("高清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_hd.flv");
        videos.put("超清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_shd.flv");
        //由于exoplayer不支持flv seek操作，现切换成MediaPlayer来演示功能
        mVideoView.setPlayerFactory(AndroidMediaPlayerFactory.create());
        mDefinitionControlView.setData(videos);
        mVideoView.setVideoController(mController);
        mVideoView.setUrl(videos.get("标清"));//默认播放标清
        mVideoView.start();
    }

    private void addControlComponents() {
        CompleteView completeView = new CompleteView(this);
        ErrorView errorView = new ErrorView(this);
        PrepareView prepareView = new PrepareView(this);
        prepareView.setClickStart();
        TitleView titleView = new TitleView(this);
        mDefinitionControlView = new DefinitionControlView(this);
        mDefinitionControlView.setOnRateSwitchListener(this);
        GestureView gestureView = new GestureView(this);
        mController.addControlComponent(completeView, errorView, prepareView, titleView, mDefinitionControlView, gestureView);
    }

    @Override
    public void onRateChange(String url) {
        mVideoView.setUrl(url);
        mVideoView.replay(false);
    }
}
