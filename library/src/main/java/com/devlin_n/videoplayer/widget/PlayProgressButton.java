package com.devlin_n.videoplayer.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.devlin_n.videoplayer.R;
import com.devlin_n.videoplayer.util.WindowUtil;

/**
 * 播放按钮和加载进度条封装
 * Created by Devlin_n on 2017/6/9.
 */

public class PlayProgressButton extends FrameLayout {


    public static final int STATE_PLAYING = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_LOADING = 3;
    public static final int STATE_LOADING_END = 4;
    private int currentState;
    private boolean mShowing;
    private ImageView playButton;
    private ProgressBar progressBar;


    public PlayProgressButton(@NonNull Context context) {
        this(context, null);
    }

    public PlayProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayProgressButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        LayoutParams params = new LayoutParams(WindowUtil.dp2px(getContext(), 50), WindowUtil.dp2px(getContext(), 50));

        playButton = new ImageView(getContext());
        playButton.setImageResource(R.drawable.selector_play_button);
        playButton.setBackgroundResource(R.drawable.shape_play_bg);
        int playPadding = WindowUtil.dp2px(getContext(), 10);
        playButton.setPadding(playPadding, playPadding, playPadding, playPadding);
        addView(playButton, params);

        progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_loading));
        progressBar.setVisibility(GONE);
        addView(progressBar, params);

    }


    public void setState(int state) {
        switch (state) {
            case STATE_PLAYING:
                currentState = STATE_PLAYING;
                progressBar.setVisibility(GONE);
                playButton.setSelected(true);
                playButton.setVisibility(VISIBLE);
                break;
            case STATE_PAUSE:
                currentState = STATE_PAUSE;
                progressBar.setVisibility(GONE);
                playButton.setSelected(false);
                playButton.setVisibility(VISIBLE);
                break;
            case STATE_LOADING:
                currentState = STATE_LOADING;
                playButton.setVisibility(GONE);
                progressBar.setVisibility(VISIBLE);
                break;
            case STATE_LOADING_END:
                currentState = STATE_LOADING_END;
                playButton.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                break;
        }
    }

    public void show() {
        if (mShowing) return;
        setVisibility(VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
    }

    public void hide() {
        if (!mShowing) return;
        if (currentState == STATE_LOADING) return;
        setVisibility(GONE);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));

    }

    public void setPlayButtonClickListener(OnClickListener listener) {
        playButton.setOnClickListener(listener);
    }

    @Override
    public void setVisibility(int visibility) {
        mShowing = visibility == VISIBLE;
        super.setVisibility(visibility);
    }
}
