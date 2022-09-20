package xyz.doikki.videoplayer.controller;

public interface AudioFocusController {

    /**
     * 请求音频焦点
     */
    void requestAudioFocus();

    /**
     * 放弃音频焦点
     */
    void abandonAudioFocus();

}
