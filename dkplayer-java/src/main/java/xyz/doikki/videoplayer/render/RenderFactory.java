package xyz.doikki.videoplayer.render;

import android.content.Context;

/**
 * 此接口用于扩展自己的渲染View。使用方法如下：
 * 1.继承IRenderView实现自己的渲染View。
 * 2.重写createRenderView返回步骤1的渲染View。
 * 可参考{@link TextureViewRender}和{@link TextureViewRenderFactory}的实现。
 */
public interface RenderFactory {

    Render create(Context context);

    public static RenderFactory textureViewRenderFactory(){
        return new RenderFactory() {
            @Override
            public Render create(Context context) {
                return new TextureViewRender(context);
            }
        };
    }
}
