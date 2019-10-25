package com.dueeeke.dkplayer.activity.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.SeamlessPlayHelper;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

public class DetailActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("无缝播放详情");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //拿到VideoView单例实例
        mVideoView = SeamlessPlayHelper.getInstance().getVideoView();
        //如果已经添加到某个父容器，就将其移除
        removePlayerFormParent();
        //设置新的控制器
        StandardVideoController standardVideoController = new StandardVideoController(this);
        mVideoView.setVideoController(standardVideoController);

        Intent intent = getIntent();
        boolean seamlessPlay = intent.getBooleanExtra(IntentKeys.SEAMLESS_PLAY, false);
        if (seamlessPlay) {
            //无缝播放需还原Controller状态
            standardVideoController.setPlayState(mVideoView.getCurrentPlayState());
            standardVideoController.setPlayerState(mVideoView.getCurrentPlayerState());
        } else {
            //不是无缝播放的情况
            String title = intent.getStringExtra(IntentKeys.TITLE);
            standardVideoController.setTitle(title);
            String url = intent.getStringExtra(IntentKeys.URL);
            mVideoView.setUrl(url);
            mVideoView.start();
        }
        //还原有声音状态
        if (mVideoView.isMute()) mVideoView.setMute(false);
        //把播放器添加到页面的容器中
        FrameLayout playerContainer = findViewById(R.id.player_container);
        playerContainer.addView(mVideoView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removePlayerFormParent();
        //移除Controller
        mVideoView.setVideoController(null);
        mVideoView.release();
    }

    /**
     * 将播放器从父控件中移除
     */
    private void removePlayerFormParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(mVideoView);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }

    }
}
