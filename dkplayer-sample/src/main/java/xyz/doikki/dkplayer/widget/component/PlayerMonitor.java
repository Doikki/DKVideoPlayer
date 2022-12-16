package xyz.doikki.dkplayer.widget.component;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import xyz.doikki.videoplayer.controller.VideoController;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.UtilsKt;

public class PlayerMonitor implements ControlComponent {

    private VideoController mControlWrapper;

    @Override
    public void attachController(@NonNull VideoController controller) {
        mControlWrapper = controller;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        L.d("onVisibilityChanged: " + isVisible);
    }

    @Override
    public void onPlayStateChanged(int playState, @NotNull HashMap<String, Object> extras) {
        L.d("onPlayStateChanged: " + UtilsKt.playState2str(playState));
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        L.d("onPlayerStateChanged: " + UtilsKt.screenMode2str(screenMode));
    }

    @Override
    public void onProgressChanged(long duration, long position) {
        L.d("setProgress: duration: " + duration + " position: " + position + " buffered percent: " + mControlWrapper.getPlayerControl().getBufferedPercentage());
        L.d("network speed: " + mControlWrapper.getPlayerControl().getTcpSpeed());
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        L.d("onLockStateChanged: " + isLocked);
    }


}
