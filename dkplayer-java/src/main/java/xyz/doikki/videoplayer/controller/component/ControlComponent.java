package xyz.doikki.videoplayer.controller.component;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.controller.ControlWrapper;

/**
 * 控制器中的控制组件
 */
public interface ControlComponent {

    /**
     * 将 ControlWrapper 传递到当前 ControlComponent 中
     */
    void attach(@NonNull ControlWrapper controlWrapper);

    /**
     * 如果 ControlComponent 是 View，返回当前控件（this）即可
     * 如果不是，返回null
     */
    @Nullable
    View getView();

    /**
     * 回调控制器显示和隐藏状态，
     * 此方法可用于控制 ControlComponent 中的控件的跟随手指点击显示和隐藏
     *
     * @param isVisible true 代表要显示， false 代表要隐藏
     * @param anim      显示和隐藏的动画，是一个补间Alpha动画
     */
    void onVisibilityChanged(boolean isVisible, Animation anim);

    /**
     * 回调播放器的播放器状态变更；如果只是单纯的想监听状态变更，可以通过{@link VideoView#addOnStateChangeListener(VideoView.OnStateChangeListener)}方法增加监听
     *
     * @param playState 播放状态
     */
    void onPlayStateChanged(int playState);

    /**
     * 回调播放器的状态，如果你只是单纯的想监听此状态，建议使用
     * {@link VideoView} 中的
     * addOnStateChangeListener 方法
     *
     * @param playerState 播放器状态
     */
    void onPlayerStateChanged(int playerState);

    /**
     * 回调播放进度，1秒回调一次
     *
     * @param duration 视频总时长
     * @param position 播放进度
     */
    void onProgressChanged(int duration, int position);

    /**
     * 回调控制器是否被锁定，锁定后会产生如下影响：
     * 无法响应滑动手势，双击事件，点击显示和隐藏控制UI，跟随重力感应切换横竖屏
     *
     * @param isLocked 是否锁定
     */
    void onLockStateChanged(boolean isLocked);

}
