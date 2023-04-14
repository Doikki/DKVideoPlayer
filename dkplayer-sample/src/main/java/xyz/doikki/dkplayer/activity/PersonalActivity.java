package xyz.doikki.dkplayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import xyz.doikki.dkplayer.R;

public class PersonalActivity extends AppCompatActivity {

    public static void startPersonalActivity (Context context ) {
        context.startActivity(new Intent(context,PersonalActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
    }
}