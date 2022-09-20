package xyz.doikki.videoplayer.render;

import android.content.Context;

public class TextureViewRenderFactory implements RenderFactory {

    public static TextureViewRenderFactory create() {
        return new TextureViewRenderFactory();
    }

    @Override
    public Render create(Context context) {
        return new TextureViewRender(context);
    }
}
