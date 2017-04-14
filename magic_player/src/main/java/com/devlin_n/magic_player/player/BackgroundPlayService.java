package com.devlin_n.magic_player.player;

import android.app.Service;
import android.content.Context;
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
import com.devlin_n.magic_player.util.KeyUtil;
import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */

public class BackgroundPlayService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private IjkVideoView videoView;
    private String url;
    private View floatView;
    private int position;
    private int type;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(KeyUtil.URL);
        position = intent.getIntExtra(KeyUtil.POSITION, 0);
        type = intent.getIntExtra(KeyUtil.TYPE, 0);

        if (floatView != null && wm != null) wm.removeView(floatView);
        startPlay();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();
    }

    private void startPlay() {
        floatView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window, null);
        videoView = (IjkVideoView) floatView.findViewById(R.id.video_view);
        videoView.setVideoType(type);
        videoView.skipPositionWhenPlay(url, position);
        floatView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(floatView);
                videoView.release();
                floatView = null;
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
                        wmParams.gravity = Gravity.START | Gravity.TOP;
                        wmParams.x = X - floatX;
                        wmParams.y = Y - floatY;
                        wm.updateViewLayout(floatView, wmParams);
                        break;
                }
                return false;
            }
        });
        wm.addView(floatView, wmParams);
    }


    private void initWindow() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wmParams.gravity = Gravity.END | Gravity.BOTTOM; // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        int width = (int) (WindowUtil.getScreenWidth(getApplicationContext()) / 1.5);
        wmParams.width = width;
        wmParams.height = width * 9 / 16;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) wm.removeView(floatView);
        videoView.release();
    }
}
