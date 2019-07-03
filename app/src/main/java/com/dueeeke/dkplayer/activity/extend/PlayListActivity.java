package com.dueeeke.dkplayer.activity.extend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
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

public class PlayListActivity extends AppCompatActivity {

    private VideoView ijkVideoView;

    private List<VideoBean> data = DataUtil.getVideoList();

    private StandardVideoController mStandardVideoController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ijkVideoView = new VideoView(this);
        setContentView(ijkVideoView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PlayerUtils.dp2px(this, 240)));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_play_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mStandardVideoController = new StandardVideoController(this);

        //加载第一条数据
        VideoBean videoBean = data.get(0);
        ijkVideoView.setUrl(videoBean.getUrl());
        mStandardVideoController.setTitle(videoBean.getTitle());
        ijkVideoView.setVideoController(mStandardVideoController);

        //监听播放结束
        ijkVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
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
                        ijkVideoView.release();
                        //重新设置数据
                        VideoBean videoBean = data.get(mCurrentVideoPosition);
                        ijkVideoView.setUrl(videoBean.getUrl());
                        mStandardVideoController.setTitle(videoBean.getTitle());
                        ijkVideoView.setVideoController(mStandardVideoController);
                        //开始播放
                        ijkVideoView.start();
                    }
                }
            }
        });

        ijkVideoView.start();

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
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
