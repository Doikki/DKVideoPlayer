package xyz.doikki.dkplayer.activity.api;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.DKVideoView;

/**
 * 多开
 */
public class ParallelPlayActivity extends AppCompatActivity {

    private static final String VOD_URL_1 = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4";
    private static final String VOD_URL_2 = DataUtil.SAMPLE_URL;

    private List<DKVideoView> mVideoViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallel_play);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_multi_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        DKVideoView player1 = findViewById(R.id.player_1);
        player1.setDataSource(VOD_URL_1);

        StandardVideoController controller1 = new StandardVideoController(this);
        controller1.addDefaultControlComponent(getString(R.string.str_multi_player), false);
//        controller1.addControlComponent(new AudioFocusMonitor());
        player1.setVideoController(controller1);
        mVideoViews.add(player1);

        DKVideoView player2 = findViewById(R.id.player_2);
        player2.setDataSource(VOD_URL_2);
        StandardVideoController controller2 = new StandardVideoController(this);
        controller2.addDefaultControlComponent(getString(R.string.str_multi_player), false);
//        controller2.addControlComponent(new AudioFocusMonitor());
        player2.setVideoController(controller2);
        mVideoViews.add(player2);
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
        for (DKVideoView vv : mVideoViews) {
            vv.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (DKVideoView vv : mVideoViews) {
            vv.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (DKVideoView vv : mVideoViews) {
            vv.release();
        }
    }

    @Override
    public void onBackPressed() {
        for (DKVideoView vv : mVideoViews) {
            if (vv.onBackPressed())
                return;
        }
        super.onBackPressed();
    }
}
