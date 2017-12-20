package com.devlin_n.videoplayer.listener;

/**
 * Created by xinyu on 2017/12/21.
 */

public interface IjkPlayerInterface {

    void onError();

    void onCompletion();

    void onInfo(int what, int extra);

    void onBufferingUpdate(int position);

    void onPrepared();

    void onVideoSizeChanged(int width, int height);

}
