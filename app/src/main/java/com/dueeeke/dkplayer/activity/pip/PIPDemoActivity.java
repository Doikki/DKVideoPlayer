package com.dueeeke.dkplayer.activity.pip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.api.LivePlayerActivity;

public class PIPDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_pip);
    }


    public void pip(View view) {
        startActivity(new Intent(this, LivePlayerActivity.class));
    }

    public void pipInList(View view) {
        startActivity(new Intent(this, PIPListActivity.class));
    }
}
