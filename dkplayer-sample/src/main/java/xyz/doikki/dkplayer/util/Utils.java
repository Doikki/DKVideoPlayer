package xyz.doikki.dkplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

import java.lang.reflect.Field;

public final class Utils {

    private Utils() {
    }


    /**
     * 获取当前的播放核心
     */
    public static Object getCurrentPlayerFactory() {
        VideoViewConfig config = VideoViewManager.getConfig();
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) return;
        ViewParent parent = v.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(v);
        }
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
    public static String playerState2str(int state) {
        String playerStateString;
        switch (state) {
            default:
            case VideoView.PLAYER_NORMAL:
                playerStateString = "normal";
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                playerStateString = "full screen";
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                playerStateString = "tiny screen";
                break;
        }
        return String.format("playerState: %s", playerStateString);
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param context    Context
     * @param contentUri The content:// URI to find the file path from
     * @return the file path as a string
     */
    public static String getFileFromContentUri(Context context, Uri contentUri) {
        if (contentUri == null) {
            return null;
        }
        if (ContentResolver.SCHEME_FILE.equals(contentUri.getScheme())) {
            return contentUri.toString();
        }
        String filePath = null;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            cursor.close();
        }
        return filePath;
    }

}
