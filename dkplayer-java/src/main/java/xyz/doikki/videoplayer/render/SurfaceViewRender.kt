package xyz.doikki.videoplayer.render

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.render.Render.Companion.createShotBitmap
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.Render.SurfaceListener
import xyz.doikki.videoplayer.util.L

class SurfaceViewRender @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs), Render, SurfaceHolder.Callback {

    private val mMeasureHelper: RenderLayoutMeasure = RenderLayoutMeasure()
    private var mMediaPlayer: DKPlayer? = null
    private var mSurfaceListener: SurfaceListener? = null

    init {
        val surfaceHolder = holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setFormat(PixelFormat.RGBA_8888)
    }

    override fun attachPlayer(player: DKPlayer) {
        mMediaPlayer = player
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
    }

    override fun setSurfaceListener(listener: SurfaceListener?) {
        mSurfaceListener = listener
    }

    override fun setVideoRotation(degree: Int) {
        if (mMeasureHelper.videoRotationDegree == degree)
            return
        mMeasureHelper.videoRotationDegree = degree
        rotation = degree.toFloat()
    }

    override fun setAspectRatioType(aspectRatioType: Int) {
        if (mMeasureHelper.aspectRatioType != aspectRatioType)
            return
        mMeasureHelper.aspectRatioType = aspectRatioType
        requestLayout()
    }

    override fun screenshot(highQuality: Boolean, callback: ScreenShotCallback) {
        if (Build.VERSION.SDK_INT >= 24) {
            val bmp = createShotBitmap(this, highQuality)
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()
            PixelCopy.request(this, bmp, { copyResult: Int ->
                try {
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback.onScreenShotResult(bmp)
                    }
                    handlerThread.quitSafely()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    if (!bmp.isRecycled) bmp.recycle()
                    callback.onScreenShotResult(null)
                }
            }, Handler())
        } else {
            callback.onScreenShotResult(null)
            L.w("SurfaceView not support screenshot when Build.VERSION.SDK_INT < Build.VERSION_CODES.N")
        }
    }

    override val view: View = this

    override fun release() {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper.measuredWidth, mMeasureHelper.measuredHeight)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mSurfaceListener?.onSurfaceAvailable(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mMediaPlayer?.setDisplay(holder)
        mSurfaceListener?.onSurfaceSizeChanged(holder.surface, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mSurfaceListener?.onSurfaceDestroyed(holder.surface)
    }

}