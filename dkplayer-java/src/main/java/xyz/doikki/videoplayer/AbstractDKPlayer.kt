package xyz.doikki.videoplayer

abstract class AbstractDKPlayer : DKPlayer {

    /**
     * 播放器事件回调
     */
    protected var eventListener: DKPlayer.EventListener? = null
        private set

    override fun setEventListener(eventListener: DKPlayer.EventListener?) {
        this.eventListener = eventListener
    }

}