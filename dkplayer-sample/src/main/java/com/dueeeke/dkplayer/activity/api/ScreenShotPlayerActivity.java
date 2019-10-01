package com.dueeeke.dkplayer.activity.api;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.videocontroller.StandardVideoController;

/**
 * 截图
 * Created by Devlin_n on 2017/4/7.
 */

public class ScreenShotPlayerActivity extends BaseActivity {

    private ImageView mScreenShot;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_screen_shot_player;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_screen_shot;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        mScreenShot = findViewById(R.id.iv_screen_shot);
        StandardVideoController controller = new StandardVideoController(this);
        mVideoView.setUrl("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4");
        mVideoView.setVideoController(controller);
        mVideoView.start();
    }

    public void doScreenShot(View view) {
        Bitmap bitmap = mVideoView.doScreenShot();
        mScreenShot.setImageBitmap(bitmap);
    }
}
