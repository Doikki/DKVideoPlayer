package xyz.doikki.videoplayer;

import java.util.Collection;

import xyz.doikki.videoplayer.render.ScreenMode;

public class Utils {

    /**
     * 判断集合是否为null或者空
     *
     * @param collection
     * @param <E>
     * @return
     */
    public static <E> boolean isNullOrEmpty(Collection<E> collection) {
        return collection == null || collection.size() == 0;
    }


    /**
     * Returns a string containing player state debugging information.
     */
    public static String playState2str(int state) {
        String playStateString;
        switch (state) {
            default:
            case VideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case VideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String screenMode2str(@ScreenMode int mode) {
        String playerStateString;
        switch (mode) {
            default:
            case ScreenMode.NORMAL:
                playerStateString = "normal";
                break;
            case ScreenMode.FULL:
                playerStateString = "full screen";
                break;
            case ScreenMode.TINY:
                playerStateString = "tiny screen";
                break;
        }
        return String.format("screenMode: %s", playerStateString);
    }
}
