package com.devlin_n.magic_player;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 管理悬浮窗
 * Created by Devlin_n on 2017/4/11.
 */

public class MyWindowManager {

    private static WindowManager mWindowManager;
    public static List<View> floatViews = new ArrayList<>();
    public static IjkMediaPlayer ijkMediaPlayer;


    public static WindowManager getWindowManager(Context context){
        if(mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void cleanManager(){
        mWindowManager.removeView(floatViews.get(0));
        ijkMediaPlayer.release();
        floatViews.clear();
        ijkMediaPlayer = null;
    }
}
