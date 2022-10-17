package xyz.doikki.dkplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.VideoController;

public final class Utils {

    private Utils() {
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
