package com.dueeeke.dkplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.api.ApiActivity;
import com.dueeeke.dkplayer.activity.extend.ExtendActivity;
import com.dueeeke.dkplayer.activity.list.ListActivity;
import com.dueeeke.dkplayer.activity.api.PlayerActivity;
import com.dueeeke.dkplayer.activity.pip.PIPDemoActivity;
import com.dueeeke.videoplayer.player.VideoCacheManager;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private boolean isLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et);

        ((RadioGroup) findViewById(R.id.rg)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.vod:
                    isLive = false;
                    break;
                case R.id.live:
                    isLive = true;
                    break;
            }
        });
        //用于将视频缓存到SDCard，如不授权将缓存到/data/data目录
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close_float_window:
                PIPManager.getInstance().stopFloatWindow();
                PIPManager.getInstance().reset();
                break;
            case R.id.clear_cache:
                if (VideoCacheManager.clearAllCache(this)) {
                    Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playOther(View view) {
        String url = editText.getText().toString();
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", isLive);
        startActivity(intent);
    }

    public void clearUrl(View view) {
        editText.setText("");
    }

    public void api(View view) {
        startActivity(new Intent(this, ApiActivity.class));
    }

    public void extend(View view) {
        startActivity(new Intent(this, ExtendActivity.class));
    }

    public void list(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void pip(View view) {
        startActivity(new Intent(this, PIPDemoActivity.class));
    }
}
