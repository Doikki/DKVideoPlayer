package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 抖音
 * Created by xinyu on 2018/1/6.
 */

public class DouYinController extends BaseVideoController {

    private ImageView thumb;
    public DouYinController(@NonNull Context context) {
        super(context);
    }

    public DouYinController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DouYinController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_douyin_controller;
    }

    @Override
    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        thumb = controllerView.findViewById(R.id.iv_thumb);
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);

        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                thumb.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_PLAYING:
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        thumb.setVisibility(GONE);
                    }
                }, 500);
                break;
        }
    }

    public ImageView getThumb() {
        return thumb;
    }
}
