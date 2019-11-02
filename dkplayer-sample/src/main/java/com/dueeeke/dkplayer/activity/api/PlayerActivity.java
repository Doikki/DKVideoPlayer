package com.dueeeke.dkplayer.activity.api;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.DebugActivity;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.LiveControlView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class PlayerActivity extends DebugActivity {

    private static final String THUMB = "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);

        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            //根据屏幕方向自动进入/退出全屏
            controller.setEnableOrientation(true);

            PrepareView prepareView = new PrepareView(this);//准备播放界面
            ImageView thumb = prepareView.findViewById(R.id.thumb);//封面图
            Glide.with(this).load(THUMB).into(thumb);
            controller.addControlComponent(prepareView);

            controller.addControlComponent(new CompleteView(this));//自动完成播放界面

            controller.addControlComponent(new ErrorView(this));//错误界面

            TitleView titleView = new TitleView(this);//标题栏
            controller.addControlComponent(titleView);

            //根据是否为直播设置不同的底部控制条
            boolean isLive = intent.getBooleanExtra("isLive", false);
            if (isLive) {
                controller.addControlComponent(new LiveControlView(this));//直播控制条
                controller.setLiveMode(true);
            } else {
                controller.addControlComponent(new VodControlView(this));//点播控制条
                controller.setLiveMode(false);
            }

            //设置标题
            String title = intent.getStringExtra(IntentKeys.TITLE);
            titleView.setTitle(title);

            mVideoView.setVideoController(controller);

            mVideoView.setUrl(intent.getStringExtra("url"));

            //保存播放进度
//            mVideoView.setProgressManager(new ProgressManagerImpl());
            //播放状态监听
//            mVideoView.addOnVideoViewStateChangeListener(mOnVideoViewStateChangeListener);

            //使用IjkPlayer解码
//            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
            //使用ExoPlayer解码
//            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
            //使用MediaPlayer解码
//            mVideoView.setPlayerFactory(AndroidMediaPlayerFactory.create());

            mVideoView.start();
        }
    }

    private OnVideoViewStateChangeListener mOnVideoViewStateChangeListener = new OnVideoViewStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case VideoView.PLAYER_NORMAL://小屏
                    break;
                case VideoView.PLAYER_FULL_SCREEN://全屏
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case VideoView.STATE_IDLE:
                    break;
                case VideoView.STATE_PREPARING:
                    break;
                case VideoView.STATE_PREPARED:
                    //需在此时获取视频宽高
                    int[] videoSize = mVideoView.getVideoSize();
                    L.d("视频宽：" + videoSize[0]);
                    L.d("视频高：" + videoSize[1]);
                    break;
                case VideoView.STATE_PLAYING:
                    break;
                case VideoView.STATE_PAUSED:
                    break;
                case VideoView.STATE_BUFFERING:
                    break;
                case VideoView.STATE_BUFFERED:
                    break;
                case VideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case VideoView.STATE_ERROR:
                    break;
            }
        }
    };

    private int i = 0;

    public void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.scale_default:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
                break;
            case R.id.scale_169:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                break;
            case R.id.scale_43:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_4_3);
                break;
            case R.id.scale_original:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_ORIGINAL);
                break;
            case R.id.scale_match_parent:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
                break;
            case R.id.scale_center_crop:
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
                break;

            case R.id.speed_0_5:
                mVideoView.setSpeed(0.5f);
                break;
            case R.id.speed_0_75:
                mVideoView.setSpeed(0.75f);
                break;
            case R.id.speed_1_0:
                mVideoView.setSpeed(1.0f);
                break;
            case R.id.speed_1_5:
                mVideoView.setSpeed(1.5f);
                break;
            case R.id.speed_2_0:
                mVideoView.setSpeed(2.0f);
                break;

            case R.id.screen_shot:
                ImageView imageView = findViewById(R.id.iv_screen_shot);
                Bitmap bitmap = mVideoView.doScreenShot();
                imageView.setImageBitmap(bitmap);
                break;

            case R.id.mirror_rotate:
                mVideoView.setMirrorRotation(i % 2 == 0);
                i++;
                break;
        }
    }
}
