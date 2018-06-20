package com.dueeeke.dkplayer.activity.pip;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;

public class PIPDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip_demo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public void pip(View view) {
        startActivity(new Intent(this, PIPActivity.class));
    }

    public void pipInList(View view) {
        startActivity(new Intent(this, PIPListActivity.class));
    }

    public void pipAndroidO(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivity(new Intent(this, AndroidOPiPActivity.class));
        } else {
            Toast.makeText(this, "Only support Android O ~", Toast.LENGTH_SHORT).show();
        }
    }
}
