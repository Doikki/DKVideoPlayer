package xyz.doikki.videoplayer.render

import android.view.View

class RenderViewProxy(private val view: View) {

    companion object {
        @JvmStatic
        fun new(view: View): RenderViewProxy {
            return RenderViewProxy(view)
        }
    }

    private val mMeasureHelper: RenderMeasure = RenderMeasure()

    /**
     * 获取宽的测量结果
     */
    val measuredWidth: Int get() = mMeasureHelper.measuredWidth

    /**
     * 测量所得的高
     */
    val measuredHeight: Int get() = mMeasureHelper.measuredHeight

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            view.requestLayout()
        }
    }

    fun setAspectRatioType(aspectRatioType: Int) {
        if (mMeasureHelper.aspectRatioType == aspectRatioType)
            return
        mMeasureHelper.aspectRatioType = aspectRatioType
        view.requestLayout()
    }

    fun setVideoRotation(degree: Int) {
        if (mMeasureHelper.videoRotationDegree == degree)
            return
        mMeasureHelper.videoRotationDegree = degree
        view.rotation = degree.toFloat()
    }

    fun setMirrorRotation(enable: Boolean) {
        view.scaleX = if (enable) -1f else 1f
    }

    fun isMirrorRotation(): Boolean {
        return view.scaleX == -1f
    }

    fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}