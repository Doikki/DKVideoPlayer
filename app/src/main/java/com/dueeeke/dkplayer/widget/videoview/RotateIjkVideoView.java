package com.dueeeke.dkplayer.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.WindowUtil;

public class RotateIjkVideoView extends IjkVideoView {
    public RotateIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public RotateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void startFullScreen() {
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity == null) return;
        if (mIsFullScreen) return;
        WindowUtil.hideSystemBar(getContext());
        this.removeView(mPlayerContainer);
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mPlayerContainer, params);
        mIsFullScreen = true;
        setPlayerState(PLAYER_FULL_SCREEN);
    }

    @Override
    public void stopFullScreen() {
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity == null) return;
        if (!mIsFullScreen) return;
        if (mVideoController != null) mVideoController.hide();
        WindowUtil.showSystemBar(getContext());
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);
        mIsFullScreen = false;
        setPlayerState(PLAYER_NORMAL);
    }
}
