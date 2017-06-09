package com.devlin_n.yin_yang_player.util;

import android.util.Log;

/**
 * 日志类
 * Created by Devlin_n on 2017/6/5.
 */

public class L {

    private static final String TAG = "MagicPlayer";


    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
