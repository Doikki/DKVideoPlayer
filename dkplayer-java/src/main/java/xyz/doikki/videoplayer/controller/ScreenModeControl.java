package xyz.doikki.videoplayer.controller;

import xyz.doikki.videoplayer.DKVideoView;

/**
 * 全屏、小窗口、自动横屏等 屏幕相关操作所需接口
 */
public interface ScreenModeControl {

    /**
     * 是否是全屏状态
     *
     * @return
     */
    boolean isFullScreen();

    /**
     * 当前是否是小窗播放状态
     *
     * @return
     */
    boolean isTinyScreen();

    /**
     * 横竖屏切换
     */
    boolean toggleFullScreen();

    default boolean startFullScreen(){
        return startFullScreen(false);
    }

    /**
     * 开始全屏;
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     * @param isLandscapeReversed 是否是反向横屏
     * @see DKVideoView#addOnStateChangeListener(DKVideoView.OnStateChangeListener)
     */
    boolean startFullScreen(boolean isLandscapeReversed);

    /**
     * 结束全屏
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    boolean stopFullScreen();

    /**
     * 开始VideoView全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与{@link #startFullScreen()}方法的区别在于，此方法不会调用{@link android.app.Activity#setRequestedOrientation(int)}改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    boolean startVideoViewFullScreen();

    /**
     * 结束VideoView的全屏（用于只想横竖屏切换VideoView而不更改Activity方向的情况）
     * 此方法与{@link #startFullScreen()}方法的区别在于，此方法不会调用{@link android.app.Activity#setRequestedOrientation(int)}改变Activity的方向。
     *
     * @return true：video view的方向发生了变化
     */
    boolean stopVideoViewFullScreen();

    /**
     * 开始小窗播放
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void startTinyScreen();

    /**
     * 结束小窗播放
     * 屏幕切换之后会回调{@link DKVideoView.OnStateChangeListener#onScreenModeChanged(int)}
     */
    void stopTinyScreen();

}
