package xyz.doikki.dkplayer.widget.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.controller.VideoController;
import xyz.doikki.videoplayer.controller.VideoViewControl;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.util.UtilsKt;

/**
 * 调试信息
 */
public class DebugInfoView extends AppCompatTextView implements ControlComponent {

    private VideoController mController;

    public DebugInfoView(Context context) {
        super(context);
    }

    public DebugInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackgroundResource(android.R.color.black);
        setTextSize(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(lp);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState, @NotNull HashMap<String, Object> extras) {
        setText(getDebugString(playState, extras));
        bringToFront();
    }

    /**
     * Returns the debugging information string to be shown by the target {@link TextView}.
     */
    protected String getDebugString(int playState, @NotNull HashMap<String, Object> extras) {
        VideoViewControl control = mController.getPlayerControl();
        if (control == null) return "";
        VideoView videoView = (VideoView) mController.getPlayerControl();
        StringBuilder sb = new StringBuilder();
        sb.append("player:")
                .append(videoView.getPlayerFactory().getClass().getSimpleName())
                .append("   ").append("render:")
                .append(videoView.getRenderFactory().getClass().getSimpleName())
                .append("\n")
                .append(UtilsKt.playState2str(playState)).append("  ");

        Object obj = extras.get(VideoView.EXT_VIDEO_SIZE);
        if (obj instanceof int[]) {
            int[] videoSize = (int[]) obj;
            sb.append("video width:").append(videoSize[0]).append(",height:").append(videoSize[1]);
        }

        return sb.toString();
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        bringToFront();
    }

    @Override
    public void onProgressChanged(long duration, long position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    @Override
    public void attachController(@NonNull VideoController controller) {
        this.mController = controller;
    }
}
