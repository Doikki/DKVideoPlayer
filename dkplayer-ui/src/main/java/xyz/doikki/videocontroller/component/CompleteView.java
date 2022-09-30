package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.render.ScreenMode;

/**
 * 自动播放完成界面
 * <p>
 * update by luochao at 2022/9/28
 */
public class CompleteView extends BaseControlComponent implements ControlComponent {

    private ImageView mStopFullscreen;

    public CompleteView(@NonNull Context context) {
        super(context);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupViews() {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_complete_view, this, true);
        //在xml中去除了一个布局层级，因此xml中的背景色改为代码设置在当前布局中
        setBackgroundColor(Color.parseColor("#33000000"));
        View replyAct = findViewById(R.id.replay_layout);
        if (isFocusUiMode()) {
            replyAct.setClickable(true);
            replyAct.setFocusable(true);
            replyAct.setFocusableInTouchMode(true);
        } else {
            //防止touch模式下，事件穿透
            setClickable(true);
        }

        replyAct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新播放
                mControlWrapper.replay(true);
            }
        });

        mStopFullscreen = findViewById(R.id.stop_fullscreen);
        mStopFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mControlWrapper.isFullScreen()) {
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        mControlWrapper.stopFullScreen();
                    }
                }
            }
        });
    }

    /**
     * 设置播放结束按钮的文本（默认是“重新播放”）
     *
     * @param message
     */
    public void setCompleteText(CharSequence message) {
        TextView tv = findViewById(R.id.tv_replay);
        if (tv == null)
            return;
        tv.setText(message);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(VISIBLE);
            mStopFullscreen.setVisibility(mControlWrapper.isFullScreen() ? VISIBLE : GONE);
            bringToFront();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        if (screenMode == ScreenMode.FULL) {
            mStopFullscreen.setVisibility(VISIBLE);
        } else if (screenMode == ScreenMode.NORMAL) {
            mStopFullscreen.setVisibility(GONE);
        }

        Activity activity = getActivity();
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                sflp.setMargins(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                sflp.setMargins(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                sflp.setMargins(0, 0, 0, 0);
            }
        }
    }
}
