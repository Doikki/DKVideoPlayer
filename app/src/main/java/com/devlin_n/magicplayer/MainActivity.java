package com.devlin_n.magicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.devlin_n.magic_player.player.BackgroundPlayService;
import com.devlin_n.magic_player.player.VideoCacheManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于将视频缓存到SDCard，如不授权将缓存到/data/data目录
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
    }

    public void skipToVodPlayer(View view) {
        startActivity(new Intent(this, VodPlayerActivity.class));
    }

    public void skipToLivePlayer(View view) {
        startActivity(new Intent(this, LivePlayerActivity.class));
    }

    public void closeFloatWindow(View view) {
        Intent intent = new Intent(this, BackgroundPlayService.class);
        getApplicationContext().stopService(intent);
    }

    public void startFullScreen(View view) {
        startActivity(new Intent(this, FullScreenActivity.class));
    }

    public void clearCache(View view) {
        if (VideoCacheManager.clearAllCache(this)) {
            Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
        }
    }
}
