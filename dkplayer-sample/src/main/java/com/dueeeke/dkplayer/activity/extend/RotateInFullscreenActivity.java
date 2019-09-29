package com.dueeeke.dkplayer.activity.extend;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.controller.RotateInFullscreenController;

public class RotateInFullscreenActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_rotate_in_fullscreen;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_extend_rotate_in_fullscreen;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        RotateInFullscreenController controller = new RotateInFullscreenController(this);
        mVideoView.setVideoController(controller);
//        mVideoView.setPlayerConfig(new VideoViewConfig.Builder().enableMediaCodec().build());
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String url = "file://"  + path + File.separator + "test.mp4";
//        String url = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
        String url = "https://aweme.snssdk.com/aweme/v1/play/?video_id=374e166692ee4ebfae030ceae117a9d0&line=0&ratio=720p&media_type=4&vr_type=0";
        mVideoView.setUrl(url);
        mVideoView.start();
    }
}
