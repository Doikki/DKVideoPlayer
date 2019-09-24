package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.controller.PadController;

public class PadActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pad;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);

        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");

        mVideoView.setVideoController(new PadController(this));

        mVideoView.start();
    }
}
