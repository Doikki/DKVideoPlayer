package com.dueeeke.dkplayer.widget.render;

import android.content.Context;

import com.dueeeke.videoplayer.render.IRenderView;
import com.dueeeke.videoplayer.render.RenderViewFactory;
import com.dueeeke.videoplayer.render.TextureRenderView;

public class TikTokRenderViewFactory extends RenderViewFactory {

    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TikTokRenderView(new TextureRenderView(context));
    }
}
