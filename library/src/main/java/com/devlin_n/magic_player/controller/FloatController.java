package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.player.MagicVideoView;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController {

    private ProgressBar bufferProgress;


    public FloatController(@NonNull Context context) {
        super(context);
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_float_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        bufferProgress = (ProgressBar) controllerView.findViewById(R.id.buffering);
    }

    @Override
    public void setPlayState(int playState) {
        switch (playState) {
            case MagicVideoView.STATE_IDLE:
                break;
            case MagicVideoView.STATE_PLAYING:
                break;
            case MagicVideoView.STATE_PAUSED:
                break;
            case MagicVideoView.STATE_PREPARING:
                bufferProgress.setVisibility(VISIBLE);
                break;
            case MagicVideoView.STATE_PREPARED:
                bufferProgress.setVisibility(GONE);
                break;
            case MagicVideoView.STATE_ERROR:
                break;
            case MagicVideoView.STATE_BUFFERING:
                bufferProgress.setVisibility(VISIBLE);
                break;
            case MagicVideoView.STATE_BUFFERED:
                bufferProgress.setVisibility(GONE);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
