package com.dueeeke.dkplayer.interf;

import com.dueeeke.dkplayer.widget.videoview.MultiRateIjkVideoView;
import com.dueeeke.videoplayer.listener.MediaPlayerControl;

import java.util.List;

/**
 * Created by xinyu on 2017/12/25.
 */

public interface MultiRateMediaPlayerControl extends MediaPlayerControl {
    List<MultiRateIjkVideoView.MultiRateVideoModel> getMultiRateData();
    void switchRate(String type);
}
