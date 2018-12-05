package com.dueeeke.videocontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;


public class BatteryReceiver extends BroadcastReceiver {
    private ImageView pow;

    public BatteryReceiver(ImageView pow) {
        this.pow = pow;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        int current = extras.getInt("level");// 获得当前电量
        int total = extras.getInt("scale");// 获得总电量
        int percent = current * 100 / total;
        pow.getDrawable().setLevel(percent);
    }
}