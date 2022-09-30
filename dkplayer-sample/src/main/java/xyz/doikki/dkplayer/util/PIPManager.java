package xyz.doikki.dkplayer.util;

import android.view.View;

import xyz.doikki.dkplayer.app.MyApplication;
import xyz.doikki.dkplayer.widget.FloatView;
import xyz.doikki.dkplayer.widget.controller.FloatController;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.DKManager;

/**
 * 悬浮播放
 * Created by Doikki on 2018/3/30.
 */

public class PIPManager {

    private static PIPManager instance;
    private final DKVideoView mVideoView;
    private final FloatView mFloatView;
    private final FloatController mFloatController;
    private boolean mIsShowing;
    private int mPlayingPosition = -1;
    private Class mActClass;


    private PIPManager() {
        mVideoView = new DKVideoView(MyApplication.getInstance());
        DKManager.add(mVideoView, Tag.PIP);
        mFloatController = new FloatController(MyApplication.getInstance());
        mFloatView = new FloatView(MyApplication.getInstance(), 0, 0);
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

    public void startFloatWindow() {
        if (mIsShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mVideoView.setVideoController(mFloatController);
        mFloatController.setPlayerState(mVideoView.getPlayerState());
        mFloatController.setScreenMode(mVideoView.getScreenMode());
        mFloatView.addView(mVideoView);
        mFloatView.addToWindow();
        mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (!mIsShowing) return;
        mFloatView.removeFromWindow();
        Utils.removeViewFormParent(mVideoView);
        mIsShowing = false;
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (mIsShowing) return;
        mVideoView.pause();
    }

    public void resume() {
        if (mIsShowing) return;
        mVideoView.resume();
    }

    public void reset() {
        if (mIsShowing) return;
        Utils.removeViewFormParent(mVideoView);
        mVideoView.release();
        mVideoView.setVideoController(null);
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !mIsShowing && mVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return mIsShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (mIsShowing) {
            mVideoView.resume();
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}
