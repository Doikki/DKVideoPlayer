package com.devlin_n.magic_player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * 控制条
 * Created by Devlin_n on 2017/4/7.
 */

public abstract class BaseIjkMediaController extends FrameLayout {

    protected static final String TAG = BaseIjkMediaController.class.getSimpleName();
    protected MediaPlayerControlInterface mediaPlayer;
    protected ImageView startButton;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer,topContainer;
    protected View controllerView;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;


    public BaseIjkMediaController(@NonNull Context context) {
        super(context);
    }

    public BaseIjkMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void initView(Context context){
        controllerView = LayoutInflater.from(context).inflate(getControllerViewLayout(), this);
    }

    protected void startPlayLogic(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startButton.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            startButton.setImageResource(R.drawable.ic_pause);
        }
    }

    protected void startFloatScreen(){
        mediaPlayer.startFloatScreen();
    }

    protected void startFullScreen() {
        mediaPlayer.startFullScreen();
        updateFullScreen();
    }

    protected abstract int getControllerViewLayout();

    protected abstract void show();

    protected abstract void hide();

    protected abstract void reset();

    protected abstract void updateFullScreen();

    protected interface MediaPlayerControlInterface {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        void startFloatScreen();

        void startFullScreen();

        boolean isFullScreen();
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
