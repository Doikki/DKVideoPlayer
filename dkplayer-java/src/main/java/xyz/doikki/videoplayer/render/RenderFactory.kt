package xyz.doikki.videoplayer.render

import android.content.Context
import android.os.Build
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.render.RenderFactory.Companion.textureViewRenderFactory

/**
 * 此接口用于扩展自己的渲染View。使用方法如下：
 * 1.继承IRenderView实现自己的渲染View。
 * 2.重写createRenderView返回步骤1的渲染View。
 * 3.通过[GlobalConfig.renderFactory] 设置步骤2的实例
 * 可参考[TextureViewRender]和[textureViewRenderFactory]的实现。
 */
fun interface RenderFactory {

    fun create(context: Context): Render

    companion object {

        @JvmStatic
        val DEFAULT: RenderFactory =
            if (Build.VERSION.SDK_INT < 21) surfaceViewRenderFactory() else textureViewRenderFactory()

        @JvmStatic
        fun textureViewRenderFactory(): RenderFactory {
            return TextureViewRenderFactory()
        }

        @JvmStatic
        fun surfaceViewRenderFactory(): RenderFactory {
            return SurfaceViewRenderFactory()
        }
    }
}