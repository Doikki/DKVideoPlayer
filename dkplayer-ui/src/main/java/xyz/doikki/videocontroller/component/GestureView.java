package xyz.doikki.videocontroller.component;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.component.KeyControlComponent;

/**
 * 手势控制
 */
public class GestureView extends FrameLayout implements KeyControlComponent {

    public GestureView(@NonNull Context context) {
        super(context);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ControlWrapper mControlWrapper;

    private final ImageView mIcon;
    private final ProgressBar mProgressPercent;
    private final TextView mTextPercent;

    private final LinearLayout mCenterContainer;


    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_gesture_control_view, this, true);
        mIcon = findViewById(R.id.iv_icon);
        mProgressPercent = findViewById(R.id.pro_percent);
        mTextPercent = findViewById(R.id.tv_percent);
        mCenterContainer = findViewById(R.id.center_container);
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
    public void onScreenModeChanged(int screenMode) {

    }

    @Override
    public void onStartLeftOrRightKeyPressed(@NonNull KeyEvent event) {
        onStartSlide();
    }

    @Override
    public void onStopLeftOrRightKeyPressed(@NonNull KeyEvent event) {
        onStopSlide();
    }

    @Override
    public void onCancelLeftOrRightKeyPressed(@NonNull KeyEvent keyEvent) {
        onStopSlide();
    }

    @Override
    public void onStartSlide() {
        mControlWrapper.hide();
        mCenterContainer.setVisibility(VISIBLE);
        mCenterContainer.setAlpha(1f);
    }

    @Override
    public void onStopSlide() {
        mCenterContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mCenterContainer.setVisibility(GONE);
                    }
                })
                .start();
    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        mProgressPercent.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_forward);
        } else if (slidePosition < currentPosition) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_fast_rewind);
        } else {
            //相等的情况不处理，避免最大最小位置图标错乱
        }
        mTextPercent.setText(String.format("%s/%s", stringForTime(slidePosition), stringForTime(duration)));
    }

    @Override
    public void onBrightnessChange(int percent) {
        mProgressPercent.setVisibility(VISIBLE);
        mIcon.setImageResource(R.drawable.dkplayer_ic_action_brightness);
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onVolumeChange(int percent) {

        mProgressPercent.setVisibility(VISIBLE);
        if (percent <= 0) {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_off);
        } else {
            mIcon.setImageResource(R.drawable.dkplayer_ic_action_volume_up);
        }
        mTextPercent.setText(percent + "%");
        mProgressPercent.setProgress(percent);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == DKVideoView.STATE_IDLE
                || playState == DKVideoView.STATE_START_ABORT
                || playState == DKVideoView.STATE_PREPARING
                || playState == DKVideoView.STATE_PREPARED
                || playState == DKVideoView.STATE_ERROR
                || playState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void onProgressChanged(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

}
