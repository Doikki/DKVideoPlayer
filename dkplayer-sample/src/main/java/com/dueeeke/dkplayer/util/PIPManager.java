package com.dueeeke.dkplayer.util;

import android.view.View;

import com.dueeeke.dkplayer.app.MyApplication;
import com.dueeeke.dkplayer.widget.FloatView;
import com.dueeeke.dkplayer.widget.controller.FloatController;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2018/3/30.
 */

public class PIPManager {

    private static PIPManager instance;
    private VideoView mVideoView;
    private FloatView floatView;
    private FloatController mFloatController;
    private boolean isShowing;
//    private KeyReceiver mKeyReceiver;
    private int mPlayingPosition = -1;
    private Class mActClass;
//    private MyVideoListener mMyVideoListener = new MyVideoListener() {
//        @Override
//        public void onComplete() {
//            super.onComplete();
//            reset();
//        }
//    };


    private PIPManager() {
        mVideoView = new VideoView(MyApplication.getInstance());
//        mVideoView.setVideoListener(mMyVideoListener);
//        mKeyReceiver = new KeyReceiver();
        mFloatController = new FloatController(MyApplication.getInstance());
//        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        MyApplication.getInstance().registerReceiver(mKeyReceiver, homeFilter);
        floatView = new FloatView(MyApplication.getInstance(), 0, 0);
    }

    public static PIPManager getInstance() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }

    public VideoView getVideoView() {
        return mVideoView;
    }

    public void startFloatWindow() {
        if (isShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mFloatController.setPlayState(mVideoView.getCurrentPlayState());
        mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
        mVideoView.setVideoController(mFloatController);
        floatView.addView(mVideoView);
        floatView.addToWindow();
        isShowing = true;
    }

    public void stopFloatWindow() {
        if (!isShowing) return;
        floatView.removeFromWindow();
        Utils.removeViewFormParent(mVideoView);
        isShowing = false;
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (isShowing) return;
        mVideoView.pause();
    }

    public void resume() {
        if (isShowing) return;
        mVideoView.resume();
    }

    public void reset() {
        if (isShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mVideoView.release();
        mVideoView.setVideoController(null);
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !isShowing && mVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return isShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (isShowing) {
            mVideoView.resume();
            floatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}
