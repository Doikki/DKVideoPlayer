package com.dueeeke.dkplayer.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dueeeke.dkplayer.util.DebugTextViewHelper;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 监控相关代码封装
 */
public abstract class BaseActivity extends AppCompatActivity {

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
        addContentView(mDebugInfo, params);

        mHelper = new DebugTextViewHelper(getVideoView(), mDebugInfo);
        mHelper.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDebugInfo.bringToFront();
    }

    protected abstract VideoView getVideoView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.stop();
        mHelper = null;
    }
}
