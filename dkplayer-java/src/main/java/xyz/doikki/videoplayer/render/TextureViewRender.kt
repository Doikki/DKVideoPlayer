package xyz.doikki.videoplayer.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
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
class TextureViewRender : TextureView, Render, SurfaceTextureListener {

    private val mMeasureHelper: RenderLayoutMeasure = RenderLayoutMeasure()

    private var mPlayerRef: WeakReference<DKPlayer>? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null
    private var mSurfaceListener: SurfaceListener? = null

    private val isEnableRenderOptimization: Boolean = DKManager.isTextureViewRenderOptimizationEnabled

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        surfaceTextureListener = this
    }

    /**
     * 绑定播放器
     *
     * @param player
     */
    override fun attachPlayer(player: DKPlayer) {
        mPlayerRef?.let { ref ->
            ref.get()?.let { previousPlayer ->
                if (previousPlayer !== player) {
                    //如果之前已绑定过播放器，并且与当前设置的播放器不相同，则将surface与之前的播放器解除绑定
                    previousPlayer.setSurface(null)
                    mPlayerRef = null
                }
            }
        }
        mPlayerRef = WeakReference(player)
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
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
        if (mMeasureHelper.aspectRatioType == aspectRatioType)
            return
        mMeasureHelper.aspectRatioType = aspectRatioType
        requestLayout()
    }

    override fun setVideoRotation(degree: Int) {
        if (mMeasureHelper.videoRotationDegree == degree)
            return
        rotation = degree.toFloat()
        requestLayout()
    }

    override fun setMirrorRotation(enable: Boolean) {
        scaleX = if (enable) -1f else 1f
    }

    override fun release() {
        if (mSurface != null) mSurface!!.release()
        if (mSurfaceTexture != null) mSurfaceTexture!!.release()
    }

    /*************END Render 实现逻辑 */
    /*************START TextureView.SurfaceTextureListener 实现逻辑 */
    override fun onSurfaceTextureAvailable(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
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

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        //清空释放
        if (mSurfaceListener != null) {
            mSurfaceListener!!.onSurfaceDestroyed(mSurface)
        }
        return if (isEnableRenderOptimization) {
            //如果开启了渲染优化，那mSurfaceTexture通常情况不可能为null（在onSurfaceTextureAvailable初次回调的时候被赋值了），
            // 所以这里通常返回的是false，返回值false会告诉父类不要释放SurfaceTexture
            mSurfaceTexture == null
        } else {
            true
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceListener?.onSurfaceSizeChanged(mSurface, width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        mSurfaceListener?.onSurfaceUpdated(mSurface)
    }

    private fun bindSurfaceToMediaPlayer(surface: Surface) {
        mPlayerRef?.get()?.setSurface(surface)
    }

    private fun notifySurfaceAvailable(surface: Surface?, width: Int, height: Int) {
        mSurfaceListener?.onSurfaceAvailable(surface)
    }

    /*************END TextureView.SurfaceTextureListener 实现逻辑 */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper!!.measuredWidth, mMeasureHelper.measuredHeight)
    }

}