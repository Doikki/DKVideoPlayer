package com.dueeeke.dkplayer.activity.extend;

import android.content.Intent;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;

/**
 * 基于VideoView扩展的功能
 * Created by xinyu on 2018/1/3.
 */

public class ExtendActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.acitivity_extend;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_extend;
    }

    public void startFullScreen(View view) {
        startActivity(new Intent(this, FullScreenActivity.class));
    }

    public void danmaku(View view) {
        startActivity(new Intent(this, DanmakuActivity.class));
    }

    public void ad(View view) {
        startActivity(new Intent(this, ADActivity.class));
    }

    public void rotateInFullscreen(View view) {
        startActivity(new Intent(this, RotateInFullscreenActivity.class));
    }

    public void switchPlayer(View view) {
        startActivity(new Intent(this, SwitchPlayerActivity.class));
    }

    public void cache(View view) {
        startActivity(new Intent(this, CacheActivity.class));
    }

    public void playList(View view) {
        startActivity(new Intent(this, PlayListActivity.class));
    }

    public void pad(View view) {
        startActivity(new Intent(this, PadActivity.class));
    }

    public void customPlayer(View view) {
        startActivity(new Intent(this, CustomPlayerActivity.class));
    }
}
