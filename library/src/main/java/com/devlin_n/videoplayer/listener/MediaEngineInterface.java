package com.devlin_n.videoplayer.listener;

/**
 * Created by xinyu on 2017/12/21.
 */

public interface MediaEngineInterface {

    void onError();

    void onCompletion();

    void onInfo(int what, int extra);

    void onBufferingUpdate(int percent);

    void onPrepared();

    void onVideoSizeChanged(int width, int height);

}
