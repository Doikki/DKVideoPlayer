package xyz.doikki.dkplayer.activity.pip;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.PIPManager;
import xyz.doikki.dkplayer.util.Tag;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.VideoView;

public class PIPActivity extends BaseActivity {

    private PIPManager mPIPManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip_demo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FrameLayout playerContainer = findViewById(R.id.player_container);
        mPIPManager = PIPManager.getInstance();
        VideoView videoView = getVideoViewManager().get(Tag.PIP);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent(getString(R.string.str_pip), false);
        videoView.setVideoController(controller);
        if (mPIPManager.isStartFloatWindow()) {
            mPIPManager.stopFloatWindow();
            controller.setScreenMode(videoView.getScreenMode());
            controller.setPlayerState(videoView.getPlayerState());
        } else {
            mPIPManager.setActClass(PIPActivity.class);
            ImageView thumb = controller.findViewById(R.id.thumb);
            Glide.with(this)
                    .load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg")
                    .placeholder(android.R.color.darker_gray)
                    .into(thumb);
            videoView.setDataSource(DataUtil.SAMPLE_URL);
        }
        playerContainer.addView(videoView);
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
        mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPIPManager.reset();
    }


    @Override
    public void onBackPressed() {
        if (mPIPManager.onBackPress()) return;
        super.onBackPressed();
    }

    public void startFloatWindow(View view) {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 10000);
        } else {
            mPIPManager.startFloatWindow();
            mPIPManager.resume();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 10000 && Settings.canDrawOverlays(this)) {
                mPIPManager.startFloatWindow();
                mPIPManager.resume();
                finish();
            }
        }
    }
}
