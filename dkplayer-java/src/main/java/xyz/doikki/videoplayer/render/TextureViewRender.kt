package xyz.doikki.videoplayer.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import xyz.doikki.videoplayer.DKManager
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.render.Render.Companion.createShotBitmap
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.Render.SurfaceListener
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

    private val mProxy: RenderViewProxy = RenderViewProxy.new(this)
    private var mPlayerRef: WeakReference<DKPlayer>? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mSurfaceListener: SurfaceListener? = null

    private val isEnableRenderOptimization: Boolean = DKManager.isTextureViewRenderOptimizationEnabled

    private val mSTCallback: SurfaceTextureListener = object : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            Log.d(
                "TextureView",
                "onSurfaceTextureAvailable $surfaceTexture $width $height ${mPlayerRef?.get()}"
            )
            if (isEnableRenderOptimization) {
                //开启渲染优化
                if (mSurfaceTexture == null) {
                    mSurfaceTexture = surfaceTexture
                    mSurface = Surface(surfaceTexture)
                    bindSurfaceToMediaPlayer(mSurface!!)
                } else {
                    //在开启优化的情况下，使用最开始的那个渲染器
                    setSurfaceTexture(mSurfaceTexture!!)
                }
            } else {
                mSurface = Surface(surfaceTexture)
                bindSurfaceToMediaPlayer(mSurface!!)
            }
            notifySurfaceAvailable(mSurface, width, height)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            Log.d("TextureView", "onSurfaceTextureSizeChanged $surfaceTexture $width $height")
            mSurfaceListener?.onSurfaceSizeChanged(mSurface, width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Log.d("TextureView", "onSurfaceTextureDestroyed $surfaceTexture")
            //清空释放
            mSurfaceListener?.onSurfaceDestroyed(mSurface)
            return if (isEnableRenderOptimization) {
                //如果开启了渲染优化，那mSurfaceTexture通常情况不可能为null（在onSurfaceTextureAvailable初次回调的时候被赋值了），
                // 所以这里通常返回的是false，返回值false会告诉父类不要释放SurfaceTexture
                mSurfaceTexture == null
            } else {
                true
            }
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            if (isEnableRenderOptimization && mSurfaceTexture == null) {
                //用于修正开启渲染优化的情况下，mSurfaceTexture为空的异常情况：据说是因为存在机型不回调onSurfaceTextureAvailable而只回调此方法
                mSurfaceTexture = surface
                mSurface = Surface(surface)
                bindSurfaceToMediaPlayer(mSurface!!)
                notifySurfaceAvailable(mSurface, width, height)
            } else if (mSurface == null) {
                //用于修正未开启渲染优化的情况下，mSurface为空的异常情况：据说是因为存在机型不回调onSurfaceTextureAvailable而只回调此方法
                mSurface = Surface(surfaceTexture)
                bindSurfaceToMediaPlayer(mSurface!!)
                notifySurfaceAvailable(mSurface, width, height)
            } else {
                //onSurfaceTextureUpdated会在SurfaceTexture.updateTexImage()的时候回调该方法，因此只要图像更新，都会调用本方法，因此在这个方法中不适合做什么处理
//                Log.d("TextureView", "onSurfaceTextureUpdated $surface")
//                mSurfaceListener?.onSurfaceUpdated(mSurface)
            }
        }

        private fun bindSurfaceToMediaPlayer(surface: Surface) {
            mPlayerRef?.get()?.setSurface(surface)
        }

        private fun notifySurfaceAvailable(surface: Surface?, width: Int, height: Int) {
            mSurfaceListener?.onSurfaceAvailable(surface)
        }
    }

    /**
     * 绑定播放器
     *
     * @param player
     */
    override fun attachPlayer(player: DKPlayer) {
        mPlayerRef = WeakReference(player)
        //当前surface不为空，则说明是surface重用
        mSurface?.let {
            player.setSurface(it)
        }
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mProxy.setVideoSize(videoWidth, videoHeight)
    }

    /*************START Render 实现逻辑 */

    override val view: View = this

    override fun setSurfaceListener(listener: SurfaceListener?) {
        mSurfaceListener = listener
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
        mProxy.setAspectRatioType(aspectRatioType)
    }

    override fun setVideoRotation(degree: Int) {
        mProxy.setVideoRotation(degree)
    }

    override fun setMirrorRotation(enable: Boolean) {
        mProxy.setMirrorRotation(enable)
    }

    override fun release() {
        mSurface?.release()
        mSurfaceTexture?.release()
    }

    /*************END Render 实现逻辑 */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mProxy.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mProxy.measuredWidth, mProxy.measuredHeight)
    }

    init {
        surfaceTextureListener = mSTCallback
    }
}