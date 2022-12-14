package xyz.doikki.videoplayer.render

import android.view.View
import androidx.annotation.IntRange
import xyz.doikki.videoplayer.util.L

/**
 * 测量工具类
 */
class RenderMeasure {

    /**
     * 视频旋转角度
     * （外层Render在接收到视频带有旋转信息时已经调用了[View.setRotation]），所以当时的View已经旋转了
     */
    @JvmField
    @IntRange(from = 0, to = 360)
    var videoRotationDegree = 0

    /**
     * 视频宽
     */
    private var mVideoWidth = 0

    /**
     * 视频高
     */
    private var mVideoHeight = 0

    /**
     * 界面缩放类型
     */
    @get:AspectRatioType
    @AspectRatioType
    var aspectRatioType = 0

    /**
     * 获取宽的测量结果
     */
    var measuredWidth = 0
        private set

    /**
     * 测量所得的高
     */
    var measuredHeight = 0
        private set

    /**
     * 设置视频大小
     *
     * @param width  视频内容宽
     * @param height 视频内容高
     */
    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     *
     * @param rotationDegree 旋转角度，[View.getRotation]
     */
    fun doMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        @IntRange(from = 0, to = 360) rotationDegree: Int
    ) {
        videoRotationDegree = rotationDegree
        doMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     */
    fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //测量参数是否已旋转
        val isMeasureRotated = videoRotationDegree == 90 || videoRotationDegree == 270
        //相对的宽 MeasureSpec
        val relateWidthMeasureSpec: Int
        //相对的高 MeasureSpec
        val relateHeightMeasureSpec: Int
        if (isMeasureRotated) { //交换宽高测量参数
            relateWidthMeasureSpec = heightMeasureSpec
            relateHeightMeasureSpec = widthMeasureSpec
        } else {
            relateWidthMeasureSpec = widthMeasureSpec
            relateHeightMeasureSpec = heightMeasureSpec
        }
        if (mVideoHeight == 0 || mVideoWidth == 0) { //当前视频没有宽高返回看，则直接返回测量结果
            measuredWidth = 1 // View.MeasureSpec.getSize(relateWidthMeasureSpec);
            measuredHeight = 1 //View.MeasureSpec.getSize(relateHeightMeasureSpec);
            return
        }
        val aspectType = aspectRatioType
        if (aspectType == AspectRatioType.MATCH_PARENT) {
            //匹配Parent的大小，则直接返回测量参数
            measuredWidth = relateWidthMeasureSpec
            measuredHeight = relateHeightMeasureSpec
            val sb = StringBuilder()
            sb.append("aspectType=").append(aspectType).append("\n")
            sb.append("mMeasuredWidth=").append(measuredWidth).append("\n")
            sb.append("mMeasuredHeight=").append(measuredHeight).append("\n")
            L.i(sb.toString())
            return
        }
        val videoWidth = mVideoWidth
        val videoHeight = mVideoHeight
        val widthSpecMode = View.MeasureSpec.getMode(relateWidthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(relateWidthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(relateHeightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(relateHeightMeasureSpec)
        //视屏图像比例
        val displayAspectRatio =
            calculateDisplayAspectRatio(aspectType, isMeasureRotated, videoWidth, videoHeight)
        //控件大小比例
        val specAspectRatio = widthSpecSize.toFloat() / heightSpecSize.toFloat()
        //是否是图像比例更宽：即视频图像的宽高比 大于了 控件的宽高比，也就是是说控件区域更窄，类似竖屏的情况下显示横屏视频
        val isWiderDisplay = displayAspectRatio > specAspectRatio
        val preMeasuredWidth: Int
        val preMeasuredHeight: Int
        if (isWiderDisplay) { //通过控件能够分配的比例跟图像比例做了比较，因此不再需要判断测量Mode
            when (aspectType) {
                AspectRatioType.CENTER_CROP -> { //特殊处理，限定高来计算宽
                    preMeasuredHeight = heightSpecSize
                    preMeasuredWidth = (preMeasuredHeight * displayAspectRatio).toInt()
                }
                AspectRatioType.SCALE_ORIGINAL -> {
                    preMeasuredWidth = Math.min(widthSpecSize, videoWidth)
                    preMeasuredHeight = (preMeasuredWidth / displayAspectRatio).toInt()
                }
                else -> { //缩放模式
                    preMeasuredWidth = widthSpecSize
                    preMeasuredHeight = (preMeasuredWidth / displayAspectRatio).toInt()
                }
            }
        } else {
            when (aspectType) {
                AspectRatioType.CENTER_CROP -> {
                    preMeasuredWidth = widthSpecSize
                    preMeasuredHeight = (preMeasuredWidth / displayAspectRatio).toInt()
                }
                AspectRatioType.SCALE_ORIGINAL -> {
                    preMeasuredHeight = Math.min(heightSpecSize, videoHeight)
                    preMeasuredWidth = (preMeasuredHeight * displayAspectRatio).toInt()
                }
                else -> { //缩放模式
                    preMeasuredHeight = heightSpecSize
                    preMeasuredWidth = (preMeasuredHeight * displayAspectRatio).toInt()
                }
            }
        }
        measuredWidth = preMeasuredWidth
        measuredHeight = preMeasuredHeight
        val sb = StringBuilder()
        sb.append("aspectType=").append(aspectType).append("\n")
        sb.append("isWiderDisplay=").append(isWiderDisplay).append("\n")
        sb.append("displayAspectRatio=").append(displayAspectRatio).append("\n")
        sb.append("videoWidth=").append(videoWidth).append("\n")
        sb.append("videoHeight=").append(videoHeight).append("\n")
        sb.append("widthSpecSize=").append(widthSpecSize).append("\n")
        sb.append("heightSpecSize=").append(heightSpecSize).append("\n")
        sb.append("mMeasuredWidth=").append(measuredWidth).append("\n")
        sb.append("mMeasuredHeight=").append(measuredHeight).append("\n")
        L.i(sb.toString())
    }

    /**
     * 是否是缩放模式
     *
     * @param aspectRatioType
     * @return
     */
    private fun isScaleType(@AspectRatioType aspectRatioType: Int): Boolean {
        return aspectRatioType == AspectRatioType.DEFAULT_SCALE || aspectRatioType == AspectRatioType.SCALE_4_3 || aspectRatioType == AspectRatioType.SCALE_16_9 || aspectRatioType == AspectRatioType.SCALE_18_9
    }

    /**
     * 计算图像的缩放比例
     *
     * @param aspectRatioType  缩放模式
     * @param isMeasureRotated 被测量的控件是否旋转：旋转了则比例要取倒数进行计算
     * @param videoWidth       视频图像宽度
     * @param videoHeight      视频图像高度
     * @return 用于展示的图像比例
     */
    private fun calculateDisplayAspectRatio(
        @AspectRatioType aspectRatioType: Int,
        isMeasureRotated: Boolean,
        videoWidth: Int,
        videoHeight: Int
    ): Float {
        val displayAspectRatio: Float = when (aspectRatioType) {
            AspectRatioType.SCALE_4_3 -> if (isMeasureRotated) {
                3.0f / 4.0f
            } else {
                4.0f / 3.0f
            }
            AspectRatioType.SCALE_16_9 -> if (isMeasureRotated) {
                9.0f / 16.0f
            } else {
                16.0f / 9.0f
            }
            AspectRatioType.SCALE_18_9 -> if (isMeasureRotated) {
                9.0f / 18.0f
            } else {
                18.0f / 9.0f
            }
            AspectRatioType.DEFAULT_SCALE, AspectRatioType.SCALE_ORIGINAL, AspectRatioType.CENTER_CROP ->                 //以上模式按照图像比例进行处理
                videoWidth.toFloat() / videoHeight.toFloat()
            else -> videoWidth.toFloat() / videoHeight.toFloat()
        }
        return displayAspectRatio
    }

}