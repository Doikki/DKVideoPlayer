package xyz.doikki.videoplayer;

public abstract class AbstractAVPlayer implements AVPlayer{

    /**
     * 播放器事件回调
     */
    protected EventListener eventListener;

    @Override
    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
