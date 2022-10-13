package xyz.doikki.videocontroller.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import xyz.doikki.videoplayer.controller.ControlWrapper
import xyz.doikki.videoplayer.controller.component.ControlComponent
import xyz.doikki.videoplayer.util.PlayerUtils

abstract class BaseControlComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ControlComponent {

    @JvmField
    protected var mControlWrapper: ControlWrapper? = null

    init {
        setupViews()
    }

    protected abstract fun setupViews()

    protected val activity: Activity?
        get() = PlayerUtils.scanForActivity(context)

    protected fun setViewInFocusMode(view: View) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }

    override fun attach(controlWrapper: ControlWrapper) {
        mControlWrapper = controlWrapper
    }

    override fun getView(): View? {
        return this
    }


}