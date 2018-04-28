package com.dueeeke.dkplayer.activity.extend;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.dkplayer.widget.videoview.RotateIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.WindowUtil;

public class RotateInFullscreenActivity extends AppCompatActivity{

    private RotateIjkVideoView mIjkVideoView;
    private MyController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_in_fullscreen);
        mIjkVideoView = findViewById(R.id.player);
        mController = new MyController(this);
        mIjkVideoView.setUrl("http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv");
        mIjkVideoView.setOnClickListener(v -> {
            mIjkVideoView.startFullScreen();
            mController.setPlayState(mIjkVideoView.getCurrentPlayState());
            mController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
            mIjkVideoView.setVideoController(mController);
        });
        mIjkVideoView.start();
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
        mIjkVideoView.release();
    }

    @Override
    public void onBackPressed() {

        if (mIjkVideoView.onBackPressed()) {
            return;
        }
        super.onBackPressed();

    }

    private class MyController extends StandardVideoController {

        private boolean isLandscape;

        public MyController(@NonNull Context context) {
            super(context);
        }

        public MyController(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public MyController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        @Override
        protected void doStartStopFullScreen() {
            if (isLandscape) {
                WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isLandscape = false;
            } else {
                WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                isLandscape = true;
            }
            fullScreenButton.setSelected(isLandscape);
        }

        @Override
        public void setPlayerState(int playerState) {
            super.setPlayerState(playerState);
            switch (playerState) {
                case IjkVideoView.PLAYER_FULL_SCREEN:
                    fullScreenButton.setSelected(false);
                    break;
            }

        }

        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.fullscreen) {
                doStartStopFullScreen();
            } else if (i == R.id.lock) {
                doLockUnlock();
            } else if (i == R.id.iv_play || i == R.id.thumb || i == R.id.iv_replay) {
                doPauseResume();
            } else if (i == R.id.back) {
                if (isLandscape) WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mediaPlayer.stopFullScreen();
            }
        }

        @Override
        public boolean onBackPressed() {
            if (isLocked) {
                show();
                Toast.makeText(getContext(), R.string.lock_tip, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (mediaPlayer.isFullScreen()) {
                if (isLandscape) WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mediaPlayer.stopFullScreen();
                setPlayerState(IjkVideoView.PLAYER_NORMAL);
                return true;
            }
            return super.onBackPressed();
        }
    }
}
