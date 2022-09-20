package xyz.doikki.videoplayer.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

class Utils {

    public static Bitmap createShotBitmap(Context context, int width, int height, boolean highQuality) {
        Bitmap.Config config = highQuality ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        if (Build.VERSION.SDK_INT >= 17) {
            return Bitmap.createBitmap(context.getResources().getDisplayMetrics(),
                    width, height, config);
        } else {
            return Bitmap.createBitmap(width, height, config);
        }
    }

    public static Bitmap createShotBitmap(Render render, boolean highQuality) {
        View view = render.getView();
        return createShotBitmap(view.getContext(), view.getWidth(), view.getHeight(), highQuality);
    }

}
