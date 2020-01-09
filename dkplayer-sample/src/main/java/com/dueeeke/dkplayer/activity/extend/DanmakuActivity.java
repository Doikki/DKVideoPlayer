package com.dueeeke.dkplayer.activity.extend;

import android.os.Handler;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.widget.component.MyDanmakuView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 弹幕播放
 * Created by dueeeke on 17-6-11.
 */

public class DanmakuActivity extends BaseActivity<VideoView> {

    private MyDanmakuView mMyDanmakuView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_danmaku_player;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_danmu;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent(getString(R.string.str_danmu), false);
        mMyDanmakuView = new MyDanmakuView(this);
        controller.addControlComponent(mMyDanmakuView);
        mVideoView.setVideoController(controller);
        mVideoView.setUrl(DataUtil.SAMPLE_URL);
        mVideoView.start();

        mVideoView.addOnStateChangeListener(new VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PREPARED) {
                    simulateDanmu();
                } else if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void showDanMu(View view) {
        mMyDanmakuView.show();
    }

    public void hideDanMu(View view) {
        mMyDanmakuView.hide();
    }

    public void addDanmakuWithDrawable(View view) {
        mMyDanmakuView.addDanmakuWithDrawable();
    }

    public void addDanmaku(View view) {
        mMyDanmakuView.addDanmaku("这是一条文字弹幕~", true);
    }


    private Handler mHandler = new Handler();

    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMyDanmakuView.addDanmaku("awsl", false);
                mHandler.postDelayed(this, 100);
            }
        });
    }
}
