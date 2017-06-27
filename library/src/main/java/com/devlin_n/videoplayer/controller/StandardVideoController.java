package com.devlin_n.videoplayer.controller;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.videoplayer.R;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.videoplayer.util.Constants;
import com.devlin_n.videoplayer.util.L;
import com.devlin_n.videoplayer.util.WindowUtil;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class StandardVideoController extends BaseVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView totalTime, currTime;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView moreMenu;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    private boolean isLive;
    private boolean isDragging;
    private View statusHolder;

    private ProgressBar bottomProgress;
    private ImageView playButton;
    private ImageView startPlayButton;
    private ProgressBar loadingProgress;
    private ImageView thumb;
    private LinearLayout completeContainer;
    private Animation showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in);
    private Animation hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out);
    private PopupMenu popupMenu;


    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        moreMenu = (ImageView) controllerView.findViewById(R.id.more_menu);
        moreMenu.setOnClickListener(this);
        fullScreenButton = (ImageView) controllerView.findViewById(R.id.fullscreen);
        fullScreenButton.setOnClickListener(this);
        bottomContainer = (LinearLayout) controllerView.findViewById(R.id.bottom_container);
        topContainer = (LinearLayout) controllerView.findViewById(R.id.top_container);
        videoProgress = (SeekBar) controllerView.findViewById(R.id.seekBar);
        videoProgress.setOnSeekBarChangeListener(this);
        totalTime = (TextView) controllerView.findViewById(R.id.total_time);
        currTime = (TextView) controllerView.findViewById(R.id.curr_time);
        backButton = (ImageView) controllerView.findViewById(R.id.back);
        backButton.setOnClickListener(this);
        lock = (ImageView) controllerView.findViewById(R.id.lock);
        lock.setOnClickListener(this);
        thumb = (ImageView) controllerView.findViewById(R.id.thumb);
        thumb.setOnClickListener(this);
        playButton = (ImageView) controllerView.findViewById(R.id.iv_play);
        playButton.setOnClickListener(this);
        startPlayButton = (ImageView) controllerView.findViewById(R.id.start_play);
        loadingProgress = (ProgressBar) controllerView.findViewById(R.id.loading);
        bottomProgress = (ProgressBar) controllerView.findViewById(R.id.bottom_progress);
        ImageView rePlayButton = (ImageView) controllerView.findViewById(R.id.iv_replay);
        rePlayButton.setOnClickListener(this);
        completeContainer = (LinearLayout) controllerView.findViewById(R.id.complete_container);
        completeContainer.setOnClickListener(this);
        title = (TextView) controllerView.findViewById(R.id.title);
        statusHolder = controllerView.findViewById(R.id.status_holder);
        statusHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) WindowUtil.getStatusBarHeight(getContext())));
        popupMenu = new PopupMenu(getContext(), moreMenu, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.controller_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.float_window) {
                    mediaPlayer.startFloatWindow();
                } else if (itemId == R.id.scale_default) {
                    mediaPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
                } else if (itemId == R.id.scale_original) {
                    mediaPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_ORIGINAL);
                } else if (itemId == R.id.scale_match) {
                    mediaPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_MATCH_PARENT);
                } else if (itemId == R.id.scale_16_9) {
                    mediaPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_16_9);
                } else if (itemId == R.id.scale_4_3) {
                    mediaPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_4_3);
                }
                popupMenu.dismiss();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen || i == R.id.back) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play || i == R.id.thumb || i == R.id.iv_replay) {
            doPauseResume();
        } else if (i == R.id.more_menu) {
            popupMenu.show();
            show();
        }
    }

    public void showTitle() {
        title.setVisibility(VISIBLE);
    }

    @Override
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case IjkVideoView.PLAYER_NORMAL:
                L.e("PLAYER_NORMAL");
                if (isLocked) return;
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                gestureEnabled = false;
                fullScreenButton.setSelected(false);
                backButton.setVisibility(GONE);
                lock.setVisibility(GONE);
                statusHolder.setVisibility(GONE);
                title.setVisibility(INVISIBLE);
                break;
            case IjkVideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");
                if (isLocked) return;
                postDelayed(new Runnable() {//解决ListView无效问题
                    @Override
                    public void run() {
                        setLayoutParams(new FrameLayout.LayoutParams(
                                Constants.SCREEN_HEIGHT,
                                Constants.SCREEN_WIDTH));
                    }
                }, 300);
                gestureEnabled = true;
                fullScreenButton.setSelected(true);
                statusHolder.setVisibility(VISIBLE);
                backButton.setVisibility(VISIBLE);
                title.setVisibility(VISIBLE);
                if (mShowing) {
                    lock.setVisibility(VISIBLE);
                    WindowUtil.showNavKey(getContext());
                    WindowUtil.showStatusBar(getContext());
                } else {
                    lock.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                hide();
                isLocked = false;
                lock.setSelected(false);
                mediaPlayer.setLock(false);
                completeContainer.setVisibility(GONE);
                bottomProgress.setVisibility(GONE);
                loadingProgress.setVisibility(GONE);
                startPlayButton.setVisibility(VISIBLE);
                thumb.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                post(mShowProgress);
                playButton.setSelected(true);
                completeContainer.setVisibility(GONE);
                thumb.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                playButton.setSelected(false);
                break;
            case IjkVideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                completeContainer.setVisibility(GONE);
                startPlayButton.setVisibility(GONE);
                loadingProgress.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                if (!isLive) bottomProgress.setVisibility(VISIBLE);
                loadingProgress.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                break;
            case IjkVideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                startPlayButton.setVisibility(GONE);
                loadingProgress.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_BUFFERED:
                loadingProgress.setVisibility(GONE);
                L.e("STATE_BUFFERED");
                break;
            case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                hide();
                thumb.setVisibility(VISIBLE);
                completeContainer.setVisibility(VISIBLE);
                bottomProgress.setProgress(0);
                bottomProgress.setSecondaryProgress(0);
                isLocked = false;
                mediaPlayer.setLock(false);
                break;
        }
    }

    private void doLockUnlock() {
        if (isLocked) {
            isLocked = false;
            mShowing = false;
            gestureEnabled = true;
            show();
            lock.setSelected(false);
            Toast.makeText(getContext(), R.string.unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            isLocked = true;
            gestureEnabled = false;
            lock.setSelected(true);
            Toast.makeText(getContext(), R.string.locked, Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.setLock(isLocked);
    }

    /**
     * 设置是否为直播视频
     */
    public void setLive(boolean live) {
        isLive = live;
        bottomProgress.setVisibility(GONE);
        videoProgress.setVisibility(INVISIBLE);
        totalTime.setVisibility(INVISIBLE);
        moreMenu.setVisibility(VISIBLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / videoProgress.getMax();
        mediaPlayer.seekTo((int) newPosition);
        isDragging = false;
        post(mShowProgress);
        show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * progress) / videoProgress.getMax();
        if (currTime != null)
            currTime.setText(stringForTime((int) newPosition));
    }

    @Override
    public void hide() {
        if (mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(GONE);
                if (!isLocked) {
                    hideAllViews();
                    WindowUtil.hideStatusBar(getContext());
                    WindowUtil.hideNavKey(getContext());
                }
            } else {
                hideAllViews();
            }
            if (!isLive && !isLocked) {
                bottomProgress.setVisibility(VISIBLE);
                bottomProgress.startAnimation(showAnim);
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        topContainer.setVisibility(GONE);
        topContainer.startAnimation(hideAnim);
        bottomContainer.setVisibility(GONE);
        bottomContainer.startAnimation(hideAnim);
    }

    private void show(int timeout) {
        if (!mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(VISIBLE);
                if (!isLocked) {
                    showAllViews();
                }
            } else {
                showAllViews();
            }
            if (!isLocked && !isLive) {
                bottomProgress.setVisibility(GONE);
                bottomProgress.startAnimation(hideAnim);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        bottomContainer.setVisibility(VISIBLE);
        bottomContainer.startAnimation(showAnim);
        topContainer.setVisibility(VISIBLE);
        topContainer.startAnimation(showAnim);
        WindowUtil.showStatusBar(getContext());
        WindowUtil.showNavKey(getContext());
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    protected int setProgress() {
        if (mediaPlayer == null || isDragging) {
            return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (videoProgress != null) {
            if (duration > 0) {
                videoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * videoProgress.getMax());
                videoProgress.setProgress(pos);
                bottomProgress.setProgress(pos);
            } else {
                videoProgress.setEnabled(false);
            }
            int percent = mediaPlayer.getBufferPercentage();
            if (percent >= 95) { //修复第二进度不能100%问题
                videoProgress.setSecondaryProgress(videoProgress.getMax());
                bottomProgress.setSecondaryProgress(bottomProgress.getMax());
            } else {
                videoProgress.setSecondaryProgress(percent * 10);
                bottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (totalTime != null)
            totalTime.setText(stringForTime(duration));
        if (currTime != null)
            currTime.setText(stringForTime(position));
        if (title != null)
            title.setText(mediaPlayer.getTitle());
        return position;
    }


    @Override
    protected void slideToChangePosition(float deltaX) {
        if (isLive) {
            mNeedSeek = false;
        } else {
            super.slideToChangePosition(deltaX);
        }
    }

    public ImageView getThumb() {
        return thumb;
    }
}
