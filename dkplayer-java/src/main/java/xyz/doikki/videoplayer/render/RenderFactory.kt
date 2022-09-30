package xyz.doikki.videoplayer.render

import android.content.Context
import xyz.doikki.videoplayer.DKManager

/**
 * 此接口用于扩展自己的渲染View。使用方法如下：
 * 1.继承IRenderView实现自己的渲染View。
 * 2.重写createRenderView返回步骤1的渲染View。
 * 3.通过[DKManager.setRenderFactory] 设置步骤2的实例
 * 可参考[TextureViewRender]和[textureViewRenderFactory]的实现。
 */
fun interface RenderFactory {

    fun create(context: Context): Render

    companion object {

        @JvmStatic
        val DEFAULT: RenderFactory = textureViewRenderFactory()

        /**
         *
         */
        @JvmStatic
        fun textureViewRenderFactory(): RenderFactory {
            return RenderFactory { context -> TextureViewRender(context) }
        }

        /**
         * 基于[android.view.SurfaceView]的默认实现
         */
        @JvmStatic
        fun surfaceViewRenderFactory(): RenderFactory {
            return object : RenderFactory {
                override fun create(context: Context): Render {
                    return SurfaceViewRender(context)
                }
            }
        }
    }


}