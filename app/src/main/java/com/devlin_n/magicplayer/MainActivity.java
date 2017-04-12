package com.devlin_n.magicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void skipToVodPlayer(View view) {
        startActivity(new Intent(this, VodPlayerActivity.class));
    }

    public void skipToLivePlayer(View view) {
        startActivity(new Intent(this, LivePlayerActivity.class));
    }
}
