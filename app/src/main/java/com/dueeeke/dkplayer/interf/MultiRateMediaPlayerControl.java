package com.dueeeke.dkplayer.interf;

import com.dueeeke.videoplayer.listener.MediaPlayerControl;

import java.util.LinkedHashMap;

/**
 * Created by xinyu on 2017/12/25.
 */

public interface MultiRateMediaPlayerControl extends MediaPlayerControl {
    LinkedHashMap<String, String> getMultiRateData();
    void switchRate(String type);
}
