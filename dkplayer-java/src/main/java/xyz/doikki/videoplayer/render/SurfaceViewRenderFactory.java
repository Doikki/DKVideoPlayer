package xyz.doikki.videoplayer.render;

import android.content.Context;

public class SurfaceViewRenderFactory implements RenderFactory {

    public static SurfaceViewRenderFactory create() {
        return new SurfaceViewRenderFactory();
    }

    @Override
    public Render create(Context context) {
        return new SurfaceViewRender(context);
    }
}
