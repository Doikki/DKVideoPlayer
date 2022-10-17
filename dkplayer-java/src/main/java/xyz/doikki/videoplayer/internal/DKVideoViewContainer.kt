package xyz.doikki.videoplayer.internal

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import xyz.doikki.videoplayer.*
import xyz.doikki.videoplayer.controller.VideoController
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.Render
import xyz.doikki.videoplayer.render.RenderFactory
import xyz.doikki.videoplayer.util.canTakeFocus
import xyz.doikki.videoplayer.util.orDefault

/**
 * 真正的容器：内部包含了Render
 */
internal class DKVideoViewContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    /**
     * render是否可以重用
     */
    private var renderReusable = DKPlayerConfig.isRenderReusable

    /**
     * 渲染视图
     */
    private var render: Render? = null

    /**
     * 自定义RenderView，继承[RenderFactory]实现自己的RenderView,设置为null则会使用[DKManager.renderFactory]
     */
    var renderFactory = DKPlayerConfig.renderFactory
        set(value) {
            if (field == value) {
                // 当前工厂并没有发生任何变化，不作任何处理
                return
            }
            field = value.orDefault(DKPlayerConfig.renderFactory)

            // 如果之前已存在render，则将以前的render移除释放并重新创建
            if (render != null) {
                setupRender(true)
            }
        }

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
     * 视频画面大小
     * todo 直接返回数组对象是否欠妥
     */
    val videoSize: IntArray = mVideoSize

    private var attachedPlayer: DKPlayer? = null

    /**
     * 设置控制器，传null表示移除控制器
     */
    var videoController: VideoController? = null
        set(value) {
            field?.let { // 移除之前已添加的控制器
                removeView(it)
            }
            field = value
            value?.let { controller ->
                //添加控制器
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                addView(controller, params)
            }
        }

    /**
     * 初始化视频渲染View
     */
    fun attachPlayer(player: DKPlayer) {
        if (render == null || !renderReusable) {
            setupRender()
        }
        attachedPlayer = player
        render!!.attachPlayer(player)
    }

    /**
     * render是否可以重用
     */
    fun setRenderReusable(reusable: Boolean) {
        renderReusable = reusable
    }

    private fun setupRender(removePrevious: Boolean = true) {
        if (removePrevious) {
            removeRenderIfAdded()
        }
        render = renderFactory.create(context).also { render ->
            val params = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            //设置之前配置
            render.setAspectRatioType(mScreenAspectRatioType)
            render.setVideoSize(mVideoSize[0], mVideoSize[1])

            //render添加到最底层
            addView(render.view, 0, params)

            //播放器不为空说明是中途切换的renderFactory
            attachedPlayer?.let { player ->
                render.attachPlayer(player)
            }
        }
    }

    fun setScreenAspectRatioType(@AspectRatioType aspectRatioType: Int) {
        mScreenAspectRatioType = aspectRatioType
        render?.setAspectRatioType(aspectRatioType)
    }

    fun screenshot(highQuality: Boolean, callback: Render.ScreenShotCallback) {
        val render = render
        if (render != null) {
            render.screenshot(highQuality, callback)
            return
        }
        Log.w("DKPlayer", "render is null , screenshot is ignored.")
        callback.onScreenShotResult(null)
    }

    /**
     * 旋转视频画面
     *
     * @param degree 旋转角度
     */
    fun setVideoRotation(degree: Int) {
        render?.setVideoRotation(degree)
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    fun setVideoMirrorRotation(enable: Boolean) {
        render?.setMirrorRotation(enable)
    }

    /**
     * 视频大小发生变化：用于[DKVideoView]或者持有播放器[DKPlayer]的对象在[DKPlayer.EventListener.onVideoSizeChanged]回调时进行调用
     */
    fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        mVideoSize[0] = videoWidth
        mVideoSize[1] = videoHeight
        render?.setVideoSize(videoWidth, videoHeight)
    }

    /**
     * 重置
     */
    fun reset() {
        removeRenderIfAdded()
        mScreenAspectRatioType = AspectRatioType.DEFAULT_SCALE
        mVideoSize[0] = 0
        mVideoSize[1] = 0
    }

    /**
     * 释放资源
     */
    fun release() {
        removeRenderIfAdded()
        //关闭屏幕常亮
        this.keepScreenOn = false
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    fun onBackPressed(): Boolean {
        return videoController?.onBackPressed().orDefault()
    }

    //释放renderView
    private fun removeRenderIfAdded() {
        render?.let {
            removeView(it.view)
            it.release()
        }
        render = null
    }

    override fun addFocusables(views: ArrayList<View>, direction: Int) {
        val controller = videoController
        if (controller != null && controller.canTakeFocus) {
            views.add(controller)//controller能够获取焦点的情况下，优先只让controller获取焦点
            return
        }
        super.addFocusables(views, direction)
    }
}