package com.dueeeke.dkplayer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.videoplayer.player.IjkVideoView;

import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 包含弹幕的播放器
 * Created by xinyu on 2017/12/23.
 */

public class DanmukuVideoView extends IjkVideoView {
    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;


    public DanmukuVideoView(@NonNull Context context) {
        super(context);
    }

    public DanmukuVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmukuVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initPlayer() {
        super.initPlayer();
        if (mDanmakuView != null) {
            playerContainer.removeView(mDanmakuView);
            playerContainer.addView(mDanmakuView, 1);
        }
    }


    @Override
    protected void startPrepare() {
        super.startPrepare();
        if (mDanmakuView != null) {
            mDanmakuView.prepare(mParser, mContext);
        }
    }

    @Override
    protected void startInPlaybackState() {
        super.startInPlaybackState();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (isInPlaybackState()) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.pause();
            }
        }
    }

    @Override
    public void resume() {
        super.resume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void release() {
        super.release();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void seekTo(int pos) {
        super.seekTo(pos);
        if (isInPlaybackState()) {
            if (mDanmakuView != null) mDanmakuView.seekTo((long) pos);
        }
    }

    /**
     * 添加弹幕
     */
    public IjkVideoView addDanmukuView(DanmakuView danmakuView, DanmakuContext context, BaseDanmakuParser parser) {
        this.mDanmakuView = danmakuView;
        this.mContext = context;
        this.mParser = parser;
        return this;
    }
}
