package com.dueeeke.dkplayer.activity.extend;

import android.os.Handler;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.DanmukuVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 弹幕播放
 * Created by devlin on 17-6-11.
 */

public class DanmakuActivity extends BaseActivity<DanmukuVideoView> {

    private static final String URL_VOD = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4";
    //    private static final String URL_VOD = "http://uploads.cutv.com:8088/video/data/201703/10/encode_file/515b6a95601ba6b39620358f2677a17358c2472411d53.mp4";

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
        mVideoView.setVideoController(controller);
        mVideoView.setUrl(URL_VOD);
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
        mVideoView.showDanMu();
    }

    public void hideDanMu(View view) {
        mVideoView.hideDanMu();
    }

    public void addDanmakuWithDrawable(View view) {
        mVideoView.addDanmakuWithDrawable();
    }

    public void addDanmaku(View view) {
        mVideoView.addDanmaku("这是一条文字弹幕~", true);
    }


    private Handler mHandler = new Handler();

    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.addDanmaku("鸡你太美", false);
                mHandler.postDelayed(this, 100);
            }
        });
    }
}
