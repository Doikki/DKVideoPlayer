package xyz.doikki.videocontroller.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import kotlin.math.abs

/**
 * 播放出错提示界面
 * Created by Doikki on 2017/4/13.
 * update by luochao on022/9/28 调整基类接口变更引起的变动，去掉无用代码
 */
class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseControlComponent(
    context, attrs, defStyleAttr
) {
    private var downX = 0f
    private var downY = 0f

    override fun onPlayStateChanged(playState: Int, extras: HashMap<String, Any>) {
        if (playState == VideoView.STATE_ERROR) {
            bringToFront()
            visibility = VISIBLE
        } else if (playState == VideoView.STATE_IDLE) {
            visibility = GONE
        }
    }

    /**
     * 以下逻辑用于小窗展示的情况下，避免在触摸的小范围内滑动窗口
     *
     * @param ev
     * @return
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                // True if the child does not want the parent to intercept touch events.
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val absDeltaX = abs(ev.x - downX)
                val absDeltaY = abs(ev.y - downY)
                if (absDeltaX > ViewConfiguration.get(context).scaledTouchSlop ||
                    absDeltaY > ViewConfiguration.get(context).scaledTouchSlop
                ) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    init {
        visibility = GONE
        if (isInEditMode) {
            visibility = VISIBLE
        }
        setBackgroundResource(R.color.dkplayer_control_component_container_color)
        layoutInflater.inflate(R.layout.dkplayer_layout_error_view, this)
        val statusBtn = findViewById<View>(R.id.status_btn)
        if (isTelevisionUiMode()) {
            statusBtn.isFocusable = true
            statusBtn.isFocusableInTouchMode = true
        } else {
            //设置当前容器能点击的原因是为了避免事件穿透
            isClickable = true
        }
        statusBtn.setOnClickListener {
            visibility = GONE
            controller?.replay(false)
        }
    }
}