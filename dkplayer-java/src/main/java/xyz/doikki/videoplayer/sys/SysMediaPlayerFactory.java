package xyz.doikki.videoplayer.sys;

import android.content.Context;

import xyz.doikki.videoplayer.MediaPlayerFactory;

/**
 * 创建{@link SysMediaPlayer}的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 */
public class SysMediaPlayerFactory extends MediaPlayerFactory<SysMediaPlayer> {

    public static SysMediaPlayerFactory create() {
        return new SysMediaPlayerFactory();
    }

    @Override
    public SysMediaPlayer create(Context context) {
        return new SysMediaPlayer(context);
    }
}
