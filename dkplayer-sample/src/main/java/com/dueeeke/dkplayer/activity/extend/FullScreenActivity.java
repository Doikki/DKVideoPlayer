package com.dueeeke.dkplayer.activity.extend;

import android.content.pm.ActivityInfo;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.component.FullScreenControlView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 全屏播放
 * Created by dueeeke on 2017/4/21.
 */

public class FullScreenActivity extends BaseActivity<VideoView> {

    private StandardVideoController mController;

    @Override
    protected View getContentView() {
        mVideoView = new VideoView(this);
        return mVideoView;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_fullscreen_directly;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView.startFullScreen();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
        mController = new StandardVideoController(this);
        mController.addControlComponent(new CompleteView(this));
        mController.addControlComponent(new ErrorView(this));
        mController.addControlComponent(new PrepareView(this));
        TitleView titleView = new TitleView(this);
        titleView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setTitle(getString(R.string.str_fullscreen_directly));
        mController.addControlComponent(titleView);
        mController.addControlComponent(new FullScreenControlView(this));
        mVideoView.setVideoController(mController);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
        mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        if (!mController.isLocked()) {
            finish();
        }
    }
}
