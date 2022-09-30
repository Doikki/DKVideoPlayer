package xyz.doikki.videoplayer.internal

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import xyz.doikki.videoplayer.DKManager
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.orDefault
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.Render
import xyz.doikki.videoplayer.render.RenderFactory

internal class DKVideoViewContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /**
     * 渲染视图
     */
    private var mRender: Render? = null

    /**
     * 自定义Render工厂
     */
    private var mRenderFactory: RenderFactory? = null

    /**
     * 渲染视图纵横比
     */
    @AspectRatioType
    private var mScreenAspectRatioType = DKVideoView.SCREEN_ASPECT_RATIO_DEFAULT

    /**
     * 视频画面大小
     */
    private val mVideoSize = intArrayOf(0, 0)

    /**
     * 获取渲染视图的名字
     * @return
     */
    val renderName: String
        get() {
            val className = mRenderFactory.orDefault(DKManager.renderFactory).javaClass.name
            return className.substring(className.lastIndexOf("."))
        }

    //todo 直接返回数组对象是否欠妥
    val videoSize: IntArray = mVideoSize

    /**
     * 初始化视频渲染View
     */
    fun setupRenderView(player: DKPlayer) {
        mRender?.let {
            removeView(it.view)
            it.release()
        }
        mRender = DKManager.createRender(context, mRenderFactory).also {
            it.attachPlayer(player)
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            //render添加到最底层
            addView(it.view, 0, params)
        }
    }

    /**
     * 自定义RenderView，继承[RenderFactory]实现自己的RenderView
     */
    fun setRenderViewFactory(renderViewFactory: RenderFactory) {
        mRenderFactory = renderViewFactory
    }

    fun setScreenAspectRatioType(@AspectRatioType aspectRatioType: Int) {
        mScreenAspectRatioType = aspectRatioType
        mRender?.setAspectRatioType(aspectRatioType)
    }

    fun screenshot(highQuality: Boolean, callback: Render.ScreenShotCallback) {
        val render = mRender
        if (render != null) {
            render.screenshot(highQuality, callback)
            return
        }
        callback.onScreenShotResult(null)
    }

    /**
     * 旋转视频画面
     *
     * @param degree 旋转角度
     */
    fun setVideoRotation(degree: Int) {
        mRender?.setVideoRotation(degree)
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    fun setVideoMirrorRotation(enable: Boolean) {
        mRender?.setMirrorRotation(enable)
    }

    /**
     * 保持屏幕常亮
     *
     * @param isOn
     */
    fun keepScreenOn(isOn: Boolean) {
        this.keepScreenOn = isOn
    }

    fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        mVideoSize[0] = videoWidth
        mVideoSize[1] = videoHeight
        mRender?.let {
            it.setAspectRatioType(mScreenAspectRatioType)
            it.setVideoSize(videoWidth, videoHeight)
        }
    }

    fun release() {
        //释放renderView
        mRender?.let {
            removeView(it.view)
            it.release()

        }
        mRender = null

        //关闭屏幕常亮
        this.keepScreenOn = false
    }

}