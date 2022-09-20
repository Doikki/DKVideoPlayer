package xyz.doikki.dkplayer.widget.render;

import android.content.Context;

import xyz.doikki.videoplayer.render.Render;
import xyz.doikki.videoplayer.render.RenderFactory;
import xyz.doikki.videoplayer.render.TextureViewRender;

public class TikTokRenderViewFactory implements RenderFactory {

    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public Render create(Context context) {
        return new TikTokRenderView(new TextureViewRender(context));
    }
}
