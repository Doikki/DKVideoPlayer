package xyz.doikki.videoplayer

import xyz.doikki.videoplayer.player.PlayerFactory
import xyz.doikki.videoplayer.player.SystemPlayerFactory
import xyz.doikki.videoplayer.render.AspectRatioType
import xyz.doikki.videoplayer.render.RenderFactory
import xyz.doikki.videoplayer.util.NewFunc

/**
 * 播放器全局配置
 * Created by Doikki on 2022/10/17.
 */
object GlobalConfig {

    /**
     * 是否开启调试模式
     * 影响一些组件的日志输出
     */
    @JvmStatic
    var isDebuggable: Boolean = false

    /**
     * 播放器内核工厂
     */
    @JvmStatic
    var playerFactory: PlayerFactory = SystemPlayerFactory.create()

    /**
     * Render工厂
     */
    @JvmStatic
    var renderFactory: RenderFactory = RenderFactory.DEFAULT

    /**
     * 是否采用焦点模式：用于TV项目采用按键操作,开启此选项会改变部分Controller&controlComponent的操作方式
     */
    @JvmStatic
    @NewFunc
    var isTelevisionUiMode = false

    /**
     * 是否适配刘海屏，默认适配
     */
    @JvmStatic
    var isAdaptCutout: Boolean = true

    /**
     * [xyz.doikki.videoplayer.render.TextureViewRender] 是否开启优化TextureView渲染；
     * 默认开启
     */
    @JvmStatic
    @NewFunc
    var isTextureViewRenderOptimizationEnabled = true

    /**
     * 是否在移动网络下直接播放视频
     * 默认true
     */
    @JvmStatic
    var isPlayOnMobileNetwork: Boolean = true

    /**
     * 图像比例模式
     */
    @JvmStatic
    @AspectRatioType
    var screenAspectRatioType: Int = AspectRatioType.DEFAULT_SCALE

    /**
     * RenderView 是否重用（即在播放器调用播放或者重新播放的时候，是否重用已有的RenderView：以前的版本是每次都会创建一个新的RenderView）
     */
    @JvmStatic
    @NewFunc
    var isRenderReusable: Boolean = false
}