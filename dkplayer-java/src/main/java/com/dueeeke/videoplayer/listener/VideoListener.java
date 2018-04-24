package com.dueeeke.videoplayer.listener;

/**
 * Created by Devlin_n on 2017/6/22.
 */

public interface VideoListener {

    void onVideoStarted();

    void onVideoPaused();

    //播放完成
    void onComplete();

    //准备完成
    void onPrepared();

    void onError();

    void onInfo(int what, int extra);

}
