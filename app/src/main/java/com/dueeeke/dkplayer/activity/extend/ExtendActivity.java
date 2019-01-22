package com.dueeeke.dkplayer.activity.extend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;

/**
 * 基于IjkVideoView扩展的功能
 * Created by xinyu on 2018/1/3.
 */

public class ExtendActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_extend);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_extend);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
}
