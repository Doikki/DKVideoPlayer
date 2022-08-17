package xyz.doikki.dkplayer.widget.render.gl;

import android.content.Context;

import xyz.doikki.videoplayer.render.IRenderView;
import xyz.doikki.videoplayer.render.RenderViewFactory;

public class GLSurfaceRenderViewFactory extends RenderViewFactory {

    public static GLSurfaceRenderViewFactory create() {
        return new GLSurfaceRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new GLSurfaceRenderView(context);
    }
}
