package com.devlin_n.yyplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlin_n.library.FloatWindowManager;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yyplayer.R;

/**
 * 直播播放
 * Created by Devlin_n on 2017/4/7.
 */

public class LivePlayerActivity extends AppCompatActivity {

    private YinYangPlayer yinYangPlayer;
    private static final String URL = "http://ivi.bupt.edu.cn/hls/hunanhd.m3u8";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIVE");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        yinYangPlayer = (YinYangPlayer) findViewById(R.id.player);
//        int widthPixels = getResources().getDisplayMetrics().widthPixels;
//        yinYangPlayer.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 4 * 3));

        StandardVideoController controller = new StandardVideoController(this);
        controller.setLive(true);
        Glide.with(this)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=" +
                        "1497428393984&di=0b8c95c1ae8f9427335f9cf73bfe692c&imgtype=0&src=http%" +
                        "3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Frecom%2F2017%2F01%2F08%2F148380896090867654.JPEG")
                .asBitmap()
                .animate(R.anim.anim_alpha_in)
                .placeholder(android.R.color.darker_gray)
                .into(controller.getThumb());
        yinYangPlayer
                .autoRotate()
//                .useAndroidMediaPlayer()
                .setUrl(URL)
                .setTitle("湖南卫视")
                .setVideoController(controller);
//                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        yinYangPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        yinYangPlayer.resume();
        yinYangPlayer.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        yinYangPlayer.release();
    }


    @Override
    public void onBackPressed() {
        if (!yinYangPlayer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                yinYangPlayer.startFloatWindow();
            } else {
                Toast.makeText(LivePlayerActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
