package xyz.doikki.videoplayer.player

abstract class AbstractPlayer : IPlayer {

    /**
     * 播放器事件回调
     */
    protected var eventListener: IPlayer.EventListener? = null
        private set

    override fun setEventListener(eventListener: IPlayer.EventListener?) {
        this.eventListener = eventListener
    }

}