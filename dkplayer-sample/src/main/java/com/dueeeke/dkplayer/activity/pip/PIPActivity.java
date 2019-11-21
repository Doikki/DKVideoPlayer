package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.util.Tag;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.yanzhenjie.permission.AndPermission;

public class PIPActivity extends BaseActivity {

    private PIPManager mPIPManager;
    private static final String URL = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip_demo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FrameLayout playerContainer = findViewById(R.id.player_container);
        mPIPManager = PIPManager.getInstance();
        VideoView videoView = getVideoViewManager().get(Tag.PIP);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent(getString(R.string.str_pip), false);
        videoView.setVideoController(controller);
        if (mPIPManager.isStartFloatWindow()) {
            mPIPManager.stopFloatWindow();
            controller.setPlayerState(videoView.getCurrentPlayerState());
            controller.setPlayState(videoView.getCurrentPlayState());
        } else {
            mPIPManager.setActClass(PIPActivity.class);
            ImageView thumb = controller.findViewById(R.id.thumb);
            Glide.with(this)
                    .load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg")
                    .asBitmap()
                    .animate(R.anim.anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .into(thumb);
            videoView.setUrl(URL);
        }
        playerContainer.addView(videoView);
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
        mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPIPManager.reset();
    }


    @Override
    public void onBackPressed() {
        if (mPIPManager.onBackPress()) return;
        super.onBackPressed();
    }

    public void startFloatWindow(View view) {

        AndPermission
                .with(this)
                .overlay()
                .onGranted(data -> {
                    mPIPManager.startFloatWindow();
                    finish();
                })
                .onDenied(data -> {

                })
                .start();
    }
}
