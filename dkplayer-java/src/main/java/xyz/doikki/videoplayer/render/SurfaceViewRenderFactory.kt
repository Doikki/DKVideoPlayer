package xyz.doikki.videoplayer.render

import android.content.Context

internal class SurfaceViewRenderFactory : RenderFactory {

    override fun create(context: Context): Render {
        return SurfaceViewRender(context)
    }

    override fun equals(other: Any?): Boolean {
        println("equals ${other is SurfaceViewRenderFactory}")
        if (other is SurfaceViewRenderFactory)
            return true
        return super.equals(other)
    }

    override fun hashCode(): Int {
        println("hashCode $this ${this.javaClass.hashCode()}")
        return this.javaClass.hashCode()
    }

}