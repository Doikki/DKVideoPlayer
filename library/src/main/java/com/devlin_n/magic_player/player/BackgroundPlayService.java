package com.devlin_n.magic_player.player;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.controller.FloatController;
import com.devlin_n.magic_player.util.KeyUtil;
import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */

public class BackgroundPlayService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private MagicVideoView videoView;
    private String url;
    private View floatView;
    private int position;
    private Intent intent;
    private boolean isCache;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        url = intent.getStringExtra(KeyUtil.URL);
        position = intent.getIntExtra(KeyUtil.POSITION, 0);
        isCache = intent.getBooleanExtra(KeyUtil.ENABLE_CACHE, false);
        startPlay();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();
    }

    private void startPlay() {
        if (isCache) videoView.enableCache();
        videoView.skipPositionWhenPlay(url, position).setVideoController(new FloatController(getApplicationContext())).start();
        wm.addView(floatView, wmParams);
    }


    private void initWindow() {
        wm = WindowUtil.getWindowManager(getApplicationContext());
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = WindowUtil.dip2px(getApplicationContext(), 250);
        wmParams.width = width;
        wmParams.height = width * 9 / 16;
        wmParams.x = WindowUtil.getScreenWidth(getApplicationContext()) - width;
        wmParams.y = WindowUtil.getScreenHeight(getApplicationContext(), false) / 2;
        floatView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window, null);
        videoView = (MagicVideoView) floatView.findViewById(R.id.video_view);
        floatView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stopService(intent);
            }
        });
        floatView.setOnTouchListener(new View.OnTouchListener() {
            private int floatX;
            private int floatY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        floatX = (int) event.getX();
                        floatY = (int) (event.getY() + WindowUtil.getStatusBarHeight(getApplicationContext()));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_MOVE:
                        wmParams.x = X - floatX;
                        wmParams.y = Y - floatY;
                        wm.updateViewLayout(floatView, wmParams);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) wm.removeView(floatView);
        videoView.release();
    }
}
