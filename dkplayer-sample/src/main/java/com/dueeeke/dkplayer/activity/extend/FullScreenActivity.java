package com.dueeeke.dkplayer.activity.extend;

import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.controller.FullScreenController;
import com.dueeeke.dkplayer.widget.videoview.FullScreenVideoView;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends BaseActivity<FullScreenVideoView> {

    @Override
    protected View getContentView() {
        mVideoView = new FullScreenVideoView(this);
        return mVideoView;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_fullscreen_directly;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
        FullScreenController controller = new FullScreenController(this);
        controller.setTitle("这是一个标题");
        mVideoView.setVideoController(controller);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
        mVideoView.start();
    }
}
