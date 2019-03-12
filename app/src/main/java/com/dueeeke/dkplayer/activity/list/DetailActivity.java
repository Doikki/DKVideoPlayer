package com.dueeeke.dkplayer.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.SeamlessPlayHelper;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

public class DetailActivity extends AppCompatActivity {

    private IjkVideoView mIjkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("无缝播放详情");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //拿到IjkVideoView单例实例
        mIjkVideoView = SeamlessPlayHelper.getInstance().getIjkVideoView();
        //如果已经添加到某个父容器，就将其移除
        removePlayerFormParent();
        //设置新的控制器
        StandardVideoController standardVideoController = new StandardVideoController(this);
        mIjkVideoView.setVideoController(standardVideoController);

        Intent intent = getIntent();
        boolean seamlessPlay = intent.getBooleanExtra(IntentKeys.SEAMLESS_PLAY, false);
        if (seamlessPlay) {
            //无缝播放需还原Controller状态
            standardVideoController.setPlayState(mIjkVideoView.getCurrentPlayState());
            standardVideoController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
        } else {
            //不是无缝播放的情况
            String title = intent.getStringExtra(IntentKeys.TITLE);
            standardVideoController.setTitle(title);
            String url = intent.getStringExtra(IntentKeys.URL);
            mIjkVideoView.setUrl(url);
            mIjkVideoView.start();
        }
        //还原有声音状态
        if (mIjkVideoView.isMute()) mIjkVideoView.setMute(false);
        //把播放器添加到页面的容器中
        FrameLayout playerContainer = findViewById(R.id.player_container);
        playerContainer.addView(mIjkVideoView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIjkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIjkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removePlayerFormParent();
        //移除Controller
        mIjkVideoView.setVideoController(null);
        mIjkVideoView.release();
    }

    /**
     * 将播放器从父控件中移除
     */
    private void removePlayerFormParent() {
        ViewParent parent = mIjkVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(mIjkVideoView);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIjkVideoView.onBackPressed()) {
            super.onBackPressed();
        }

    }
}
