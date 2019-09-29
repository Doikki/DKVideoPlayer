package com.dueeeke.videocontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.dueeeke.videoplayer.util.PlayerUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <!--允许绘制到oppo、vivo刘海屏机型的刘海区域 -->
 * <meta-data
 *     android:name="android.max_aspect"
 *     android:value="2.2" />
 *
 * <!-- 允许绘制到华为刘海屏机型的刘海区域 -->
 * <meta-data
 *     android:name="android.notch_support"
 *     android:value="true" />
 *
 * <!-- 允许绘制到小米刘海屏机型的刘海区域 -->
 * <meta-data
 *     android:name="notch.config"
 *     android:value="portrait" />
 */
public class CutoutUtil {
 
    private static Boolean sAllowDisplayToCutout;
 
    /** 
     * 是否为允许全屏界面显示内容到刘海区域的刘海屏机型（与AndroidManifest中配置对应） 
     */ 
    public static boolean allowDisplayToCutout(Context context) {
        if (sAllowDisplayToCutout == null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                // 9.0系统全屏界面默认会保留黑边，不允许显示内容到刘海区域
                Activity activity = PlayerUtils.scanForActivity(context);
                if (activity == null) {
                    return sAllowDisplayToCutout = false;
                }

                DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (displayCutout == null) {
                    return sAllowDisplayToCutout = true;
                }
                List<Rect> boundingRects = displayCutout.getBoundingRects();
                if (boundingRects.size() > 0) {
                    return sAllowDisplayToCutout = true;
                }
                return sAllowDisplayToCutout = false;
            }
            Context app = PlayerUtils.getApplication();
            if (hasCutout_Huawei(app)) {
                return sAllowDisplayToCutout = true; 
            } 
            if (hasCutout_OPPO(app)) {
                return sAllowDisplayToCutout = true; 
            } 
            if (hasCutout_VIVO(app)) {
                return sAllowDisplayToCutout = true; 
            } 
            if (hasCutout_XIAOMI(app)) {
                return sAllowDisplayToCutout = true; 
            } 
            return sAllowDisplayToCutout = false; 
        } else { 
            return sAllowDisplayToCutout; 
        } 
    } 
 
 
    /** 
     * 是否是华为刘海屏机型 
     */ 
    @SuppressWarnings("unchecked") 
    private static boolean hasCutout_Huawei(Context context) { 
        if (!Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) { 
            return false; 
        } 
        try { 
            ClassLoader cl = context.getClassLoader(); 
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
    @SuppressWarnings("unchecked") 
    private static boolean hasCutout_OPPO(Context context) { 
        if (!Build.MANUFACTURER.equalsIgnoreCase("oppo")) { 
            return false; 
        } 
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism"); 
    } 
 
    /** 
     * 是否是vivo刘海屏机型 
     */ 
    @SuppressWarnings("unchecked") 
    private static boolean hasCutout_VIVO(Context context) { 
        if (!Build.MANUFACTURER.equalsIgnoreCase("vivo")) { 
            return false; 
        } 
        try { 
            ClassLoader cl = context.getClassLoader(); 
            @SuppressLint("PrivateApi")
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
    private static boolean hasCutout_XIAOMI(Context context) { 
        if (!Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) { 
            return false; 
        } 
        try { 
            ClassLoader cl = context.getClassLoader(); 
            @SuppressLint("PrivateApi") 
            Class SystemProperties = cl.loadClass("android.os.SystemProperties"); 
            Class[] paramTypes = new Class[2]; 
            paramTypes[0] = String.class; 
            paramTypes[1] = int.class; 
            Method getInt = SystemProperties.getMethod("getInt", paramTypes); 
            //参数 
            Object[] params = new Object[2]; 
            params[0] = "ro.miui.notch"; 
            params[1] = 0; 
            return (Integer) getInt.invoke(SystemProperties, params) == 1; 
        } catch (Exception e) { 
            return false; 
        } 
    }


    public static void setStatusBarTransparent(Context context, final boolean isTransparent) {
        Activity activity = PlayerUtils.scanForActivity(context);
        if (activity == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    return insets.replaceSystemWindowInsets(
                            insets.getSystemWindowInsetLeft(),
                            isTransparent ? 0 : insets.getSystemWindowInsetTop(),
                            insets.getSystemWindowInsetRight(),
                            insets.getSystemWindowInsetBottom());
                }
            });
            requestApplyInsets(decorView);
            if (isTransparent) {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
                setAndroidPCutout(context, true);
            } else {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.dkplayer_theme_color));
                setAndroidPCutout(context, false);
            }
        }
    }

    public static void setAndroidPCutout(Context context, boolean isInsertCutout) {
        Activity activity = PlayerUtils.scanForActivity(context);
        if (activity == null) return;
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (isInsertCutout) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        } else {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        }
        activity.getWindow().setAttributes(lp);

    }

    public static void requestApplyInsets(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 20) {
            view.requestApplyInsets();
        } else if (Build.VERSION.SDK_INT >= 16) {
            view.requestFitSystemWindows();
        }
    }
}