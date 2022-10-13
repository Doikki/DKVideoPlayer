package xyz.doikki.videoplayer.controller.component

import android.view.KeyEvent

interface KeyControlComponent : GestureControlComponent {

    fun onStartLeftOrRightKeyPressed(event: KeyEvent)

    fun onStopLeftOrRightKeyPressed(event: KeyEvent)

    fun onCancelLeftOrRightKeyPressed(keyEvent: KeyEvent)
}