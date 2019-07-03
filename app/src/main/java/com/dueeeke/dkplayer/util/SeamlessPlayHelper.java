package com.dueeeke.dkplayer.util;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.app.MyApplication;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 无缝播放
 */

public class SeamlessPlayHelper {

    private VideoView mVideoView;
    private static SeamlessPlayHelper instance;

    private SeamlessPlayHelper() {
        mVideoView = new VideoView(MyApplication.getInstance());
        mVideoView.setId(R.id.video_player);
    }

    public static SeamlessPlayHelper getInstance() {
        if (instance == null) {
            synchronized (SeamlessPlayHelper.class) {
                if (instance == null) {
                    instance = new SeamlessPlayHelper();
                }
            }
        }
        return instance;
    }


    public VideoView getVideoView() {
        return mVideoView;
    }




}
