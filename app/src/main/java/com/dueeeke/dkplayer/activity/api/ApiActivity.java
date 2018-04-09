package com.dueeeke.dkplayer.activity.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dueeeke.dkplayer.R;

import java.io.File;

/**
 * 基础API相关Demo
 * Created by xinyu on 2018/1/3.
 */

public class ApiActivity extends AppCompatActivity {

    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    //断线自动重连,需加上ijkhttphook:
//    private static final String VOD_URL = "ijkhttphook:http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private static final String LIVE_URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    //断线自动重连,需加上ijklivehook:
//    private static final String LIVE_URL = "ijklivehook:rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
    }

    public void skipToVodPlayer(View view) {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String url = "file://" + absolutePath + File.separator + "test.ffconcat";
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", false);
        startActivity(intent);
    }

    public void skipToLivePlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", LIVE_URL);
        intent.putExtra("isLive", true);
        startActivity(intent);
    }
}
