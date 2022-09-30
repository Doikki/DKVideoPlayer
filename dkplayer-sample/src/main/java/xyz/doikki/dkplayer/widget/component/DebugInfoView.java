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

import xyz.doikki.videoplayer.Utils;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.component.ControlComponent;

/**
 * 调试信息
 */
public class DebugInfoView extends AppCompatTextView implements ControlComponent {

    private ControlWrapper mControlWrapper;

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
    public void attach(@NonNull ControlWrapper holder) {
        mControlWrapper = holder;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        setText(getDebugString(playState));
        bringToFront();
    }

    /**
     * Returns the debugging information string to be shown by the target {@link TextView}.
     */
    protected String getDebugString(int playState) {
        int[] videoSize = mControlWrapper.getPlayer().getVideoSize();
        DKVideoView videoView = (DKVideoView) mControlWrapper.getPlayer();
        StringBuilder sb = new StringBuilder();
        sb.append("player:").append(videoView.getPlayerName()).append("   ")
                .append("render:").append(videoView.getRenderName()).append("\n");
        sb.append(Utils.playState2str(playState)).append("  ")
                .append("video width:").append(videoSize[0])
                .append(",height:").append(videoSize[1]);
        return sb.toString();
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        bringToFront();
    }

    @Override
    public void onProgressChanged(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
