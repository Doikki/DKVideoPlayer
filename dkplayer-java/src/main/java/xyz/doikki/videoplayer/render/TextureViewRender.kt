package xyz.doikki.videoplayer.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.player.IPlayer
import xyz.doikki.videoplayer.render.Render.Companion.createShotBitmap
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.Render.SurfaceListener
import xyz.doikki.videoplayer.util.L
import java.lang.ref.WeakReference

/**
 * 经查看[TextureView]源码，发现在[.onDetachedFromWindow]的时候，
 * 会先回调[TextureView.SurfaceTextureListener.onSurfaceTextureDestroyed]并释放[SurfaceTexture],
 * 在需要[SurfaceTexture]时会重新构建并回调[TextureView.SurfaceTextureListener.onSurfaceTextureAvailable]
 *
 * @see Render 具体可调用的方法请查看Render
 */
@SuppressLint("ViewConstructor")
class TextureViewRender : TextureView, Render {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val proxy: RenderViewProxy = RenderViewProxy.new(this)
    private var playerRef: WeakReference<IPlayer>? = null
    private var st: SurfaceTexture? = null
    private var surface: Surface? = null
    private var surfaceListener: SurfaceListener? = null

    private val isEnableRenderOptimization: Boolean = GlobalConfig.isTextureViewRenderOptimizationEnabled

    private val stListener: SurfaceTextureListener = object : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            L.d("TextureViewRender onSurfaceTextureAvailable $st $width $height ${playerRef?.get()}")
            if (isEnableRenderOptimization) {
                //开启渲染优化
                if (st == null) {
                    st = surfaceTexture
                    this@TextureViewRender.surface = Surface(st)
                    bindSurfaceToMediaPlayer(this@TextureViewRender.surface!!)
                } else {
                    //在开启优化的情况下，使用最开始的那个渲染器
                    setSurfaceTexture(st!!)
                }
            } else {
                this@TextureViewRender.surface = Surface(st)
                bindSurfaceToMediaPlayer(this@TextureViewRender.surface!!)
            }
            notifySurfaceAvailable(this@TextureViewRender.surface, width, height)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            L.d("TextureViewRender onSurfaceTextureSizeChanged $st $width $height")
            surfaceListener?.onSurfaceSizeChanged(this@TextureViewRender.surface, width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            L.d("TextureViewRender onSurfaceTextureDestroyed $st")
            //清空释放
            surfaceListener?.onSurfaceDestroyed(this@TextureViewRender.surface)
            return if (isEnableRenderOptimization) {
                //如果开启了渲染优化，那mSurfaceTexture通常情况不可能为null（在onSurfaceTextureAvailable初次回调的时候被赋值了），
                // 所以这里通常返回的是false，返回值false会告诉父类不要释放SurfaceTexture
                st == null
            } else {
                true
            }
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            if (isEnableRenderOptimization && st == null) {
                //用于修正开启渲染优化的情况下，mSurfaceTexture为空的异常情况：据说是因为存在机型不回调onSurfaceTextureAvailable而只回调此方法
                st = surface
                this@TextureViewRender.surface = Surface(surface)
                bindSurfaceToMediaPlayer(this@TextureViewRender.surface!!)
                notifySurfaceAvailable(this@TextureViewRender.surface, width, height)
            } else if (this@TextureViewRender.surface == null) {
                //用于修正未开启渲染优化的情况下，mSurface为空的异常情况：据说是因为存在机型不回调onSurfaceTextureAvailable而只回调此方法
                this@TextureViewRender.surface = Surface(surfaceTexture)
                bindSurfaceToMediaPlayer(this@TextureViewRender.surface!!)
                notifySurfaceAvailable(this@TextureViewRender.surface, width, height)
            } else {
                //onSurfaceTextureUpdated会在SurfaceTexture.updateTexImage()的时候回调该方法，因此只要图像更新，都会调用本方法，因此在这个方法中不适合做什么处理
//                Log.d("TextureView", "onSurfaceTextureUpdated $surface")
//                mSurfaceListener?.onSurfaceUpdated(mSurface)
            }
        }

        private fun bindSurfaceToMediaPlayer(surface: Surface) {
            playerRef?.get()?.setSurface(surface)
        }

        private fun notifySurfaceAvailable(surface: Surface?, width: Int, height: Int) {
            surfaceListener?.onSurfaceAvailable(surface)
        }
    }

    /**
     * 绑定播放器
     *
     * @param player
     */
    override fun attachPlayer(player: IPlayer) {
        playerRef = WeakReference(player)
        //当前surface不为空，则说明是surface重用
        surface?.let {
            player.setSurface(it)
        }
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        proxy.setVideoSize(videoWidth, videoHeight)
    }

    override val view: View = this

    override fun setSurfaceListener(listener: SurfaceListener?) {
        surfaceListener = listener
    }

    override fun screenshot(highQuality: Boolean, callback: ScreenShotCallback) {
        if (!isAvailable) {
            callback.onScreenShotResult(null)
            return
        }
        if (highQuality) {
            callback.onScreenShotResult(bitmap)
        } else {
            callback.onScreenShotResult(getBitmap(createShotBitmap(this, false)))
        }
    }

    override fun setAspectRatioType(aspectRatioType: Int) {
        proxy.setAspectRatioType(aspectRatioType)
    }

    override fun setVideoRotation(degree: Int) {
        proxy.setVideoRotation(degree)
    }

    override fun setMirrorRotation(enable: Boolean) {
        proxy.setMirrorRotation(enable)
    }

    override fun release() {
        surface?.release()
        st?.release()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        proxy.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(proxy.measuredWidth, proxy.measuredHeight)
    }

    init {
        surfaceTextureListener = stListener
    }
}