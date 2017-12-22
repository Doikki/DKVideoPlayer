package com.dueeeke.videoplayer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.dueeeke.videoplayer.R;

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
        if (percent < 15) {
            pow.setImageResource(R.drawable.ic_action_battery_10);
        } else if (percent < 25) {
            pow.setImageResource(R.drawable.ic_action_battery_20);
        } else if (percent < 35) {
            pow.setImageResource(R.drawable.ic_action_battery_30);
        } else if (percent < 45) {
            pow.setImageResource(R.drawable.ic_action_battery_40);
        } else if (percent < 55) {
            pow.setImageResource(R.drawable.ic_action_battery_50);
        } else if (percent < 65) {
            pow.setImageResource(R.drawable.ic_action_battery_60);
        } else if (percent < 75) {
            pow.setImageResource(R.drawable.ic_action_battery_70);
        } else if (percent < 85) {
            pow.setImageResource(R.drawable.ic_action_battery_80);
        } else if (percent < 95) {
            pow.setImageResource(R.drawable.ic_action_battery_90);
        } else {
            pow.setImageResource(R.drawable.ic_action_battery);
        }

    }
}