package com.devlin_n.magic_player.player;

/**
 * Created by Devlin_n on 2017/4/11.
 */

public class VideoModel {

    public String url;
    public String title;

    public VideoModel(String url, String title, int type) {
        this.url = url;
        this.title = title;
        this.type = type;
    }

    public int type;
}
