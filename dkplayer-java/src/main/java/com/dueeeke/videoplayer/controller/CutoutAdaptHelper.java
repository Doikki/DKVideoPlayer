package com.dueeeke.videoplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 刘海屏适配辅助类
 */
public class CutoutAdaptHelper {

    private boolean mAdaptCutout;

    private int mSpace;

    private int mCurrentOrientation = -1;

    private Activity mActivity;

    private BaseVideoController mController;

    private boolean mIsChecked;

    public CutoutAdaptHelper(@NonNull BaseVideoController controller) {
        mController = controller;
        mActivity = PlayerUtils.scanForActivity(controller.getContext());
        if (mActivity == null) {
            L.w("Activity can not be null!");
            return;
        }
        AdaptView adaptView = new AdaptView(mActivity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(0, 0);
        controller.addView(adaptView, lp);
    }

    public void onOrientationChanged() {
        adjustView();
    }

    public void onPlayerStateChanged(int playerState) {
        if (!mAdaptCutout) return;
        if (playerState == VideoView.PLAYER_NORMAL) {
            adaptCutoutAboveAndroidP(false);
        } else if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            adaptCutoutAboveAndroidP(true);
        }
    }

    public boolean getAdaptCutout() {
        return mAdaptCutout;
    }

    /**
     * 适配刘海屏，针对Android P以上系统
     */
    private void adaptCutoutAboveAndroidP(boolean isAdapt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            if (isAdapt) {
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            }
            mActivity.getWindow().setAttributes(lp);
        }
    }

    private void adjustView() {
        if (mAdaptCutout) {
            int o = mActivity.getRequestedOrientation();
            if (o == mCurrentOrientation) {
                return;
            }
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mController.adjustView(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, mSpace);
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mController.adjustView(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, mSpace);
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mController.adjustView(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, mSpace);
            }
            mCurrentOrientation = o;
        }
    }

    /**
     * 将此View添加到ContentView中，用于监听系统回调事件
     */
    private class AdaptView extends View {

        public AdaptView(Context context) {
            super(context);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            checkCutout();
        }

        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            onOrientationChanged();
        }

        /**
         * 检查是否需要适配刘海
         */
        private void checkCutout() {
            if (mIsChecked) return;
            mAdaptCutout = allowDisplayToCutout();
            if (mAdaptCutout) {
                mSpace = (int) PlayerUtils.getStatusBarHeight(mActivity);
            }
            L.d("adaptCutout: " + mAdaptCutout + " space: " + mSpace);
            mIsChecked = true;
        }

        /**
         * 是否为允许全屏界面显示内容到刘海区域的刘海屏机型（与AndroidManifest中配置对应）
         */
        public boolean allowDisplayToCutout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // 9.0系统全屏界面默认会保留黑边，不允许显示内容到刘海区域
                Window window = mActivity.getWindow();
                WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
                if (windowInsets == null) {
                    return false;
                }
                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                if (displayCutout == null) {
                    return false;
                }
                List<Rect> boundingRects = displayCutout.getBoundingRects();
                return boundingRects.size() > 0;
            } else {
                return hasCutoutHuawei()
                        || hasCutoutOPPO()
                        || hasCutoutVIVO()
                        || hasCutoutXIAOMI();
            }
        }

        /**
         * 是否是华为刘海屏机型
         */
        @SuppressWarnings("unchecked")
        private boolean hasCutoutHuawei() {
            if (!Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) {
                return false;
            }
            try {
                ClassLoader cl = mActivity.getClassLoader();
                Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
                if (HwNotchSizeUtil != null) {
                    Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
                    return (boolean) get.invoke(HwNotchSizeUtil);
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 是否是oppo刘海屏机型
         */
        private boolean hasCutoutOPPO() {
            if (!Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
                return false;
            }
            return mActivity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        }

        /**
         * 是否是vivo刘海屏机型
         */
        @SuppressWarnings("unchecked")
        @SuppressLint("PrivateApi")
        private boolean hasCutoutVIVO() {
            if (!Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
                return false;
            }
            try {
                ClassLoader cl = mActivity.getClassLoader();
                Class ftFeatureUtil = cl.loadClass("android.util.FtFeature");
                if (ftFeatureUtil != null) {
                    Method get = ftFeatureUtil.getMethod("isFeatureSupport", int.class);
                    return (boolean) get.invoke(ftFeatureUtil, 0x00000020);
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 是否是小米刘海屏机型
         */
        @SuppressWarnings("unchecked")
        @SuppressLint("PrivateApi")
        private boolean hasCutoutXIAOMI() {
            if (!Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
                return false;
            }
            try {
                ClassLoader cl = mActivity.getClassLoader();
                Class SystemProperties = cl.loadClass("android.os.SystemProperties");
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = "ro.miui.notch";
                params[1] = 0;
                int hasCutout = (int) getInt.invoke(SystemProperties, params);
                return hasCutout == 1;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
