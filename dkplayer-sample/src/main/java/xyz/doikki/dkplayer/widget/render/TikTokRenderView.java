package xyz.doikki.dkplayer.widget.render;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.player.AbstractPlayer;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.render.IRenderView;

/**
 * TikTok专用RenderView，横屏视频默认显示，竖屏视频居中裁剪
 * 使用代理模式实现
 */
public class TikTokRenderView implements IRenderView {

    private final IRenderView mProxyRenderView;

    TikTokRenderView(@NonNull IRenderView renderView) {
        this.mProxyRenderView = renderView;
    }

    @Override
    public void attachToPlayer(@NonNull AbstractPlayer player) {
        mProxyRenderView.attachToPlayer(player);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mProxyRenderView.setVideoSize(videoWidth, videoHeight);
            if (videoHeight > videoWidth) {
                //竖屏视频，使用居中裁剪
                mProxyRenderView.setScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
            } else {
                //横屏视频，使用默认模式
                mProxyRenderView.setScaleType(VideoView.SCREEN_SCALE_DEFAULT);
            }
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mProxyRenderView.setVideoRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        // 置空，不要让外部去设置ScaleType
    }

    @Override
    public View getView() {
        return mProxyRenderView.getView();
    }

    @Override
    public Bitmap doScreenShot() {
        return mProxyRenderView.doScreenShot();
    }

    @Override
    public void release() {
        mProxyRenderView.release();
    }
}