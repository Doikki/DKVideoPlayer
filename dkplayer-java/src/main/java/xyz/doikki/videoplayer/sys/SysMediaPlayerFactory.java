package xyz.doikki.videoplayer.sys;

import android.content.Context;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.DKPlayerFactory;

/**
 * 创建{@link SysMediaPlayer}的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 */
public class SysMediaPlayerFactory implements DKPlayerFactory<SysMediaPlayer> {

    public static SysMediaPlayerFactory create() {
        return new SysMediaPlayerFactory();
    }

    @NonNull
    @Override
    public SysMediaPlayer create(@NonNull Context context) {
        return new SysMediaPlayer(context);
    }

}
