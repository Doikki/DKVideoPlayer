package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

/**
 * 连续播放一个列表
 * Created by dueeeke on 2017/4/7.
 */

public class PlayListActivity extends BaseActivity {

    private List<VideoBean> data = DataUtil.getVideoList();

    private StandardVideoController mController;
    private TitleView mTitleView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_layout_common;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_play_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);
        mController = new StandardVideoController(this);
        addControlComponents();

        //加载第一条数据
        VideoBean videoBean = data.get(0);
        mVideoView.setUrl(videoBean.getUrl());
        mTitleView.setTitle(videoBean.getTitle());
        mVideoView.setVideoController(mController);

        //监听播放结束
        mVideoView.addOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            private int mCurrentVideoPosition;
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (data != null) {
                        mCurrentVideoPosition++;
                        if (mCurrentVideoPosition >= data.size()) return;
                        mVideoView.release();
                        //重新设置数据
                        VideoBean videoBean = data.get(mCurrentVideoPosition);
                        mVideoView.setUrl(videoBean.getUrl());
                        mTitleView.setTitle(videoBean.getTitle());
                        mVideoView.setVideoController(mController);
                        //开始播放
                        mVideoView.start();
                    }
                }
            }
        });

        mVideoView.start();
    }

    private void addControlComponents() {
        CompleteView completeView = new CompleteView(this);
        ErrorView errorView = new ErrorView(this);
        PrepareView prepareView = new PrepareView(this);
        prepareView.setClickStart();
        mTitleView = new TitleView(this);
        VodControlView vodControlView = new VodControlView(this);
        GestureView gestureView = new GestureView(this);
        mController.addControlComponent(completeView, errorView, prepareView, mTitleView, vodControlView, gestureView);
    }
}
