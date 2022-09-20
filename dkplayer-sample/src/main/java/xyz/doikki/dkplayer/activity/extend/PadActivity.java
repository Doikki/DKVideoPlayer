package xyz.doikki.dkplayer.activity.extend;

import android.view.View;
import android.widget.Toast;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.videocontroller.StandardVideoController;

public class PadActivity extends BaseActivity {

    private StandardVideoController mController;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pad;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);

        mVideoView.setDataSource(DataUtil.SAMPLE_URL);

        mController = new StandardVideoController(this);
        mController.addDefaultControlComponent("pad", false);

        mController.findViewById(R.id.fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isFullScreen()) {
                    mVideoView.stopFullScreen();
                } else {
                    mVideoView.startFullScreen();
                }
            }
        });

        mController.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.stopFullScreen();
            }
        });

        mVideoView.setVideoController(mController);

        mVideoView.start();
    }


    @Override
    public void onBackPressed() {
        if (mController.isLocked()) {
            mController.show();
            Toast.makeText(this, R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
            return;
        }
        finish();
    }
}
