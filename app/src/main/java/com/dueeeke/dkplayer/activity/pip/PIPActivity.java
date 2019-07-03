package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.yanzhenjie.permission.AndPermission;

public class PIPActivity extends AppCompatActivity{
    private PIPManager mPIPManager;
//    private static final String URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    private static final String URL = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
//    private static final String URL = "http://youku163.zuida-bofang.com/20190126/26805_c313a74d/index.m3u8";

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
        VideoView videoView = mPIPManager.getVideoView();
        StandardVideoController controller = new StandardVideoController(this);
        videoView.setVideoController(controller);
        if (mPIPManager.isStartFloatWindow()) {
            mPIPManager.stopFloatWindow();
            controller.setPlayerState(videoView.getCurrentPlayerState());
            controller.setPlayState(videoView.getCurrentPlayState());
        } else {
            mPIPManager.setActClass(PIPActivity.class);
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        videoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));
            Glide.with(this)
                    .load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg")
                    .asBitmap()
                    .animate(R.anim.dkplayer_anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .into(controller.getThumb());
            videoView.setUrl(URL);
            controller.setTitle("香港卫视");
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
