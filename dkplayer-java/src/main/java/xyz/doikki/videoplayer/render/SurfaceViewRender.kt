package xyz.doikki.videoplayer.render

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.render.Render.Companion.createShotBitmap
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.Render.SurfaceListener
import xyz.doikki.videoplayer.util.L
import java.lang.ref.WeakReference

class SurfaceViewRender @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), Render {

    private val mProxy: RenderViewProxy = RenderViewProxy.new(this)
    private var mSurfaceListener: SurfaceListener? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mPlayerRef: WeakReference<DKPlayer>? = null
    private val player: DKPlayer? get() = mPlayerRef?.get()
    private val mSHCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d("SurfaceViewRender", "surfaceCreated($holder)")
            mSurfaceHolder = holder
            player?.setDisplay(holder)
            mSurfaceListener?.onSurfaceAvailable(holder.surface)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
            Log.d("SurfaceViewRender", "surfaceChanged($holder,$format,$w,$h)")
            if (holder != mSurfaceHolder) {
                mSurfaceHolder = holder
                player?.setDisplay(holder)
                mSurfaceListener?.onSurfaceUpdated(holder.surface)
            } else {
                mSurfaceListener?.onSurfaceSizeChanged(holder.surface, width, height)
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d("SurfaceViewRender", "surfaceDestroyed($holder)")
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null
            player?.setDisplay(null)
            mSurfaceListener?.onSurfaceDestroyed(holder.surface)
        }
    }

    override fun attachPlayer(player: DKPlayer) {
        mPlayerRef = WeakReference(player)
        //当前SurfaceHolder不为空，则说明是重用render
        mSurfaceHolder?.let {
            player.setDisplay(it)
        }
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mProxy.setVideoSize(videoWidth, videoHeight)
    }

    override fun setSurfaceListener(listener: SurfaceListener?) {
        mSurfaceListener = listener
    }

    override fun setAspectRatioType(aspectRatioType: Int) {
        mProxy.setAspectRatioType(aspectRatioType)
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
        mProxy.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mProxy.measuredWidth, mProxy.measuredHeight)
    }

    init {
        val surfaceHolder = holder
        surfaceHolder.addCallback(mSHCallback)
        surfaceHolder.setFormat(PixelFormat.RGBA_8888)
    }
}