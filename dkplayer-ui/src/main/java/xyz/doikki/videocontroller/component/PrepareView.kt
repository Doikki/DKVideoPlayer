package xyz.doikki.videocontroller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.DKManager;
import xyz.doikki.videoplayer.controller.component.ControlComponent;

/**
 * 准备播放界面
 */
public class PrepareView extends BaseControlComponent implements ControlComponent {

    private ImageView mThumb;
    private ImageView mStartPlay;
    private ProgressBar mLoading;
    private FrameLayout mNetWarning;

    public PrepareView(@NonNull Context context) {
        super(context);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_prepare_view, this, true);
        mThumb = findViewById(R.id.thumb);
        mStartPlay = findViewById(R.id.start_play);
        mLoading = findViewById(R.id.loading);
        mNetWarning = findViewById(R.id.net_warning_layout);

        View btnInWarning = findViewById(R.id.status_btn);

        if (isFocusUiMode()) {
            setViewInFocusMode(mStartPlay);
            setViewInFocusMode(btnInWarning);
        }

        btnInWarning.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetWarning.setVisibility(GONE);
                DKManager.setPlayOnMobileNetwork(true);
                mControlWrapper.start();
            }
        });
    }

    /**
     * 设置点击此界面开始播放
     */
    public void setClickStart() {
        if(isFocusUiMode()){
            setViewInFocusMode(this);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlWrapper.start();
            }
        });
    }


    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case DKVideoView.STATE_PREPARING:
                bringToFront();
                setVisibility(VISIBLE);
                mStartPlay.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            case DKVideoView.STATE_PLAYING:
            case DKVideoView.STATE_PAUSED:
            case DKVideoView.STATE_ERROR:
            case DKVideoView.STATE_BUFFERING:
            case DKVideoView.STATE_BUFFERED:
            case DKVideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
            case DKVideoView.STATE_IDLE:
                setVisibility(VISIBLE);
                bringToFront();
                mLoading.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mStartPlay.setVisibility(View.VISIBLE);
                mThumb.setVisibility(View.VISIBLE);
                break;
            case DKVideoView.STATE_START_ABORT:
                setVisibility(VISIBLE);
                mNetWarning.setVisibility(VISIBLE);
                mNetWarning.bringToFront();
                break;
        }
    }
}
