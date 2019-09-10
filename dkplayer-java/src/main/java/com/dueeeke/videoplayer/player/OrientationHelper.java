package com.dueeeke.videoplayer.player;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * 设备方向监听
 */
public final class OrientationHelper extends OrientationEventListener {

    private long mLastTime;

    private OnOrientationChangeListener mOnOrientationChangeListener;

    public OrientationHelper(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime < 300) return;//300毫秒检测一次
        if (mOnOrientationChangeListener != null) {
            mOnOrientationChangeListener.onOrientationChanged(orientation);
        }
        mLastTime = currentTime;
    }


    interface OnOrientationChangeListener {
        void onOrientationChanged(int orientation);
    }

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener) {
        mOnOrientationChangeListener = onOrientationChangeListener;
    }
}
