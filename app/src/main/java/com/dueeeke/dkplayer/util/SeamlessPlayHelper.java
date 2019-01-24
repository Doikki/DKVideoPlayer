package com.dueeeke.dkplayer.util;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.app.MyApplication;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 无缝播放
 */

public class SeamlessPlayHelper {

    private IjkVideoView mIjkVideoView;
    private static SeamlessPlayHelper instance;

    private SeamlessPlayHelper() {
        mIjkVideoView = new IjkVideoView(MyApplication.getInstance());
        mIjkVideoView.setId(R.id.video_player);
    }

    public static SeamlessPlayHelper getInstance() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new SeamlessPlayHelper();
                }
            }
        }
        return instance;
    }


    public IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }




}
