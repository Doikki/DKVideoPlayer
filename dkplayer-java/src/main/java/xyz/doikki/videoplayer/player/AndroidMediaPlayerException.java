package xyz.doikki.videoplayer.player;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;

public class AndroidMediaPlayerException extends RuntimeException {

    private int what = MediaPlayer.MEDIA_ERROR_UNKNOWN;
    private int extra = MediaPlayer.MEDIA_ERROR_UNKNOWN;

    @SuppressLint("DefaultLocale")
    public AndroidMediaPlayerException(int what, int extra) {
        super(String.format("what=%d,extra=%d", what, extra));
        this.what = what;
        this.extra = extra;
    }

    public AndroidMediaPlayerException(Throwable cause) {
        super(cause);
    }

    public int getWhat() {
        return what;
    }

    public int getExtra() {
        return extra;
    }


}
