package xyz.doikki.videoplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import xyz.doikki.videoplayer.util.L;

/**
 * 设备方向 处理帮助类
 *
 * @see #enable() 启用自动旋转
 * @see #disable() 禁用自动旋转
 */
public class ScreenOrientationHelper extends OrientationEventListener {

    /**
     * 未检测到方向
     */
    public static final int SCREEN_ORIENTATION_UNKNOWN = -1;

    /**
     * 垂直正向（即竖着正向拿着手机）
     */
    public static final int SCREEN_ORIENTATION_PORTRAIT = 0;

    /**
     * 横向 正向（即横着向右拿着手机：手机的顶部朝着右边）
     */
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 270;

    /**
     * 横向反向（即横着向左拿着手机:手机的顶部朝着左边）
     */
    public static final int SCREEN_ORIENTATION_LANDSCAPE_REVERSED = 90;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SCREEN_ORIENTATION_UNKNOWN,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_LANDSCAPE_REVERSED})
    public @interface ScreenOrientation {
    }

    /**
     * 屏幕旋转角度检查的最小间隔时间
     */
    public static final long CHECK_INTERVAL = 300;

    /**
     * 方向偏差角度：默认为前后15度，比如竖屏方向，则认为在时钟0点方向左右15度为正向竖屏方向。 而横向方向则为 90度前后15度，即75-105度的方向为正向横向方向。
     */
    public static final long ORIENTATION_DEVIATION = 15;

    /**
     * 上次检测屏幕旋转角度的时间
     */
    private long mLastCheckTime;

    /**
     * 当前设备方向
     */
    @ScreenOrientation
    private int mOrientation = SCREEN_ORIENTATION_UNKNOWN;

    /**
     * 间接引用的Activity
     */
    private WeakReference<Activity> mActivityRef;

    /**
     * 回调
     */
    private OnOrientationChangeListener mOnOrientationChangeListener;

    public ScreenOrientationHelper(Context context) {
        super(context);
    }

    @Override
    public void disable() {
        super.disable();
        //重置本地变量
        mOrientation = SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 将屏幕旋转事件绑定到Activity操作上
     *
     * @param activity
     */
    public void attachActivity(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    /**
     * 解除界面绑定
     */
    public void detachActivity() {
        mActivityRef = null;
    }

    private Activity requireActivity() {
        if (mActivityRef != null)
            return mActivityRef.get();
        return null;
    }

    /**
     * 处理当前设备方向旋转角度
     *
     * @param orientation 设备旋转角度 从0-360；这个角度跟时钟方向是相似的即0点位0度，3点方向为90度，6点方向为180度，9点方向为270度
     *                    当手机平放时，返回{@link #ORIENTATION_UNKNOWN} -1;
     */
    @Override
    public void onOrientationChanged(int orientation) {
        L.i("onOrientationChanged " + orientation + "thread name:" + Thread.currentThread().getName());
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCheckTime < CHECK_INTERVAL) return;
        handleOrientationChanged(orientation);
        mLastCheckTime = currentTime;
    }

    private void handleOrientationChanged(int orientation) {
        Activity activity = requireActivity();
        if (activity == null)
            return;

        //记录用户手机上一次放置的位置
        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            //手机平放时，检测不到有效的角度
            mOrientation = SCREEN_ORIENTATION_UNKNOWN;
            return;
        }

        //获取在Manifest文件中配置的orientation或者是用android.app.Activity.setRequestedOrientation(int)设置的orientation值
        int activityRequestedOrientation = activity.getRequestedOrientation();

        if (orientation > (360 - ORIENTATION_DEVIATION) || orientation < ORIENTATION_DEVIATION) {   //当前设备角度为正向竖屏
            //当前界面处于横屏模式，而之前的角度是竖屏，则这种情况一般是用户手动切换成了横屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == SCREEN_ORIENTATION_PORTRAIT)
                return;

            //与之前角度值一样，不做改变
            if (mOrientation == SCREEN_ORIENTATION_PORTRAIT) return;

            //切换成竖屏
            mOrientation = SCREEN_ORIENTATION_PORTRAIT;
            notifyScreenOrientationChanged(SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation > (SCREEN_ORIENTATION_LANDSCAPE - ORIENTATION_DEVIATION) && orientation < (SCREEN_ORIENTATION_LANDSCAPE + ORIENTATION_DEVIATION)) {//当前设备角度为正向横屏
            //手动切换横竖屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == SCREEN_ORIENTATION_LANDSCAPE)
                return;

            //与之前角度值一样，不做改变
            if (mOrientation == SCREEN_ORIENTATION_LANDSCAPE) return;
            //90度，用户右侧横屏拿着手机
            mOrientation = SCREEN_ORIENTATION_LANDSCAPE;
            notifyScreenOrientationChanged(SCREEN_ORIENTATION_LANDSCAPE);

        } else if (orientation > (SCREEN_ORIENTATION_LANDSCAPE_REVERSED - ORIENTATION_DEVIATION) && orientation < (SCREEN_ORIENTATION_LANDSCAPE_REVERSED + ORIENTATION_DEVIATION)) {
            //手动切换横竖屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == SCREEN_ORIENTATION_LANDSCAPE_REVERSED)
                return;
            if (mOrientation == SCREEN_ORIENTATION_LANDSCAPE_REVERSED) return;
            //270度，用户左侧横屏拿着手机
            mOrientation = SCREEN_ORIENTATION_LANDSCAPE_REVERSED;
            notifyScreenOrientationChanged(SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
//
//    /**
//     * 竖屏
//     */
//    protected void onOrientationPortrait(Activity activity) {
//        //屏幕锁定的情况
//        if (mIsLocked) return;
//        //没有开启设备方向监听的情况
//        if (!mEnableOrientation) return;
//
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        mControlWrapper.stopFullScreen();
//    }
//
//    /**
//     * 横屏
//     */
//    protected void onOrientationLandscape(Activity activity) {
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        if (mControlWrapper.isFullScreen()) {
//            handlePlayerStateChanged(ScreenMode.FULL);
//        } else {
//            mControlWrapper.startFullScreen();
//        }
//    }
//
//    /**
//     * 反向横屏
//     */
//    protected void onOrientationReverseLandscape(Activity activity) {
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//        if (mControlWrapper.isFullScreen()) {
//            handlePlayerStateChanged(ScreenMode.FULL);
//        } else {
//            mControlWrapper.startFullScreen();
//        }
//    }

    private void notifyScreenOrientationChanged(@ScreenOrientation int screenOrientation) {
        if (mOnOrientationChangeListener != null) {
            mOnOrientationChangeListener.onOrientationChanged(screenOrientation);
        }
    }

    public interface OnOrientationChangeListener {
        void onOrientationChanged(@ScreenOrientation int orientation);
    }

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener) {
        mOnOrientationChangeListener = onOrientationChangeListener;
    }
}
