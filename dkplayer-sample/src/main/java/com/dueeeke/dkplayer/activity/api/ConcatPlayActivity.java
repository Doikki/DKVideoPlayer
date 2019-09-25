package com.dueeeke.dkplayer.activity.api;

import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.ijk.IjkPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * rtsp/concat，注意：本demo只针对ijkplayer
 */

public class ConcatPlayActivity extends BaseActivity<VideoView<IjkPlayer>> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_concat_play;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_rtsp_concat;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);
        StandardVideoController controller = new StandardVideoController(this);
        mVideoView.setVideoController(controller);
        mVideoView.setPlayerFactory(new MyPlayerFactory());
    }

    class MyPlayerFactory extends PlayerFactory<IjkPlayer> {

        @Override
        public IjkPlayer createPlayer() {
            return new IjkPlayer() {
                @Override
                public void setOptions() {
                    super.setOptions();
                    //支持concat
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist",
                            "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto,rtsp");
                    //使用tcp方式拉取rtsp流，默认是通过udp方式
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
                }
            };
        }
    }


    public void onButtonClick(View view) {
        mVideoView.release();
        switch (view.getId()) {
            case R.id.concat:
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
            case R.id.rtsp:
                String rtspUrl = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
                mVideoView.setUrl(rtspUrl);
                break;
        }

        mVideoView.start();
    }

    class ConcatMedia {
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
