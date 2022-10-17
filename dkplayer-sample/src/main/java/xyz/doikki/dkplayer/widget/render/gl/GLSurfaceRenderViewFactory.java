package xyz.doikki.dkplayer.widget.render.gl;

import android.content.Context;

import xyz.doikki.videoplayer.render.Render;
import xyz.doikki.videoplayer.render.RenderFactory;

public class GLSurfaceRenderViewFactory implements RenderFactory {

    public static GLSurfaceRenderViewFactory create() {
        return new GLSurfaceRenderViewFactory();
    }

    @Override
    public Render create(Context context) {
        return new GLSurfaceRenderView(context);
    }
}
