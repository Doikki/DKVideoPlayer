package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 控制器基类
 * Created by dueeeke on 2017/4/12.
 */
public abstract class BaseVideoController extends FrameLayout
        implements OrientationHelper.OnOrientationChangeListener,
        CutoutAdaptHelper.Callback, VideoControllerCallback {

    /**
     * 播放器包装类，里面扩展了MediaPlayerControl的功能
     */
    protected MediaPlayerControlWrapper mMediaPlayer;

    private boolean mShowing;//控制器是否处于显示状态

    /**
     * 是否处于锁定状态
     */
    private boolean mIsLocked;

    /**
     * 播放视图隐藏超时
     */
    protected int mDefaultTimeout = 4000;

    /**
     * 是否开启根据屏幕方向进入/退出全屏
     */
    private boolean mEnableOrientation;

    /**
     * 屏幕方向监听辅助类
     */
    protected OrientationHelper mOrientationHelper;

    /**
     * 是否适配刘海屏
     */
    private boolean mAdaptCutout;

    /**
     * 刘海屏适配辅助类
     */
    @Nullable
    private CutoutAdaptHelper mCutoutAdaptHelper;

    /**
     * 保存了所有的控制组件
     */
    private LinkedHashMap<IControlComponent, Boolean> mControlComponents = new LinkedHashMap<>();

    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        }
        setClickable(true);
        setFocusable(true);
        mOrientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = VideoViewManager.getConfig().mEnableOrientation;
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 重要：此方法用于将{@link VideoView} 和控制器绑定
     */
    @CallSuper
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mMediaPlayer = new MediaPlayerControlWrapper(mediaPlayer, this);
        //开始监听
        mOrientationHelper.setOnOrientationChangeListener(this);

        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().attach(mMediaPlayer);
        }

        if (mAdaptCutout) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null) {
                mCutoutAdaptHelper = new CutoutAdaptHelper(activity, this);
            }
        }
    }

    /**
     * 设置是否适配刘海屏
     */
    public void setAdaptCutout(boolean adaptCutout) {
        mAdaptCutout = adaptCutout;
    }

    /**
     * 设置播放视图自动隐藏超时
     */
    public void setDismissTimeout(int timeout) {
        if (timeout > 0) {
            mDefaultTimeout = timeout;
        }
    }

    /**
     * 切换显示状态
     */
    public void toggleShowState() {
        if (mShowing) {
            hideInner();
        } else {
            showInner(false);
        }
    }

    /**
     * 隐藏播放视图
     */
    protected void hideInner() {
        if (mShowing) {
            if (!mIsLocked) {//如果没有锁定屏幕，就向各个组件分发hide事件
                for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
                    next.getKey().hide();
                }
            }
            //向子类分发hide事件
            hide();
            mShowing = false;
        }
    }


    /**
     * 显示播放视图
     * @param force 是否强制显示
     */
    protected void showInner(boolean force) {
        if (!mShowing || force) {
            if (!mIsLocked) {//如果没有锁定屏幕，就向各个组件分发show事件
                for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
                    next.getKey().show();
                }
            }
            //向子类分发show事件
            show();
            mShowing = true;
        }

        startFadeOut();
    }

    protected boolean isShowing() {
        return mShowing;
    }

    /**
     * 显示
     */
    protected void show() {

    }

    /**
     * 隐藏
     */
    protected void hide() {

    }

    /**
     * 开始计时
     */
    @Override
    public void startFadeOut() {
        //重新开始计时
        stopFadeOut();
        postDelayed(mFadeOut, mDefaultTimeout);
    }

    /**
     * 取消计时
     */
    @Override
    public void stopFadeOut() {
        removeCallbacks(mFadeOut);
    }

    /**
     * 隐藏播放视图Runnable
     */
    public final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hideInner();
        }
    };

    public void setLocked(boolean locked) {
        mIsLocked = locked;
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放状态下的ui
     */
    @CallSuper
    public void setPlayState(int playState) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().onPlayStateChanged(playState);
        }
        if (playState == VideoView.STATE_IDLE) {
            reset();
        } else if (playState == VideoView.STATE_PREPARING) {
            hideInner();
        } else if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
            mIsLocked = false;
            removeCallbacks(mFadeOut);
            hideInner();
        } else if (playState == VideoView.STATE_ERROR) {
            removeCallbacks(mFadeOut);
            hideInner();
        }
    }

    private void reset() {
        mOrientationHelper.disable();
        mOrientation = 0;
        mIsLocked = false;
        removeCallbacks(mFadeOut);
        hideInner();
        removeAllPrivateComponents();
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放器状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放器状态下的ui
     */
    @CallSuper
    public void setPlayerState(int playerState) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().onPlayerStateChanged(playerState);
        }
        if (mCutoutAdaptHelper != null) {
            mCutoutAdaptHelper.onPlayerStateChanged(playerState);
        }
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                if (mEnableOrientation) {
                    mOrientationHelper.enable();
                } else {
                    mOrientationHelper.disable();
                }
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                //在全屏时强制监听设备方向
                mOrientationHelper.enable();
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                mOrientationHelper.disable();
                break;
        }
    }

    /**
     * 显示移动网络播放提示
     *
     * @return 返回显示移动网络播放提示的条件，false:不显示, true显示
     * 此处默认根据手机网络类型来决定是否显示，开发者可以重写相关逻辑
     */
    public boolean showNetWarning() {
        return PlayerUtils.getNetworkType(getContext()) == PlayerUtils.NETWORK_MOBILE
                && !VideoViewManager.instance().playOnMobileNetwork();
    }

    /**
     * 添加控制组件
     */
    public void addControlComponent(IControlComponent... component) {
        for (IControlComponent item : component) {
            addControlComponent(item, false);
        }
    }

    /**
     * 添加控制组件
     *
     * @param isPrivate 是否为独有的组件，如果是就不添加到控制器中
     */
    public void addControlComponent(IControlComponent component, boolean isPrivate) {
        mControlComponents.put(component, isPrivate);
        if (mMediaPlayer != null) {
            component.attach(mMediaPlayer);
        }
        if (!isPrivate) {
            addView(component.getView(), 0);
        }
    }

    /**
     * 移除控制组件
     */
    public void removeControlComponent(IControlComponent component) {
        removeView(component.getView());
        mControlComponents.remove(component);
    }

    public void removeAllControlComponent() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            removeView(next.getKey().getView());
        }
        mControlComponents.clear();
    }

    public void removeAllPrivateComponents() {
        Iterator<Map.Entry<IControlComponent, Boolean>> it = mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<IControlComponent, Boolean> next = it.next();
            if (next.getValue()) {
                it.remove();
            }
        }
    }

    /**
     * 播放和暂停
     */
    protected void togglePlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        mMediaPlayer.toggleFullScreen(activity);
    }

    /**
     * 子类中请使用此方法来进入全屏
     */
    protected boolean startFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return false;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mMediaPlayer.startFullScreen();
        return true;
    }

    /**
     * 子类中请使用此方法来退出全屏
     */
    protected boolean stopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return false;
        mMediaPlayer.stopFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return true;
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mMediaPlayer.isPlaying() && (mEnableOrientation || mMediaPlayer.isFullScreen())) {
            if (hasWindowFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOrientationHelper.enable();
                    }
                }, 800);
            } else {
                mOrientationHelper.disable();
            }
        }
    }

    /**
     * 是否自动旋转， 默认不自动旋转
     */
    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    private int mOrientation = 0;

    @CallSuper
    @Override
    public void onOrientationChanged(int orientation) {
        if (mCutoutAdaptHelper != null) {
            mCutoutAdaptHelper.onOrientationChanged();
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;

        //记录用户手机上一次放置的位置
        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            //手机平放时，检测不到有效的角度
            //重置为原始位置 -1
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            int o = activity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == 0) {
                return;
            }
            //0度，用户竖直拿着手机
            mOrientation = 0;
            onOrientationPortrait(activity);
        } else if (orientation > 80 && orientation < 100) {

            int o = activity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) {
                return;
            }
            //90度，用户右侧横屏拿着手机
            mOrientation = 90;
            onOrientationReverseLandscape(activity);
        } else if (orientation > 260 && orientation < 280) {
            int o = activity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) {
                return;
            }
            //270度，用户左侧横屏拿着手机
            mOrientation = 270;
            onOrientationLandscape(activity);
        }
    }

    /**
     * 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        //屏幕锁定的情况
        if (mIsLocked) return;
        //没有开启设备方向监听的情况
        if (!mEnableOrientation) return;

        mMediaPlayer.stopFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 横屏
     */
    protected void onOrientationLandscape(Activity activity) {
        mMediaPlayer.startFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        mMediaPlayer.startFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    @CallSuper
    @Override
    public void adjustReserveLandscape(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustReserveLandscape(space);
        }
    }

    @CallSuper
    @Override
    public void adjustLandscape(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustLandscape(space);
        }
    }

    @CallSuper
    @Override
    public void adjustPortrait(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustPortrait(space);
        }
    }
}
