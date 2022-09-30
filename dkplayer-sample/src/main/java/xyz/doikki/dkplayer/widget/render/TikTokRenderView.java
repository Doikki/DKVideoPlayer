package xyz.doikki.dkplayer.widget.render;

import android.view.View;

import androidx.annotation.NonNull;

import xyz.doikki.videoplayer.DKPlayer;
import xyz.doikki.videoplayer.render.AspectRatioType;
import xyz.doikki.videoplayer.render.Render;

/**
 * TikTok专用RenderView，横屏视频默认显示，竖屏视频居中裁剪
 * 使用代理模式实现
 */
public class TikTokRenderView implements Render {

    private final Render mProxyRenderView;

    TikTokRenderView(@NonNull Render renderView) {
        this.mProxyRenderView = renderView;
    }

    @Override
    public void attachPlayer(@NonNull DKPlayer player) {
        mProxyRenderView.attachPlayer(player);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mProxyRenderView.setVideoSize(videoWidth, videoHeight);
            if (videoHeight > videoWidth) {
                //竖屏视频，使用居中裁剪
                mProxyRenderView.setAspectRatioType(AspectRatioType.CENTER_CROP);
            } else {
                //横屏视频，使用默认模式
                mProxyRenderView.setAspectRatioType(AspectRatioType.SCALE);
            }
        }
    }

    @Override
    public void setSurfaceListener(SurfaceListener listener) {
        mProxyRenderView.setSurfaceListener(listener);
    }

    @Override
    public void setVideoRotation(int degree) {
        mProxyRenderView.setVideoRotation(degree);
    }

    @Override
    public void setAspectRatioType(int aspectRatioType) {
        // 置空，不要让外部去设置ScaleType
    }

    @Override
    public View getView() {
        return mProxyRenderView.getView();
    }

    @Override
    public void screenshot(boolean highQuality, @NonNull ScreenShotCallback callback) {
        mProxyRenderView.screenshot(highQuality, callback);
    }

    @Override
    public void release() {
        mProxyRenderView.release();
    }

}