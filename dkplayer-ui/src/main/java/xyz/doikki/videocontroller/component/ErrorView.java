package xyz.doikki.videocontroller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import xyz.doikki.videocontroller.R;
import xyz.doikki.videoplayer.DKVideoView;

/**
 * 播放出错提示界面
 * Created by Doikki on 2017/4/13.
 * update by luochao on022/9/28 调整基类接口变更引起的变动，去掉无用代码
 */
public class ErrorView extends BaseControlComponent {

    private float mDownX;
    private float mDownY;

    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupViews() {
        setVisibility(GONE);
        if (isInEditMode()) {
            setVisibility(VISIBLE);
        }
        setBackgroundResource(R.color.dkplayer_control_component_container_color);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_error_view, this, true);

        View statusBtn = findViewById(R.id.status_btn);
        if (isFocusUiMode()) {
            statusBtn.setFocusable(true);
            statusBtn.setFocusableInTouchMode(true);
        } else {
            //设置当前容器能点击的原因是为了避免事件穿透
            setClickable(true);
        }
        statusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                mControlWrapper.replay(false);
            }
        });
    }


    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == DKVideoView.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
        } else if (playState == DKVideoView.STATE_IDLE) {
            setVisibility(GONE);
        }
    }

    /**
     * 以下逻辑用于小窗展示的情况下，避免在触摸的小范围内滑动窗口
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
