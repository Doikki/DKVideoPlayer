package xyz.doikki.videocontroller.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import xyz.doikki.videoplayer.util.L;

/**
 * 设备旋转角度感应器 帮助类
 *
 * @see #enable() 启用传感器
 * @see #disable() 禁用传感器
 * @see #setDeviceOrientationChangedListener(DeviceOrientationChangedListener) 设置方向改变回调
 * @see DeviceOrientationChangedListener#onDeviceDirectionChanged  当屏幕方向发生了变化，则会回调该方法，在此方法中处理视频的横竖屏播放较好
 * @see DeviceOrientationChangedListener#onDeviceOrientationChanged(int)  当屏幕角度发生变化时回调该方法，也就是{@link OrientationEventListener#onOrientationChanged(int)}方法的回调
 */
public class DeviceOrientationSensorHelper extends OrientationEventListener {

    /**
     * 未检测到方向：手机可能处于平放状态
     */
    public static final int DEVICE_DIRECTION_UNKNOWN = -1;

    /**
     * 竖屏（即竖着正向拿着手机）
     */
    public static final int DEVICE_DIRECTION_PORTRAIT = 0;

    /**
     * 正向横屏（即横着向右拿着手机：手机的顶部朝着右边）
     */
    public static final int DEVICE_DIRECTION_LANDSCAPE = 270;

    /**
     * 反向横屏（即横着向左拿着手机:手机的顶部朝着左边）
     */
    public static final int DEVICE_DIRECTION_LANDSCAPE_REVERSED = 90;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DEVICE_DIRECTION_UNKNOWN,
            DEVICE_DIRECTION_PORTRAIT,
            DEVICE_DIRECTION_LANDSCAPE,
            DEVICE_DIRECTION_LANDSCAPE_REVERSED})
    public @interface DeviceDirection {
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
    @DeviceDirection
    private int mOrientation = DEVICE_DIRECTION_UNKNOWN;

    /**
     * 间接引用的Activity
     */
    private WeakReference<Activity> mActivityRef;

    /**
     * 方向发生了改变回调
     */
    private DeviceOrientationChangedListener mOrientationChangedListener;

    /**
     * @deprecated  请使用{@link #DeviceOrientationSensorHelper(Context, Activity)}
     */
    @Deprecated
    public DeviceOrientationSensorHelper(Context context) {
        super(context.getApplicationContext());
    }

    /**
     * @param context
     * @param activity 用于确定界面当前横竖屏状态，本类逻辑依赖{@link Activity},否则无法执行，在创建实例时如果无法确定Activity，则可以后续调用{@link #attachActivity(Activity)}绑定
     */
    public DeviceOrientationSensorHelper(@NonNull Context context, @Nullable Activity activity) {
        this(context);
        if (activity != null)
            mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void disable() {
        super.disable();
        //重置本地变量
        mOrientation = DEVICE_DIRECTION_PORTRAIT;
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

    public void setDeviceOrientationChangedListener(DeviceOrientationChangedListener deviceOrientationListener) {
        mOrientationChangedListener = deviceOrientationListener;
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
        try {
            if (mOrientationChangedListener != null) {
                mOrientationChangedListener.onDeviceOrientationChanged(orientation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Activity activity = requireActivity();
        if (activity == null)
            return;

        //记录用户手机上一次放置的位置
        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            //手机平放时，检测不到有效的角度
            mOrientation = DEVICE_DIRECTION_UNKNOWN;
            return;
        }

        //获取在Manifest文件中配置的orientation或者是用android.app.Activity.setRequestedOrientation(int)设置的orientation值
        int activityRequestedOrientation = activity.getRequestedOrientation();

        if (orientation > (360 - ORIENTATION_DEVIATION) || orientation < ORIENTATION_DEVIATION) {   //当前设备角度为正向竖屏
            //当前界面处于横屏模式，而之前的角度是竖屏，则这种情况一般是用户手动切换成了横屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == DEVICE_DIRECTION_PORTRAIT)
                return;

            //与之前角度值一样，不做改变
            if (mOrientation == DEVICE_DIRECTION_PORTRAIT) return;

            //切换成竖屏
            mOrientation = DEVICE_DIRECTION_PORTRAIT;
            notifyDeviceDirectionChanged(DEVICE_DIRECTION_PORTRAIT);
        } else if (orientation > (DEVICE_DIRECTION_LANDSCAPE - ORIENTATION_DEVIATION) && orientation < (DEVICE_DIRECTION_LANDSCAPE + ORIENTATION_DEVIATION)) {//当前设备角度为正向横屏
            //手动切换横竖屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == DEVICE_DIRECTION_LANDSCAPE)
                return;

            //与之前角度值一样，不做改变
            if (mOrientation == DEVICE_DIRECTION_LANDSCAPE) return;

            //90度，用户右侧横屏拿着手机
            mOrientation = DEVICE_DIRECTION_LANDSCAPE;
            notifyDeviceDirectionChanged(DEVICE_DIRECTION_LANDSCAPE);

        } else if (orientation > (DEVICE_DIRECTION_LANDSCAPE_REVERSED - ORIENTATION_DEVIATION) && orientation < (DEVICE_DIRECTION_LANDSCAPE_REVERSED + ORIENTATION_DEVIATION)) {
            //手动切换横竖屏
            if (activityRequestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == DEVICE_DIRECTION_LANDSCAPE_REVERSED)
                return;

            if (mOrientation == DEVICE_DIRECTION_LANDSCAPE_REVERSED) return;
            //270度，用户左侧横屏拿着手机
            mOrientation = DEVICE_DIRECTION_LANDSCAPE_REVERSED;
            notifyDeviceDirectionChanged(DEVICE_DIRECTION_LANDSCAPE_REVERSED);
        }
    }

    private void notifyDeviceDirectionChanged(@DeviceDirection int screenOrientation) {
        if (mOrientationChangedListener != null) {
            mOrientationChangedListener.onDeviceDirectionChanged(screenOrientation);
        }
    }

    public interface DeviceOrientationChangedListener {

        /**
         * 方向发生了变化
         *
         * @param direction 方向：竖屏、正向横屏、反向横屏
         */
        default void onDeviceDirectionChanged(@DeviceDirection int direction) {
        }

        /**
         * 角度发生了变化
         *
         * @param orientation 角度：0-360度，如果未-1则表示当前为平放
         */
        default void onDeviceOrientationChanged(@DeviceDirection int orientation) {
        }

    }

}
