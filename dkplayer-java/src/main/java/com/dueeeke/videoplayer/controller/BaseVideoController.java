package com.dueeeke.videoplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */
@SuppressLint("SourceLockedOrientationActivity")
public abstract class BaseVideoController extends FrameLayout
        implements OrientationHelper.OnOrientationChangeListener, CutoutAdaptHelper.Callback {

    protected View mControllerView;//控制器视图
    protected MediaPlayerControl mMediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean mIsLocked;
    protected int mDefaultTimeout = 4000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected int mCurrentPlayState;
    protected int mCurrentPlayerState;

    protected OrientationHelper mOrientationHelper;
    private boolean mEnableOrientation;

    @Nullable
    private CutoutAdaptHelper mCutoutAdaptHelper;

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
            mControllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        }
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        setClickable(true);
        setFocusable(true);
        mOrientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = VideoViewManager.getConfig().mEnableOrientation;
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null) {
            mCutoutAdaptHelper = new CutoutAdaptHelper(activity, this);
        }
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
        this.mMediaPlayer = mediaPlayer;
        //开始监听
        mOrientationHelper.setOnOrientationChangeListener(this);

        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().attach(mediaPlayer);
        }
    }

    /**
     * 显示
     */
    protected void show() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().show();
        }
    }

    /**
     * 隐藏
     */
    protected void hide() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().hide();
        }
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放状态下的ui
     */
    @CallSuper
    public void setPlayState(int playState) {
        mCurrentPlayState = playState;
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().onPlayStateChange(playState);
        }
        if (playState == VideoView.STATE_IDLE) {
            mOrientationHelper.disable();
            removeAllPrivateComponents();
        }
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放器状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放器状态下的ui
     */
    @CallSuper
    public void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().onPlayerStateChange(playerState);
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
    public void addControlComponent(IControlComponent component) {
        addControlComponent(component, false);
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
            addView(component.getView());
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
    protected void doPauseResume() {
        if (mCurrentPlayState == VideoView.STATE_BUFFERING) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        if (mMediaPlayer.isFullScreen()) {
            stopFullScreen();
        } else {
            startFullScreen();
        }
    }

    /**
     * 子类中请使用此方法来进入全屏
     */
    protected void startFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mMediaPlayer.startFullScreen();
    }

    /**
     * 子类中请使用此方法来退出全屏
     */
    protected void stopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        mMediaPlayer.stopFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 刷新进度Runnable
     */
    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mMediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    /**
     * 隐藏播放视图Runnable
     */
    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * 重写此方法实现刷新进度功能
     */
    private int setProgress() {
        int position = (int) mMediaPlayer.getCurrentPosition();
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().setProgress(position);
        }
        setProgress(position);
        return position;
    }

    protected void setProgress(int position) {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
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
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 是否自动旋转， 默认不自动旋转
     */
    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    private int mOrientation = -1;

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

    @Override
    public void adjustReserveLandscape(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustReserveLandscape(space);
        }
    }

    @Override
    public void adjustLandscape(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustLandscape(space);
        }
    }

    @Override
    public void adjustPortrait(int space) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().adjustPortrait(space);
        }
    }
}
