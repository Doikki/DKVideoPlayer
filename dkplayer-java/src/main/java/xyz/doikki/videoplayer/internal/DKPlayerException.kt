package xyz.doikki.videoplayer.internal

import android.media.MediaPlayer
import android.annotation.SuppressLint
import java.lang.RuntimeException

class DKPlayerException : RuntimeException {
    var what = MediaPlayer.MEDIA_ERROR_UNKNOWN
        private set
    var extra = MediaPlayer.MEDIA_ERROR_UNKNOWN
        private set

    @SuppressLint("DefaultLocale")
    constructor(what: Int, extra: Int) : super(String.format("what=%d,extra=%d", what, extra)) {
        this.what = what
        this.extra = extra
    }

    constructor(cause: Throwable?) : super(cause) {}
}