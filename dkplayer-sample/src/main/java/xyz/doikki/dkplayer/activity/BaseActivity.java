package xyz.doikki.dkplayer.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import xyz.doikki.dkplayer.R;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.VideoViewManager;

/**
 * 页面以及播放器共有逻辑封装
 * @param <T>
 */
@SuppressLint("Registered")
public class BaseActivity<T extends VideoView> extends AppCompatActivity {

    protected T mVideoView;

    protected int getTitleResId() {
        return R.string.app_name;
    }

    protected int getLayoutResId() {
        return 0;
    }

    protected View getContentView() {
        return null;
    }

    protected boolean enableBack() {
        return true;
    }

    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId());
        } else if (getContentView() != null) {
            setContentView(getContentView());
        }

        //标题栏设置
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitleResId());
            if (enableBack()) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        initView();

    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void initView() {

    }

    /**
     * 把状态栏设成透明
     */
    protected void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoView == null || !mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
