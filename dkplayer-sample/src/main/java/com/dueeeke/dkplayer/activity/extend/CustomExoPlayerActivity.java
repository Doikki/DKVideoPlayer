package com.dueeeke.dkplayer.activity.extend;

import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.ExoVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.exo.ExoMediaPlayer;
import com.dueeeke.videoplayer.exo.ExoMediaSourceHelper;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

/**
 * 自定义MediaPlayer，有多种情形：
 * 第一：集成某个现成的MediaPlayer，对其功能进行扩张，此demo就演示了通过继承{@link ExoMediaPlayer}
 * 对其功能进行扩展。
 * 第二：通过继承{@link AbstractPlayer}扩展一些其他的播放器。
 */
public class CustomExoPlayerActivity extends BaseActivity<ExoVideoView> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_custom_exo_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.vv);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent("custom exo", false);
        mVideoView.setVideoController(controller);
    }

    public void onButtonClick(View view) {
        mVideoView.release();
        switch (view.getId()) {
            case R.id.cache: {
                mVideoView.setCacheEnabled(true);
                mVideoView.setUrl("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8");
                break;
            }
            case R.id.concat: {
                mVideoView.setCacheEnabled(false);
                //将多个视频拼接在一起播放
                ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
                ExoMediaSourceHelper helper = new ExoMediaSourceHelper(this);
                MediaSource mediaSource1 = helper.getMediaSource("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
                MediaSource mediaSource2 = helper.getMediaSource("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4");
                MediaSource mediaSource3 = helper.getMediaSource("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4");
                concatenatingMediaSource.addMediaSource(mediaSource1);
                concatenatingMediaSource.addMediaSource(mediaSource2);
                concatenatingMediaSource.addMediaSource(mediaSource3);
                mVideoView.setMediaSource(concatenatingMediaSource);
                break;
            }
            case R.id.clip: {
                ExoMediaSourceHelper helper = new ExoMediaSourceHelper(this);
                MediaSource mediaSource = helper.getMediaSource("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
                //裁剪10-15秒的内容进行播放
                ClippingMediaSource clippingMediaSource = new ClippingMediaSource(mediaSource, 10_000_000, 15_000_000);
                LoopingMediaSource loopingMediaSource = new LoopingMediaSource(clippingMediaSource);
                mVideoView.setMediaSource(loopingMediaSource);
                break;
            }
        }

        mVideoView.start();
    }
}
