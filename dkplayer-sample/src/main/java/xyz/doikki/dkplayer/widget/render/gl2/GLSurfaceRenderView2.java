package xyz.doikki.dkplayer.widget.render.gl2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import xyz.doikki.dkplayer.widget.render.gl2.chooser.GLConfigChooser;
import xyz.doikki.dkplayer.widget.render.gl2.contextfactory.GLContextFactory;
import xyz.doikki.dkplayer.widget.render.gl2.filter.GlFilter;
import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.render.MeasureHelper;
import xyz.doikki.videoplayer.render.Render;

public class GLSurfaceRenderView2 extends GLSurfaceView implements Render {

    private final GLVideoRenderer renderer;

    public GLSurfaceRenderView2(Context context) {
        this(context, null);
    }

    public GLSurfaceRenderView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextFactory(new GLContextFactory());
        setEGLConfigChooser(new GLConfigChooser());
        renderer = new GLVideoRenderer(this);
        setRenderer(renderer);
    }

    private final MeasureHelper mMeasureHelper = new MeasureHelper();

    @Override
    public void attachToPlayer(@NonNull AVPlayer player) {
        this.renderer.setPlayer(player);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }


    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotationDegree(degree);
        setRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        mMeasureHelper.setAspectRatioType(scaleType);
        requestLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void screenshot(boolean highQuality, @NonNull ScreenShotCallback callback) {
        //todo glsurface 是可以截图的，待处理
        callback.onScreenShotResult(null);
    }

    @Override
    public void setSurfaceListener(SurfaceListener listener) {
        //todo
    }

    @Override
    public void release() {

    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }
}
