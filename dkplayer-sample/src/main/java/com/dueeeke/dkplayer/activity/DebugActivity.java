package com.dueeeke.dkplayer.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dueeeke.dkplayer.util.DebugTextViewHelper;

/**
 * 监控相关代码封装
 */
@SuppressLint("Registered")
public class DebugActivity extends BaseActivity {

    private TextView mDebugInfo;
    private DebugTextViewHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDebugInfo = new TextView(this);
        mDebugInfo.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        mDebugInfo.setBackgroundResource(android.R.color.black);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mDebugInfo.setLayoutParams(params);
        addContentView(mDebugInfo, mDebugInfo.getLayoutParams());
        mHelper = new DebugTextViewHelper(mVideoView, mDebugInfo);
        mHelper.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            removeViewFromParent(mDebugInfo);
            addContentView(mDebugInfo, mDebugInfo.getLayoutParams());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeViewFromParent(mDebugInfo);
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(mDebugInfo);
        }
    }

    private void removeViewFromParent(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.stop();
        mHelper = null;
    }
}
