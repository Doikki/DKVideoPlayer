package com.dueeeke.dkplayer.activity.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkPlayer;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class CustomMediaPlayerActivity extends AppCompatActivity {

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_media_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("rtsp&concat");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


//        String url = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
        String rtspUrl = "rtsp://ajj:12345678@218.21.217.122:65523/h264/ch40/sub/av_stream";
//        String rtspUrl = "rtsp://live.3gv.ifeng.com/live/71";
//        String url = "http://storage.gzstv.net/uploads/media/huangmeiyan/jr05-09.mp4";
        //测试concat,将项目根目录的other文件夹中的test.ffconcat文件复制到sd卡根目录测试
//        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String url = "file://" + absolutePath + File.separator + "test.ffconcat";
        //concat测试地址
        String concatUrl = "http://pl.feixiong.tv/Admin/Service/plconcat?url=http%3A%2F%2Fv.youku.com%2Fv_show%2Fid_XMjg4ODUzMDQyNA%3D%3D.html&source_id=1&sel_type=FLV&stream_type=SD";

        ijkVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        ijkVideoView.setVideoController(controller);
        RadioGroup radioGroup = findViewById(R.id.rg_type);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.concat:
                    ijkVideoView.setUrl(concatUrl);
                    break;
                case R.id.rtsp:
                    ijkVideoView.setUrl(rtspUrl);
                    break;
            }
        });
        ijkVideoView.setPlayerConfig(new PlayerConfig.Builder()
                .autoRotate()//自动旋转屏幕
//                    .enableCache()//启用边播边存
//                .enableMediaCodec()//启动硬解码
//                .usingSurfaceView()//使用SurfaceView
                .setCustomMediaPlayer(new IjkPlayer(this) {
                    @Override
                    public void setOptions() {
                        super.setOptions();
                        //支持concat
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                                "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
                        //支持rtsp
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                    }
                })
                .build());
    }


    public void startPlay(View view) {
        ijkVideoView.release();
        ijkVideoView.start();
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
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
