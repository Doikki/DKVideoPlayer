package com.devlin_n.magic_player;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 管理悬浮窗
 * Created by Devlin_n on 2017/4/11.
 */

public class MyWindowManager {

    private static WindowManager mWindowManager;
    public static View floatView;
    public static IjkMediaPlayer ijkMediaPlayer;


    public static WindowManager getWindowManager(Context context){
        if(mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void cleanManager(){
        mWindowManager.removeView(floatView);
        ijkMediaPlayer.release();
        floatView = null;
        ijkMediaPlayer = null;
    }

}
