package com.dueeeke.dkplayer.widget.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;

/**
 * 调试信息
 */
public class DebugInfoView extends AppCompatTextView implements IControlComponent {

    private ControlWrapper mControlWrapper;

    public DebugInfoView(Context context) {
        super(context);
    }

    public DebugInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackgroundResource(android.R.color.black);
        setTextSize(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(lp);
    }


    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        setText(getDebugString(playState));
        bringToFront();
    }

    /**
     * Returns the debugging information string to be shown by the target {@link TextView}.
     */
    protected String getDebugString(int playState) {
        return getCurrentPlayer() + Utils.playState2str(playState) + "\n"
                + "video width: " + mControlWrapper.getVideoSize()[0] + " , height: " + mControlWrapper.getVideoSize()[1];
    }

    protected String getCurrentPlayer() {
        String player;
        Object playerFactory = Utils.getCurrentPlayerFactory();
        if (playerFactory instanceof ExoMediaPlayerFactory) {
            player = "ExoPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else if (playerFactory instanceof AndroidMediaPlayerFactory) {
            player = "MediaPlayer";
        } else {
            player = "unknown";
        }
        return String.format("player: %s ", player);
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        bringToFront();
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
