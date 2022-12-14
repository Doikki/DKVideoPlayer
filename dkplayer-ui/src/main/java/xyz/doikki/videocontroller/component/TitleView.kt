package xyz.doikki.videocontroller.component

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import xyz.doikki.videocontroller.R
import xyz.doikki.videoplayer.VideoView
import xyz.doikki.videoplayer.util.PlayerUtils
import xyz.doikki.videoplayer.util.orDefault

/**
 * 播放器顶部标题栏
 */
class TitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlComponent(context, attrs, defStyleAttr) {

    private val mTitleContainer: LinearLayout
    private val mTitle: TextView
    private val mSysTime: TextView//系统当前时间

    private lateinit var mBatteryReceiver: BatteryReceiver

    //是否注册BatteryReceiver
    private var mBatteryReceiverRegistered = false

    /**
     * 是否启用电量检测功能
     */
    private var mBatteryEnabled: Boolean = true

    fun setTitle(title: String?) {
        mTitle.text = title
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mBatteryEnabled && mBatteryReceiverRegistered) {
            context.unregisterReceiver(mBatteryReceiver)
            mBatteryReceiverRegistered = false
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mBatteryEnabled && !mBatteryReceiverRegistered) {
            context.registerReceiver(mBatteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            mBatteryReceiverRegistered = true
        }
    }

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        //只在全屏时才有效
        if (!controller?.isFullScreen.orDefault()) return
        if (isVisible) {
            if (visibility == GONE) {
                mSysTime.text = PlayerUtils.getCurrentSystemTime()
                visibility = VISIBLE
                anim?.let { startAnimation(it) }
            }
        } else {
            if (visibility == VISIBLE) {
                visibility = GONE
                anim?.let { startAnimation(it) }
            }
        }
    }

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            VideoView.STATE_IDLE, VideoView.STATE_START_ABORT,
            VideoView.STATE_PREPARING, VideoView.STATE_PREPARED,
            VideoView.STATE_ERROR, VideoView.STATE_PLAYBACK_COMPLETED -> visibility = GONE
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onScreenModeChanged(screenMode: Int) {
        val controller = this.controller
        if (screenMode == VideoView.SCREEN_MODE_FULL) {
            if (controller != null && controller.isShowing && !controller.isLocked) {
                visibility = VISIBLE
                mSysTime.text = PlayerUtils.getCurrentSystemTime()
            }
            mTitle.isSelected = true
        } else {
            visibility = GONE
            mTitle.isSelected = false
        }
        val activity = this.activity
        if (activity != null && controller != null && controller.hasCutout == true) {
            val orientation = activity.requestedOrientation
            val cutoutHeight = controller.cutoutHeight
            when (orientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    mTitleContainer.setPadding(0, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    mTitleContainer.setPadding(cutoutHeight, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    mTitleContainer.setPadding(0, 0, cutoutHeight, 0)
                }
            }
        }
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        if (isLocked) {
            visibility = GONE
        } else {
            visibility = VISIBLE
            mSysTime.text = PlayerUtils.getCurrentSystemTime()
        }
    }

    private class BatteryReceiver(private val pow: ImageView) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras ?: return
            val current = extras.getInt("level") // 获得当前电量
            val total = extras.getInt("scale") // 获得总电量
            val percent = current * 100 / total
            pow.drawable.level = percent
        }
    }

    init {
        visibility = GONE
        if (isTelevisionUiMode()) {
            mBatteryEnabled = false
            layoutInflater.inflate(R.layout.dkplayer_layout_title_view_tv, this)
            //tv模式不要电量，不要返回按钮
            findViewById<ImageView>(R.id.back)?.visibility = GONE
            findViewById<ImageView>(R.id.iv_battery)?.visibility = GONE
        } else {
            mBatteryEnabled = true
            layoutInflater.inflate(R.layout.dkplayer_layout_title_view, this)
            findViewById<ImageView>(R.id.back).setOnClickListener {
                val activity = activity
                if (activity != null && controller?.isFullScreen.orDefault()) {
                    controller?.stopFullScreen()
                }
            }
            //电量
            val batteryLevel = findViewById<ImageView>(R.id.iv_battery)
            mBatteryReceiver = BatteryReceiver(batteryLevel)
        }
        mTitleContainer = findViewById(R.id.title_container)
        mTitle = findViewById(R.id.title)
        mSysTime = findViewById(R.id.sys_time)
    }
}