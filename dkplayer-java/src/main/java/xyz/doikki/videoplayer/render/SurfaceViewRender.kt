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
import xyz.doikki.videoplayer.player.IPlayer
import xyz.doikki.videoplayer.render.Render.Companion.createShotBitmap
import xyz.doikki.videoplayer.render.Render.ScreenShotCallback
import xyz.doikki.videoplayer.render.Render.SurfaceListener
import xyz.doikki.videoplayer.util.L
import java.lang.ref.WeakReference

class SurfaceViewRender @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), Render {

    private val proxy: RenderViewProxy = RenderViewProxy.new(this)
    private var surfaceListener: SurfaceListener? = null
    private var sh: SurfaceHolder? = null
    private var playerRef: WeakReference<IPlayer>? = null
    private val player: IPlayer? get() = playerRef?.get()
    private val shCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            L.d("SurfaceViewRender surfaceCreated($holder)")
            sh = holder
            player?.setDisplay(holder)
            surfaceListener?.onSurfaceAvailable(holder.surface)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
            L.d("SurfaceViewRender surfaceChanged($holder,$format,$w,$h)")
            if (holder != sh) {
                sh = holder
                player?.setDisplay(holder)
                surfaceListener?.onSurfaceUpdated(holder.surface)
            } else {
                surfaceListener?.onSurfaceSizeChanged(holder.surface, width, height)
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            L.d("SurfaceViewRender surfaceDestroyed($holder)")
            // after we return from this we can't use the surface any more
            sh = null
            player?.setDisplay(null)
            surfaceListener?.onSurfaceDestroyed(holder.surface)
        }
    }

    override fun attachPlayer(player: IPlayer) {
        playerRef = WeakReference(player)
        //当前SurfaceHolder不为空，则说明是重用render
        sh?.let {
            player.setDisplay(it)
        }
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        proxy.setVideoSize(videoWidth, videoHeight)
    }

    override fun setSurfaceListener(listener: SurfaceListener?) {
        surfaceListener = listener
    }

    override fun setAspectRatioType(aspectRatioType: Int) {
        proxy.setAspectRatioType(aspectRatioType)
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
        proxy.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(proxy.measuredWidth, proxy.measuredHeight)
    }

    init {
        val surfaceHolder = holder
        surfaceHolder.addCallback(shCallback)
        surfaceHolder.setFormat(PixelFormat.RGBA_8888)
    }
}