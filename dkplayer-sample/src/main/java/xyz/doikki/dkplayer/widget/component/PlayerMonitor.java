package xyz.doikki.dkplayer.widget.component;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.UtilsKt;

public class PlayerMonitor implements ControlComponent {

    private ControlWrapper mControlWrapper;

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
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
    public void onPlayStateChanged(int playState) {
        L.d("onPlayStateChanged: " + UtilsKt.playState2str(playState));
    }

    @Override
    public void onScreenModeChanged(int screenMode) {
        L.d("onPlayerStateChanged: " + UtilsKt.screenMode2str(screenMode));
    }

    @Override
    public void onProgressChanged(int duration, int position) {
        L.d("setProgress: duration: " + duration + " position: " + position + " buffered percent: " + mControlWrapper.getBufferedPercentage());
        L.d("network speed: " + mControlWrapper.getTcpSpeed());
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        L.d("onLockStateChanged: " + isLocked);
    }
}
