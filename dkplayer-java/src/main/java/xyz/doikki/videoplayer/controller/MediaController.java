package xyz.doikki.videoplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.DKManager;
import xyz.doikki.videoplayer.controller.component.ControlComponent;
import xyz.doikki.videoplayer.player.DeviceOrientationSensorHelper;
import xyz.doikki.videoplayer.render.ScreenMode;
import xyz.doikki.videoplayer.util.CutoutUtil;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.PlayerUtils;

/**
 * 控制器基类
 * 对外提供可以控制的方法
 *
 * @see #show() 显示控制器
 * @see #hide() 隐藏控制器
 * @see #startFadeOut() 开启超时自动隐藏控制器
 * @see #stopFadeOut() 移除自动隐藏控制器计时
 * @see #setFadeOutTime(int) 设置自动隐藏倒计时持续的时间
 * @see #setLocked(boolean) 修改锁定状态
 * @see #setEnableOrientationSensor(boolean) 设置是否启用设备旋转监听控制横竖屏切换，默认不开启
 * @see #toggleFullScreen() 横竖屏切换
 * @see #startFullScreen() 开始全屏
 * @see #stopFullScreen() 结束全屏
 * @see #setAdaptCutout(boolean) 是否适配刘海
 * @see #hasCutout() 是否有刘海
 * @see #getCutoutHeight() 刘海高度
 * <p>
 * 对子类提供的可以重写的功能
 * @see #onVisibilityChanged(boolean, Animation) 控制器可见状态发生了变化
 * @see #onLockStateChanged(boolean) 锁定状态改变
 * @see #onScreenModeChanged(int) 屏幕模式改变
 * @see #onProgressChanged(int, int)  播放进度发生了变化
 *
 * <p>
 * 此类集成各种事件的处理逻辑，包括
 * 2.播放状态改变: {@link #setPlayerState(int)} {@link #onScreenModeChanged(int)}
 * 6.设备方向监听: {@link #onDeviceDirectionChanged(int)}
 * Created by Doikki on 2017/4/12.
 */
public abstract class MediaController extends FrameLayout implements VideoViewController, DeviceOrientationSensorHelper.DeviceOrientationChangedListener {

    /**
     * 当前控制器中保存的所有控制组件
     */
    protected LinkedHashMap<ControlComponent, Boolean> mControlComponents = new LinkedHashMap<>();

    /**
     * 绑定的播放器
     */
    private VideoViewControl mPlayer;

    /**
     * 是否处于锁定状态
     */
    private boolean mLocked;

    /**
     * 当前播放器状态
     */
    @DKVideoView.PlayerState
    private int mPlayerState;

    /**
     * 显示动画
     */
    private Animation mShowAnim;

    /**
     * 隐藏动画
     */
    private Animation mHideAnim;

    /**
     * 控制器显示超时时间：即显示超过该时间后自动隐藏
     */
    private int mDefaultTimeout = 4000;

    /**
     * 自动隐藏的Runnable
     */
    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * 是否开始刷新进度
     */
    private boolean mProgressRefreshing;

    /**
     * 刷新进度Runnable
     */
    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = updateProgress();
            if (mPlayer.isPlaying()) {
                postDelayed(this, (long) ((1000 - pos % 1000) / mPlayer.getSpeed()));
            } else {
                mProgressRefreshing = false;
            }
        }
    };

    /**
     * 屏幕角度传感器监听
     */
    private DeviceOrientationSensorHelper mOrientationSensorHelper;

    /**
     * 是否开启根据传感器获得的屏幕方向进入/退出全屏
     */
    private boolean mEnableOrientationSensor;

    /**
     * 用户设置是否适配刘海屏
     */
    private boolean mAdaptCutout;

    /**
     * 是否有刘海
     */
    private Boolean mHasCutout;

    /**
     * 刘海的高度
     */
    private int mCutoutHeight;

    /**
     * 控制器是否处于显示状态
     */
    protected boolean mShowing;

    @Nullable
    protected Activity mActivity;

    //播放器包装类，集合了MediaPlayerControl的api和IVideoController的api

    /**
     * 此类过于臃肿，想改善这个类
     */
    protected ControlWrapper mControlWrapper;


    public MediaController(@NonNull Context context) {
        this(context, null);
    }

    public MediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        }
        mOrientationSensorHelper = new DeviceOrientationSensorHelper(getContext().getApplicationContext(), PlayerUtils.scanForActivity(getContext()));
        //开始监听设备方向
        mOrientationSensorHelper.setDeviceOrientationChangedListener(this);
        setEnableOrientationSensor(DKManager.isOrientationSensorEnabled());
        mAdaptCutout = DKManager.isAdaptCutout();

        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);

        mActivity = getPreferredActivity();
    }

    protected Activity getPreferredActivity() {
        if (mActivity == null) {
            mActivity = PlayerUtils.scanForActivity(getContext());
        }
        return mActivity;
    }

    public ControlWrapper getControlWrapper() {
        return mControlWrapper;
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 是否处于播放状态
     *
     * @return
     */
    protected boolean isInPlaybackState() {
        return mPlayer != null
                && mPlayerState != DKVideoView.STATE_ERROR
                && mPlayerState != DKVideoView.STATE_IDLE
                && mPlayerState != DKVideoView.STATE_PREPARING
                && mPlayerState != DKVideoView.STATE_PREPARED
                && mPlayerState != DKVideoView.STATE_START_ABORT
                && mPlayerState != DKVideoView.STATE_PLAYBACK_COMPLETED;
    }


    /**
     * 重要：此方法用于将{@link DKVideoView} 和控制器绑定
     */
    @CallSuper
    public void setMediaPlayer(VideoViewControl mediaPlayer) {
        mControlWrapper = new ControlWrapper(mediaPlayer, this);
        mPlayer = mediaPlayer;
        //绑定ControlComponent和Controller
        for (Map.Entry<ControlComponent, Boolean> next : mControlComponents.entrySet()) {
            ControlComponent component = next.getKey();
            component.attach(mControlWrapper);
        }
    }

    /***********START 关键方法代码************/

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     */
    public void addControlComponent(@NonNull ControlComponent... component) {
        for (ControlComponent item : component) {
            addControlComponent(item, false);
        }
    }

    /**
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     *
     * @param isDissociate 是否为游离的控制组件，
     *                     如果为 true ControlComponent 不会添加到控制器中，ControlComponent 将独立于控制器而存在，
     *                     如果为 false ControlComponent 将会被添加到控制器中，并显示出来。
     *                     为什么要让 ControlComponent 将独立于控制器而存在，假设有如下几种情况：
     *                     情况一：
     *                     如果在一个列表中控制器是复用的，但是控制器的某些部分是不能复用的，比如封面图，
     *                     此时你就可以将封面图拆分成一个游离的 ControlComponent，并把这个 ControlComponent
     *                     放在 item 的布局中，就可以实现每个item的封面图都是不一样，并且封面图可以随着播放器的状态显示和隐藏。
     *                     demo中演示的就是这种情况。
     *                     情况二：
     *                     假设有这样一种需求，播放器控制区域在显示区域的下面，此时你就可以通过自定义 ControlComponent
     *                     并将 isDissociate 设置为 true 来实现这种效果。
     */
    public void addControlComponent(@NonNull ControlComponent component, boolean isDissociate) {
        mControlComponents.put(component, isDissociate);
        if (mControlWrapper != null) {
            component.attach(mControlWrapper);
        }
        View view = component.getView();
        if (view != null && !isDissociate) {
            addView(view, 0);
        }
    }

    /**
     * 移除某个控制组件
     */
    public void removeControlComponent(@NonNull ControlComponent component) {
        removeControlComponentView(component);
        mControlComponents.remove(component);
    }

    /**
     * 移除所有控制组件
     */
    public void removeAllControlComponent() {
        for (Map.Entry<ControlComponent, Boolean> item : mControlComponents.entrySet()) {
            removeControlComponentView(item.getKey());
        }
        mControlComponents.clear();
    }

    /**
     * 移除所有的游离控制组件
     * 关于游离控制组件的定义请看 {@link #addControlComponent(ControlComponent, boolean)} 关于 isDissociate 的解释
     */
    public void removeAllDissociateComponents() {
        Iterator<Map.Entry<ControlComponent, Boolean>> it = mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ControlComponent, Boolean> next = it.next();
            if (next.getValue()) {
                it.remove();
            }
        }
    }

    /**
     * 从当前控制器中移除添加的控制器view
     *
     * @param component
     */
    private void removeControlComponentView(ControlComponent component) {
        View view = component.getView();
        if (view == null)
            return;
        removeView(view);
    }

    /***********END 关键方法代码************/

    /***********START 关键方法代码************/

    @Override
    public boolean isFullScreen() {
        return mPlayer.isFullScreen();
    }

    /**
     * 横竖屏切换
     */
    @Override
    public boolean toggleFullScreen() {
        return mPlayer.toggleFullScreen();
    }

    @Override
    public boolean startFullScreen(boolean isLandscapeReversed) {
        return mPlayer.startFullScreen(isLandscapeReversed);
    }

    /**
     * 子类中请使用此方法来退出全屏
     *
     * @return 是否成功退出全屏
     */
    @Override
    public boolean stopFullScreen() {
        return mPlayer.stopFullScreen();
    }


    /**
     * 设置锁定状态
     *
     * @param locked 是否锁定
     */
    @Override
    public void setLocked(boolean locked) {
        mLocked = locked;
        notifyLockStateChanged(locked);
    }

    /**
     * 判断是否锁定
     *
     * @return true:当前已锁定界面
     */
    @Override
    public boolean isLocked() {
        return mLocked;
    }

    /**
     * 启用设备角度传感器(用于自动横竖屏切换),默认不启用
     */
    @Override
    public void setEnableOrientationSensor(boolean enableOrientation) {
        mEnableOrientationSensor = enableOrientation;
    }

    /**
     * 设置当前{@link DKVideoView}界面模式：竖屏、全屏、小窗模式等
     * 是当{@link DKVideoView}修改视图之后，调用此方法向控制器同步状态
     */
    @CallSuper
    public void setScreenMode(@ScreenMode int screenMode) {
        notifyScreenModeChanged(screenMode);
    }

    /**
     * call by {@link DKVideoView},设置播放器当前播放状态
     */
    @SuppressLint("SwitchIntDef")
    @CallSuper
    public void setPlayerState(@DKVideoView.PlayerState int playState) {
        mPlayerState = playState;
        for (Map.Entry<ControlComponent, Boolean> next : mControlComponents.entrySet()) {
            next.getKey().onPlayStateChanged(playState);
        }
        switch (playState) {
            case DKVideoView.STATE_IDLE:
                mOrientationSensorHelper.disable();
                mLocked = false;
                mShowing = false;
                //由于游离组件是独立于控制器存在的，
                //所以在播放器release的时候需要移除
                removeAllDissociateComponents();
                break;
            case DKVideoView.STATE_PLAYBACK_COMPLETED:
                mLocked = false;
                mShowing = false;
                break;
            case DKVideoView.STATE_ERROR:
                mShowing = false;
                break;
        }
        onPlayerStateChanged(playState);
    }

    /**
     * 控制器是否已隐藏
     *
     * @return
     */
    @Override
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * 显示播放视图
     */
    @Override
    public void show() {
        if (mShowing)
            return;
        handleVisibilityChanged(true, mShowAnim);
        startFadeOut();
        mShowing = true;
    }

    /**
     * 隐藏播放视图
     */
    @Override
    public void hide() {
        if (!mShowing)
            return;
        stopFadeOut();
        handleVisibilityChanged(false, mHideAnim);
        mShowing = false;
    }

    /**
     * 设置自动隐藏倒计时持续的时间
     *
     * @param timeout 默认4000，比如大于0才会生效
     */
    @Override
    public void setFadeOutTime(@IntRange(from = 1) int timeout) {
        if (timeout > 0) {
            mDefaultTimeout = timeout;
        }
    }

    /**
     * 开始倒计时隐藏控制器
     */
    @Override
    public void startFadeOut() {
        //重新开始计时
        stopFadeOut();
        postDelayed(mFadeOut, mDefaultTimeout);
    }

    /**
     * 移除控制器隐藏倒计时
     */
    @Override
    public void stopFadeOut() {
        removeCallbacks(mFadeOut);
    }

    /**
     * 开始刷新进度，注意：需在STATE_PLAYING时调用才会开始刷新进度
     */
    @Override
    public void startUpdateProgress() {
        if (mProgressRefreshing) return;
        post(mShowProgress);
        mProgressRefreshing = true;
    }

    /**
     * 停止刷新进度
     */
    @Override
    public void stopUpdateProgress() {
        if (!mProgressRefreshing) return;
        removeCallbacks(mShowProgress);
        mProgressRefreshing = false;
    }

    /**
     * 设置是否适配刘海屏
     */
    @Override
    public void setAdaptCutout(boolean adaptCutout) {
        mAdaptCutout = adaptCutout;
    }

    /**
     * 是否有刘海屏
     */
    @Override
    public boolean hasCutout() {
        return mHasCutout != null && mHasCutout;
    }

    /**
     * 刘海的高度
     */
    @Override
    public int getCutoutHeight() {
        return mCutoutHeight;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkCutout();
    }

    /**
     * 检查是否需要适配刘海
     */
    private void checkCutout() {
        if (!mAdaptCutout) return;
        if (mActivity != null && mHasCutout == null) {
            mHasCutout = CutoutUtil.allowDisplayToCutout(mActivity);
            if (mHasCutout) {
                //竖屏下的状态栏高度可认为是刘海的高度
                mCutoutHeight = (int) PlayerUtils.getStatusBarHeightPortrait(mActivity);
            }
        }
        L.d("hasCutout: " + mHasCutout + " cutout height: " + mCutoutHeight);
    }

    /**
     * 显示移动网络播放提示
     *
     * @return 返回显示移动网络播放提示的条件，false:不显示, true显示
     * 此处默认根据手机网络类型来决定是否显示，开发者可以重写相关逻辑
     */
    public boolean showNetWarning() {
        return PlayerUtils.getNetworkType(getContext()) == PlayerUtils.NETWORK_MOBILE
                && !DKManager.isPlayOnMobileNetwork();
    }

    /**
     * 播放和暂停
     */
    protected void togglePlay() {
        mControlWrapper.togglePlay();
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
        if (mPlayer.isPlaying() && (mEnableOrientationSensor || mPlayer.isFullScreen())) {
            if (hasWindowFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOrientationSensorHelper.enable();
                    }
                }, 800);
            } else {
                mOrientationSensorHelper.disable();
            }
        }
    }


    @CallSuper
    @Override
    public void onDeviceDirectionChanged(@DeviceOrientationSensorHelper.DeviceDirection int direction) {
        if (direction == DeviceOrientationSensorHelper.DEVICE_DIRECTION_PORTRAIT) {
            //切换为竖屏
            //屏幕锁定的情况
            if (mLocked) return;
            //没有开启设备方向监听的情况
            if (!mEnableOrientationSensor) return;
            mPlayer.stopFullScreen();
        } else if (direction == DeviceOrientationSensorHelper.DEVICE_DIRECTION_LANDSCAPE) {
            mPlayer.startFullScreen();
        } else if (direction == DeviceOrientationSensorHelper.DEVICE_DIRECTION_LANDSCAPE_REVERSED) {
            mPlayer.startFullScreen(true);
        }
    }

    //------------------------ start handle event change ------------------------//

    private void handleVisibilityChanged(boolean isVisible, Animation anim) {
        if (!mLocked) { //没锁住时才向ControlComponent下发此事件
            for (Map.Entry<ControlComponent, Boolean> next
                    : mControlComponents.entrySet()) {
                ControlComponent component = next.getKey();
                component.onVisibilityChanged(isVisible, anim);
            }
        }
        onVisibilityChanged(isVisible, anim);
    }

    /**
     * 子类重写此方法监听控制的显示和隐藏
     *
     * @param isVisible 是否可见
     * @param anim      显示/隐藏动画
     */
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    /**
     * 用于子类重写
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前播放位置
     */
    protected void onProgressChanged(int duration, int position) {
    }

    /**
     * 用于子类重写
     */
    @CallSuper
    protected void onScreenModeChanged(int screenMode) {
    }

    /**
     * 用于子类重写
     */
    @CallSuper
    protected void onPlayerStateChanged(@DKVideoView.PlayerState int playState) {
    }

    /**
     * 用于子类重写
     */
    protected void onLockStateChanged(boolean isLocked) {
    }

    /**
     * 更新当前播放进度
     *
     * @return 当前播放位置
     */
    private int updateProgress() {
        int position = (int) mPlayer.getCurrentPosition();
        int duration = (int) mPlayer.getDuration();
        for (Map.Entry<ControlComponent, Boolean> next : mControlComponents.entrySet()) {
            ControlComponent component = next.getKey();
            component.onProgressChanged(duration, position);
        }
        onProgressChanged(duration, position);
        return position;
    }

    /**
     * 通知界面模式发生改变
     *
     * @param screenMode
     */
    private void notifyScreenModeChanged(@ScreenMode int screenMode) {
        for (Map.Entry<ControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            ControlComponent component = next.getKey();
            component.onScreenModeChanged(screenMode);
        }
        setupOrientationSensorAndCutoutOnScreenModeChanged(screenMode);
        onScreenModeChanged(screenMode);
    }

    /**
     * 在屏幕模式改变了的情况下，调整传感器和刘海屏
     *
     * @param screenMode
     */
    private void setupOrientationSensorAndCutoutOnScreenModeChanged(@ScreenMode int screenMode) {
        //修改传感器
        switch (screenMode) {
            case ScreenMode.NORMAL:
                if (mEnableOrientationSensor) {
                    mOrientationSensorHelper.enable();
                } else {
                    mOrientationSensorHelper.disable();
                }
                if (hasCutout()) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), false);
                }
                break;
            case ScreenMode.FULL:
                //在全屏时强制监听设备方向
                mOrientationSensorHelper.enable();
                if (hasCutout()) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), true);
                }
                break;
            case ScreenMode.TINY:
                mOrientationSensorHelper.disable();
                break;
        }
    }

    /**
     * 通知锁定状态发生了变化
     */
    private void notifyLockStateChanged(boolean isLocked) {
        for (Map.Entry<ControlComponent, Boolean> next : mControlComponents.entrySet()) {
            ControlComponent component = next.getKey();
            component.onLockStateChanged(isLocked);
        }
        onLockStateChanged(isLocked);
    }

    //------------------------ end handle event change ------------------------//
}
