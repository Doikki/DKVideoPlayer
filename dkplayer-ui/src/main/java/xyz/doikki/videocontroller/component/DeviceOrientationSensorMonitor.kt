package xyz.doikki.videocontroller.component

import android.content.Context
import xyz.doikki.videoplayer.DKVideoView
import xyz.doikki.videoplayer.util.PlayerUtils

/**
 * 重力感应进入和退出全屏，需要此功能时将此ControlComponent添加到controller中即可
 * Created by Doikki on 2022/10/17.
 */
class DeviceOrientationSensorMonitor(context: Context) : BaseControlComponent(context),
    DeviceOrientationSensorHelper.DeviceOrientationChangedListener {

    private val orientationSensorHelper by lazy {
        DeviceOrientationSensorHelper(
            context.applicationContext, PlayerUtils.scanForActivity(context)
        ).also {
            //开始监听设备方向
            it.setDeviceOrientationChangedListener(this)
        }
    }

    var enableOrientationSensor = true


    override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        when(playState) {
            DKVideoView.STATE_IDLE -> {
                orientationSensorHelper.disable()
            }
        }
    }

    override fun onScreenModeChanged(screenMode: Int) {
        super.onScreenModeChanged(screenMode)
        //修改传感器
        when (screenMode) {
            DKVideoView.SCREEN_MODE_NORMAL -> {
                if (enableOrientationSensor) {
                    orientationSensorHelper.enable()
                } else {
                    orientationSensorHelper.disable()
                }
            }
            DKVideoView.SCREEN_MODE_FULL -> {
                //在全屏时强制监听设备方向
                orientationSensorHelper.enable()
            }
            DKVideoView.SCREEN_MODE_TINY -> orientationSensorHelper.disable()
        }
    }


    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        val player = player ?: return
        if (player.isPlaying && (enableOrientationSensor || player.isFullScreen)) {
            if (hasWindowFocus) {
                postDelayed({ orientationSensorHelper.enable() }, 800)
            } else {
                orientationSensorHelper.disable()
            }
        }
    }

    override fun onDeviceDirectionChanged(@DeviceOrientationSensorHelper.DeviceDirection direction: Int) {
        val controller = controller ?: return
        when (direction) {
            DeviceOrientationSensorHelper.DEVICE_DIRECTION_PORTRAIT -> {
                //切换为竖屏
                //屏幕锁定的情况
                if (controller.isLocked) return
                //没有开启设备方向监听的情况
                if (!enableOrientationSensor) return
                controller.stopFullScreen()
            }
            DeviceOrientationSensorHelper.DEVICE_DIRECTION_LANDSCAPE -> {
                controller.startFullScreen()
            }
            DeviceOrientationSensorHelper.DEVICE_DIRECTION_LANDSCAPE_REVERSED -> {
                controller.startFullScreen(true)
            }
            DeviceOrientationSensorHelper.DEVICE_DIRECTION_UNKNOWN -> {
            }
        }
    }
}