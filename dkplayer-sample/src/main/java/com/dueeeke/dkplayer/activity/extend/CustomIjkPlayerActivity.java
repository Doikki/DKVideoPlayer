package com.dueeeke.dkplayer.activity.extend;

import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.IjkVideoView;
import com.dueeeke.videocontroller.StandardVideoController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的IjkVideoView
 */
public class CustomIjkPlayerActivity extends BaseActivity<IjkVideoView> implements View.OnClickListener {

    @Override
    protected int getTitleResId() {
        return super.getTitleResId();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_custom_ijk_player;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);
        findViewById(R.id.btn_ffconcat).setOnClickListener(this);
        findViewById(R.id.btn_rtsp).setOnClickListener(this);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent("custom ijk", false);
//        controller.addControlComponent(new PlayerMonitor());
        mVideoView.setVideoController(controller);
//        mVideoView.setEnableMediaCodec(true);
//        mVideoView.setEnableAccurateSeek(true);
//        mVideoView.skipPositionWhenPlay(10000);
//        mVideoView.setProgressManager(new ProgressManagerImpl());
    }

    @Override
    public void onClick(View v) {
        mVideoView.release();
        switch (v.getId()) {
            case R.id.btn_ffconcat:
                //支持concat
                mVideoView.addFormatOption("safe", "0");
                mVideoView.addFormatOption("protocol_whitelist",
                        "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
                File cacheDir = getExternalCacheDir();
                File concat = new File(cacheDir, "playlist.ffconcat");
                if (concat.exists()) {
                    concat.delete();
                }
                //注意：播放文件内容一定要按照如下格式编写，详见 https://ffmpeg.org/ffmpeg-formats.html#concat
                try {
                    FileWriter writer = new FileWriter(concat);
                    //ffconcat版本
                    writer.write("ffconcat version 1.0");
                    writer.write("\r\n");

                    for (ConcatMedia m : getConcatData()) {
                        //地址
                        writer.write("file '" + m.url + "'");
                        writer.write("\r\n");
                        //时长
                        writer.write("duration " + m.duration);
                        writer.write("\r\n");
                    }

                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String concatUrl = "file://" + concat.getAbsolutePath();
                mVideoView.setUrl(concatUrl);
                break;
            case R.id.btn_rtsp:
                //使用tcp方式拉取rtsp流，默认是通过udp方式
//                mVideoView.addFormatOption("rtsp_transport", "tcp");
//                mVideoView.addFormatOption("protocol_whitelist",
//                        "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");

//                mVideoView.addFormatOption("analyzemaxduration", 100L);
//                mVideoView.addFormatOption("analyzeduration", 1);
//                mVideoView.addFormatOption("probesize", 1024L);
//                mVideoView.addFormatOption("flush_packets", 1L);
//                mVideoView.addPlayerOption("packet-buffering", 0L);
//                mVideoView.addPlayerOption("framedrop", 1L);
                String rtspUrl = "rtsp://192.168.31.246:8554/test";
                mVideoView.setUrl(rtspUrl);
                break;
        }
        mVideoView.start();
    }

    static class ConcatMedia {
        public ConcatMedia(String url, long duration) {
            this.url = url;
            this.duration = duration;
        }

        public String url;
        public long duration;
    }


    private List<ConcatMedia> getConcatData() {
        List<ConcatMedia> medias = new ArrayList<>();
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4", 31));
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4", 100));
        medias.add(new ConcatMedia("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4", 60));
        return medias;
    }
}
