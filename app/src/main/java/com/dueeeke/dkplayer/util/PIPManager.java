package com.dueeeke.dkplayer.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.dueeeke.dkplayer.app.MyApplication;
import com.dueeeke.dkplayer.widget.FloatView;
import com.dueeeke.dkplayer.widget.controller.FloatController;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2018/3/30.
 */

public class PIPManager {

    private static PIPManager instance;
    private IjkVideoView ijkVideoView;
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
        ijkVideoView = new IjkVideoView(MyApplication.getInstance());
//        ijkVideoView.setVideoListener(mMyVideoListener);
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

    public IjkVideoView getIjkVideoView() {
        return ijkVideoView;
    }

    public void startFloatWindow() {
        if (isShowing) return;
        removePlayerFormParent();
        mFloatController.setPlayState(ijkVideoView.getCurrentPlayState());
        mFloatController.setPlayerState(ijkVideoView.getCurrentPlayerState());
        ijkVideoView.setVideoController(mFloatController);
        floatView.addView(ijkVideoView);
        floatView.addToWindow();
        isShowing = true;
    }

    public void stopFloatWindow() {
        if (!isShowing) return;
        floatView.removeFromWindow();
        removePlayerFormParent();
        isShowing = false;
    }

    private void removePlayerFormParent() {
        ViewParent parent = ijkVideoView.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(ijkVideoView);
        }
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (isShowing) return;
        ijkVideoView.pause();
    }

    public void resume() {
        if (isShowing) return;
        ijkVideoView.resume();
    }

    public void reset() {
        if (isShowing) return;
        removePlayerFormParent();
        ijkVideoView.setVideoController(null);
        ijkVideoView.release();
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !isShowing && ijkVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return isShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (isShowing) {
            ijkVideoView.resume();
            floatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

//    private class KeyReceiver extends BroadcastReceiver {
//
//        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
//        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action == null) return;
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//
//                if (reason == null)
//                    return;
//
//                // Home键
//                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                    if (isShowing) {
//                        ijkVideoView.pause();
//                        floatView.setVisibility(View.GONE);
//                    }
//                }
//
//                // 最近任务列表键
//                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
//                    if (isShowing) {
//                        ijkVideoView.pause();
//                        floatView.setVisibility(View.GONE);
//                    }
//                }
//            }
//        }
//    }


}
