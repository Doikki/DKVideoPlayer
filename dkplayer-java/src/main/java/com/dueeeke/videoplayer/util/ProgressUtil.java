package com.dueeeke.videoplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xinyu on 2018/4/18.
 */

public class ProgressUtil {


    public static void saveProgress(Context context, String url, long progress) {
        L.d("saveProgress:" + url.hashCode() + "/" + progress);
        SharedPreferences sp = context.getSharedPreferences(PlayerConstants.DK_PROGRESS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("dkplayer_" + url.hashCode(), progress).apply();
    }

    public static long getSavedProgress(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PlayerConstants.DK_PROGRESS,
                Context.MODE_PRIVATE);
        return sp.getLong("dkplayer_" + url.hashCode(), 0);
    }

    /**
     * clear all progress
     */
    public static void clearAllSavedProgress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PlayerConstants.DK_PROGRESS,
                Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /**
     * remove progress by url
     */
    public static void clearSavedProgressByUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PlayerConstants.DK_PROGRESS,
                Context.MODE_PRIVATE);
        sp.edit().remove("dkplayer_" + url.hashCode()).apply();
    }

}
