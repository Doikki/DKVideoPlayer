package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.util.PlayerUtils;

public abstract class BaseControlComponent extends FrameLayout implements ControlComponent {

    protected ControlWrapper mControlWrapper;

    public BaseControlComponent(Context context) {
        this(context, null);
    }

    public BaseControlComponent(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseControlComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    protected abstract void setupViews();

    @Nullable
    protected Activity getActivity() {
        return PlayerUtils.scanForActivity(getContext());
    }

    protected void setViewInFocusMode(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }


}
