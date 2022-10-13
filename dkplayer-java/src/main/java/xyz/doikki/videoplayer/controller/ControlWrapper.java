package xyz.doikki.videoplayer.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.render.Render;

/**
 * 此类的目的是为了在ControlComponent中既能调用VideoView的api又能调用BaseVideoController的api，
 * 并对部分api做了封装，方便使用
 */
public class ControlWrapper implements VideoViewControl, VideoViewController {

    private final VideoViewControl mPlayerControl;
    private final VideoViewController mController;

    public ControlWrapper(@NonNull VideoViewControl playerControl, @NonNull VideoViewController controller) {
        mPlayerControl = playerControl;
        mController = controller;
    }

    public VideoViewControl getPlayer() {
        return mPlayerControl;
    }

    public VideoViewController getController() {
        return mController;
    }

    //以下方法访问controller
    @Override
    public boolean isShowing() {
        return mController.isShowing();
    }

    @Override
    public void startFadeOut() {
        mController.startFadeOut();
    }

    @Override
    public void stopFadeOut() {
        mController.stopFadeOut();
    }

    @Keep
    @Override
    public boolean isLocked() {
        return mController.isLocked();
    }

    @Keep
    @Override
    public boolean toggleFullScreen() {
        return mController.toggleFullScreen();
    }

    @Keep
    @Override
    public boolean isFullScreen() {
        return mController.isFullScreen();
    }

    @Override
    public boolean startFullScreen() {
        return mController.startFullScreen();
    }

    @Override
    public boolean startFullScreen(boolean isLandscapeReversed) {
        return mController.startFullScreen(isLandscapeReversed);
    }

    @Keep
    @Override
    public boolean stopFullScreen() {
        return mController.stopFullScreen();
    }

    @Keep
    @Override
    public boolean hasCutout() {
        return mController.hasCutout();
    }

    @Keep
    @Override
    public int getCutoutHeight() {
        return mController.getCutoutHeight();
    }

    /**
     * 开始刷新进度
     */
    @Keep
    @Override
    public void startUpdateProgress() {
        mController.startUpdateProgress();
    }

    /**
     * 停止刷新进度
     */
    @Keep
    @Override
    public void stopUpdateProgress() {
        mController.stopUpdateProgress();
    }

    //以下方法直接访问player
    @Keep
    @Override
    public void replay(boolean resetPosition) {
        mPlayerControl.replay(resetPosition);
    }

    @Keep
    @Override
    public void start() {
        mPlayerControl.start();
    }

    @Override
    public void pause() {
        mPlayerControl.pause();
    }

    @Keep
    @Override
    public boolean isPlaying() {
        return mPlayerControl.isPlaying();
    }

    @Override
    public long getDuration() {
        return mPlayerControl.getDuration();
    }

    @Override
    public void seekTo(long pos) {
        mPlayerControl.seekTo(pos);
    }

    @Keep
    @Override
    public int getBufferedPercentage() {
        return mPlayerControl.getBufferedPercentage();
    }

    /**
     * 切换播放或暂停
     */
    @Keep
    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    @Override
    public void setAdaptCutout(boolean adaptCutout) {

    }

    @Keep
    @Override
    public void setMute(boolean isMute) {
        mPlayerControl.setMute(isMute);
    }

    @Keep
    @Override
    public boolean isMute() {
        return mPlayerControl.isMute();
    }



    @Keep
    @Override
    public int[] getVideoSize() {
        return mPlayerControl.getVideoSize();
    }




    @Keep
    @Override
    public long getTcpSpeed() {
        return mPlayerControl.getTcpSpeed();
    }


    @Keep
    @Override
    public boolean stopVideoViewFullScreen() {
        return mPlayerControl.stopVideoViewFullScreen();
    }



    @Keep
    @Override
    public void hide() {
        mController.hide();
    }


    @Override
    public long getCurrentPosition() {
        return mPlayerControl.getCurrentPosition();
    }


    @Override
    public boolean startVideoViewFullScreen() {
        return mPlayerControl.startVideoViewFullScreen();
    }

    @Override
    public void setScreenAspectRatioType(int screenScaleType) {
        mPlayerControl.setScreenAspectRatioType(screenScaleType);
    }

    @Override
    public void setSpeed(float speed) {
        mPlayerControl.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return mPlayerControl.getSpeed();
    }


    @Override
    public void setMirrorRotation(boolean enable) {
        mPlayerControl.setMirrorRotation(enable);
    }

    @Override
    public void screenshot(boolean highQuality, @NonNull Render.ScreenShotCallback callback) {
        mPlayerControl.screenshot(highQuality, callback);
    }


    @Override
    public void setRotation(int rotation) {
        mPlayerControl.setRotation(rotation);
    }

    @Override
    public void startTinyScreen() {
        mPlayerControl.startTinyScreen();
    }

    @Override
    public void stopTinyScreen() {
        mPlayerControl.stopTinyScreen();
    }

    @Override
    public boolean isTinyScreen() {
        return mPlayerControl.isTinyScreen();
    }

    /**
     * 横竖屏切换，根据适配宽高决定是否旋转屏幕
     */
    public void toggleFullScreenByVideoSize(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        int[] size = getVideoSize();
        int width = size[0];
        int height = size[1];
        if (isFullScreen()) {
            stopVideoViewFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            startVideoViewFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void setFadeOutTime(int timeout) {
        mController.setFadeOutTime(timeout);
    }


    @Override
    public void setEnableOrientationSensor(boolean enableOrientation) {
        mController.setEnableOrientationSensor(enableOrientation);
    }

    @Override
    public void setLocked(boolean locked) {
        mController.setLocked(locked);
    }


    @Override
    public void show() {
        mController.show();
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        setLocked(!isLocked());
    }

    /**
     * 切换显示/隐藏状态
     */
    public void toggleShowState() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }


}
