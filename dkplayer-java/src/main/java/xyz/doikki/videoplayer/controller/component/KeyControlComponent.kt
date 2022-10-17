package xyz.doikki.videoplayer.controller.component

import android.view.KeyEvent

interface KeyControlComponent : GestureControlComponent {

    /**
     * 开始按住左右方向键拖动位置
     */
    fun onStartLeftOrRightKeyPressedForSeeking(event: KeyEvent){}

    /**
     * 停止按住左右方向键拖动位置
     */
    fun onStopLeftOrRightKeyPressedForSeeking(event: KeyEvent){}

    /**
     * 取消方向键拖动位置
     */
    fun onCancelLeftOrRightKeyPressedForSeeking(keyEvent: KeyEvent){}
}