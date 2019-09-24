package com.dueeeke.dkplayer.activity.extend;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.List;

/**
 * 连续播放一个列表
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayListActivity extends BaseActivity {

    private VideoView mVideoView;

    private List<VideoBean> data = DataUtil.getVideoList();

    private StandardVideoController mStandardVideoController;

    @Override
    protected View getContentView() {
        mVideoView = new VideoView(this);
        mVideoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PlayerUtils.dp2px(this, 240)));
        setContentView(mVideoView);
        return mVideoView;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_play_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mStandardVideoController = new StandardVideoController(this);

        //加载第一条数据
        VideoBean videoBean = data.get(0);
        mVideoView.setUrl(videoBean.getUrl());
        mStandardVideoController.setTitle(videoBean.getTitle());
        mVideoView.setVideoController(mStandardVideoController);

        //监听播放结束
        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            private int mCurrentVideoPosition;
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

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
                        mStandardVideoController.setTitle(videoBean.getTitle());
                        mVideoView.setVideoController(mStandardVideoController);
                        //开始播放
                        mVideoView.start();
                    }
                }
            }
        });

        mVideoView.start();
    }
}
