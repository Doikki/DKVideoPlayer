package xyz.doikki.videoplayer.controller;

import androidx.annotation.IntRange;

import xyz.doikki.videoplayer.DKManager;

/**
 * 视图控制器
 */
public interface VideoViewController {

//    /**
//     * 是否采用焦点模式
//     */
//    default boolean isFocusUiMode() {
//        return DKManager.isFocusInTouchMode();
//    }

    /**
     * 控制视图是否处于显示状态
     */
    boolean isShowing();

    /**
     * 显示控制视图
     */
    void show();

    /**
     * 隐藏控制视图
     */
    void hide();

    /**
     * 启动自动隐藏控制器视图
     */
    void startFadeOut();

    /**
     * 移除自动隐藏控制器视图
     */
    void stopFadeOut();

    /**
     * 设置自动隐藏倒计时持续的时间
     *
     * @param timeout 默认4000，比如大于0才会生效
     */
    void setFadeOutTime(@IntRange(from = 1) int timeout);

    /**
     * 启用设备角度传感器(用于自动横竖屏切换)，默认不启用
     *
     * @param enable true:开启，默认关闭
     */
    void setEnableOrientationSensor(boolean enable);

    /**
     * 设置锁定状态
     *
     * @param locked 是否锁定
     */
    void setLocked(boolean locked);

    /**
     * 是否处于锁定状态
     */
    boolean isLocked();

    /**
     * 是否是全屏状态
     *
     * @return
     */
    boolean isFullScreen();

    /**
     * 横竖屏切换:用来代理{@link VideoViewControl#toggleFullScreen()},即通过Controller调用VideoView的方法
     */
    boolean toggleFullScreen();

    /**
     * 开始全屏
     *
     * @return
     */
    default boolean startFullScreen() {
        return startFullScreen(false);
    }

    /**
     * 开始全屏:用来代理{@link VideoViewControl#startFullScreen(boolean)}} ,即通过Controller调用VideoView的方法
     *
     * @return
     */
    boolean startFullScreen(boolean isLandscapeReversed);


    /**
     * 结束全屏:用来代理{@link VideoViewControl#stopFullScreen()},即通过Controller调用VideoView的方法
     *
     * @return
     */
    boolean stopFullScreen();


    /**
     * 开始刷新进度
     */
    void startUpdateProgress();

    /**
     * 停止刷新进度
     */
    void stopUpdateProgress();

    /**
     * 设置是否适配刘海
     *
     * @param adaptCutout
     */
    void setAdaptCutout(boolean adaptCutout);

    /**
     * 是否需要适配刘海
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     */
    int getCutoutHeight();


}
