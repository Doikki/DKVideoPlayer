package xyz.doikki.videoplayer;

import java.util.Collection;

@Deprecated
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
            case DKVideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case DKVideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case DKVideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case DKVideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case DKVideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case DKVideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case DKVideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case DKVideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case DKVideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String screenMode2str(@DKVideoView.ScreenMode int mode) {
        String playerStateString;
        switch (mode) {
            default:
            case DKVideoView.SCREEN_MODE_NORMAL:
                playerStateString = "normal";
                break;
            case DKVideoView.SCREEN_MODE_FULL:
                playerStateString = "full screen";
                break;
            case DKVideoView.SCREEN_MODE_TINY:
                playerStateString = "tiny screen";
                break;
        }
        return String.format("screenMode: %s", playerStateString);
    }
}
