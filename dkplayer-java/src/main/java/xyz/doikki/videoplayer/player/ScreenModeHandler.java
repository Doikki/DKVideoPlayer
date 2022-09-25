package xyz.doikki.videoplayer.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import xyz.doikki.videoplayer.R;

/**
 * 处理播放器屏幕切换
 *
 * @see #startFullScreen(Activity, View) 全屏显示指定的view
 * @see #stopFullScreen(Activity, View) 退出全屏
 * @see #startTinyScreen(Activity, View) 小窗显示
 * @see #stopTinyScreen(View) 小窗显示回到正常显示
 * @see #setTinyScreenSize 设置小窗大小
 */
public class ScreenModeHandler {

    /**
     * 正常显示情况下的容器
     */
    private final ViewGroup mNormalContainer;

    /**
     * 小屏窗口大小
     */
    private final int[] mTinyScreenSize = {0, 0};

    /**
     * 推荐的小窗宽度
     */
    @Px
    private int mPreferredTinyScreenWidth = 0;

    /**
     * 推荐的小窗高度
     */
    @Px
    private int mPreferredTinyScreenHeight = 0;

    /**
     * @param normalContainer 正常显示时控件所在的容器
     */
    public ScreenModeHandler(@NonNull ViewGroup normalContainer) {
        mNormalContainer = normalContainer;
    }

    /**
     * 设置小屏的宽高
     */
    public void setTinyScreenSize(int width, int height) {
        mTinyScreenSize[0] = width;
        mTinyScreenSize[1] = height;
    }

    /**
     * 切换到全屏
     *
     * @param view 用于全屏展示的view
     */
    public boolean startFullScreen(@NonNull Activity activity, View view) {
        ViewGroup decorView = getDecorView(activity);
        if (decorView == null)
            return false;
        //隐藏NavigationBar和StatusBar
        hideSystemBar(activity, decorView);
        //从parent中移除指定view
        removeFromParent(view);
        //将视图添加到DecorView中即实现了全屏展示该控件
        decorView.addView(view);
        return true;
    }

    /**
     * 退出全屏
     *
     * @param view 全屏展示的view ： 本身这个参数可以不传，但是还是保留，这样更明确逻辑
     */
    public boolean stopFullScreen(Activity activity, View view) {
        ViewGroup decorView = getDecorView(activity);
        if (decorView != null) {
            //显示状态栏
            showSystemBar(activity, decorView);
        }
        removeFromParent(view);
        mNormalContainer.addView(view);

        return true;
    }

    /**
     * 开启小屏
     *
     * @param view 在小窗口中显示的View
     */
    public boolean startTinyScreen(Activity activity, View view) {
        ViewGroup contentView = getContentView(activity);
        if (contentView == null) return false;
        removeFromParent(view);

        //缓存原来的布局参数
        ViewGroup.LayoutParams layoutParamsCache = view.getLayoutParams();
        view.setTag(R.id.screen_mode_layout_params, layoutParamsCache);

        int width = getTinyScreenWidth(activity);
        int height = getTinyScreenHeight(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;

        contentView.addView(view, params);
        return true;
    }

    /**
     * 退出小屏
     *
     * @param view 在小窗口中显示的view：本身这个参数可以不传，但是还是保留，这样更明确逻辑
     */
    public boolean stopTinyScreen(View view) {
        removeFromParent(view);
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) view.getTag(R.id.screen_mode_layout_params);
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mNormalContainer.addView(view, lp);
        return true;
    }

    /**
     * 获取小窗宽度
     */
    @Px
    private int getTinyScreenWidth(@NonNull Activity activity) {
        int width = mTinyScreenSize[0];
        if (width > 0)
            return width;
        setupPreferredTinyScreenSize(activity);
        return mPreferredTinyScreenWidth;
    }

    /**
     * 获取小窗高度
     */
    @Px
    private int getTinyScreenHeight(@NonNull Activity activity) {
        int height = mTinyScreenSize[1];
        if (height > 0)
            return height;
        return mPreferredTinyScreenHeight;
    }

    /**
     * 初始化默认的小窗大小
     */
    private void setupPreferredTinyScreenSize(Activity activity) {
        if (mPreferredTinyScreenWidth > 0)
            return;
        mPreferredTinyScreenWidth = activity.getResources().getDisplayMetrics().widthPixels / 2;
        mPreferredTinyScreenHeight = mPreferredTinyScreenWidth * 9 / 16;
    }

    /**
     * 显示系统状态栏(NavigationBar和StatusBar)
     */
    public static void showSystemBar(Activity activity) {
        ViewGroup decorView = getDecorView(activity);
        if (decorView == null)
            return;
        showSystemBar(activity, decorView);
    }

    /**
     * 显示系统状态栏(NavigationBar和StatusBar)
     */
    public static void showSystemBar(Activity activity, ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏系统状态栏(NavigationBar和StatusBar)
     *
     * @param activity
     */
    public static void hideSystemBar(Activity activity) {
        ViewGroup decorView = getDecorView(activity);
        if (decorView == null)
            return;
        hideSystemBar(activity, decorView);
    }

    /**
     * 隐藏系统状态栏(NavigationBar和StatusBar)
     *
     * @param activity
     */
    public static void hideSystemBar(Activity activity, ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 从Parent中移除自己
     */
    private static void removeFromParent(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    /**
     * 获取DecorView
     */
    @Nullable
    private static ViewGroup getDecorView(@NonNull Activity activity) {
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /**
     * 获取activity中的content view,其id为android.R.id.content
     */
    @Nullable
    protected ViewGroup getContentView(@NonNull Activity activity) {
        return activity.findViewById(android.R.id.content);
    }
}
