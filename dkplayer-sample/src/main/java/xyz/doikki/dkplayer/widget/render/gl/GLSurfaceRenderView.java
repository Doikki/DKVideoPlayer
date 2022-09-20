/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.doikki.dkplayer.widget.render.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.GlUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import xyz.doikki.videoplayer.AVPlayer;
import xyz.doikki.videoplayer.render.Render;
import xyz.doikki.videoplayer.render.MeasureHelper;

/**
 * {@link GLSurfaceView} that creates a GL context (optionally for protected content) and passes
 * video frames to a {@link VideoProcessor} for drawing to the view.
 *
 * <p>This view must be created programmatically, as it is necessary to specify whether a context
 * supporting protected content should be created at construction time.
 * 参考：https://github.com/google/ExoPlayer/tree/release-v2/demos/gl
 * 请尽量理解此demo的含义，关键代码在 {@link VideoRenderer} 的 onDrawFrame 方法，有问题问Google，不要来找我
 * OpenGL相关资料：
 * https://developer.android.com/guide/topics/graphics/opengl?hl=zh-cn
 * https://github.com/google/grafika
 */
public final class GLSurfaceRenderView extends GLSurfaceView implements Render {

    private final MeasureHelper mMeasureHelper = new MeasureHelper();

    private AVPlayer player;

    @Override
    public void attachToPlayer(@NonNull AVPlayer player) {
        this.player = player;
        setVideoRenderer(new BitmapOverlayVideoProcessor(getContext()), false);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setSurfaceListener(SurfaceListener listener) {
        //todo
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        mMeasureHelper.setScreenScale(scaleType);
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
    public void release() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    /**
     * Processes video frames, provided via a GL texture.
     */
    public interface VideoProcessor {
        /**
         * Performs any required GL initialization.
         */
        void initialize();

        /**
         * Sets the size of the output surface in pixels.
         */
        void setSurfaceSize(int width, int height);

        /**
         * Draws using GL operations.
         *
         * @param frameTexture    The ID of a GL texture containing a video frame.
         * @param transformMatrix The 4 * 4 transform matrix to be applied to the texture.
         */
        void draw(int frameTexture, float[] transformMatrix);

        /**
         * Releases any resources associated with this {@link VideoProcessor}.
         */
        void release();
    }

    private static final int EGL_PROTECTED_CONTENT_EXT = 0x32C0;

    private final Handler mainHandler;

    @Nullable
    private SurfaceTexture surfaceTexture;
    @Nullable
    private Surface surface;

    /**
     * Creates a new instance. Pass {@code true} for {@code requireSecureContext} if the {@link
     * GLSurfaceView GLSurfaceView's} associated GL context should handle secure content (if the
     * device supports it).
     *
     * @param context The {@link Context}.
     */
    @SuppressWarnings("InlinedApi")
    public GLSurfaceRenderView(Context context) {
        super(context);
        mainHandler = new Handler();
    }

    /**
     * @param processor            Processor that draws to the view.
     * @param requireSecureContext Whether a GL context supporting protected content should be
     *                             created, if supported by the device.
     */
    public void setVideoRenderer(VideoProcessor processor, boolean requireSecureContext) {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(
                /* redSize= */ 8,
                /* greenSize= */ 8,
                /* blueSize= */ 8,
                /* alphaSize= */ 8,
                /* depthSize= */ 0,
                /* stencilSize= */ 0);
        setEGLContextFactory(
                new EGLContextFactory() {
                    @Override
                    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
                        int[] glAttributes;
                        if (requireSecureContext) {
                            glAttributes =
                                    new int[]{
                                            EGL14.EGL_CONTEXT_CLIENT_VERSION,
                                            2,
                                            EGL_PROTECTED_CONTENT_EXT,
                                            EGL14.EGL_TRUE,
                                            EGL14.EGL_NONE
                                    };
                        } else {
                            glAttributes = new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
                        }
                        return egl.eglCreateContext(
                                display, eglConfig, /* share_context= */ EGL10.EGL_NO_CONTEXT, glAttributes);
                    }

                    @Override
                    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                        egl.eglDestroyContext(display, context);
                    }
                });
        setEGLWindowSurfaceFactory(
                new EGLWindowSurfaceFactory() {
                    @Override
                    public EGLSurface createWindowSurface(
                            EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                        int[] attribsList =
                                requireSecureContext
                                        ? new int[]{EGL_PROTECTED_CONTENT_EXT, EGL14.EGL_TRUE, EGL10.EGL_NONE}
                                        : new int[]{EGL10.EGL_NONE};
                        return egl.eglCreateWindowSurface(display, config, nativeWindow, attribsList);
                    }

                    @Override
                    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                        egl.eglDestroySurface(display, surface);
                    }
                });
        setRenderer(new VideoRenderer(processor));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Post to make sure we occur in order with any onSurfaceTextureAvailable calls.
        mainHandler.post(
                () -> {
                    if (surface != null) {
                        if (player != null) {
                            player.setSurface(null);
                        }
                        releaseSurface(surfaceTexture, surface);
                        surfaceTexture = null;
                        surface = null;
                    }
                });
    }

    private void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture) {
        mainHandler.post(
                () -> {
                    SurfaceTexture oldSurfaceTexture = this.surfaceTexture;
                    Surface oldSurface = GLSurfaceRenderView.this.surface;
                    this.surfaceTexture = surfaceTexture;
                    this.surface = new Surface(surfaceTexture);
                    releaseSurface(oldSurfaceTexture, oldSurface);
                    if (player != null) {
                        player.setSurface(surface);
                    }
                });
    }

    private static void releaseSurface(
            @Nullable SurfaceTexture oldSurfaceTexture, @Nullable Surface oldSurface) {
        if (oldSurfaceTexture != null) {
            oldSurfaceTexture.release();
        }
        if (oldSurface != null) {
            oldSurface.release();
        }
    }

    private final class VideoRenderer implements Renderer {

        private final VideoProcessor videoProcessor;
        private final AtomicBoolean frameAvailable;
        private final float[] transformMatrix;

        private int texture;
        @Nullable
        private SurfaceTexture surfaceTexture;

        private boolean initialized;
        private int width;
        private int height;

        public VideoRenderer(VideoProcessor videoProcessor) {
            this.videoProcessor = videoProcessor;
            frameAvailable = new AtomicBoolean();
            width = -1;
            height = -1;
            transformMatrix = new float[16];
        }

        @Override
        public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {
            texture = GlUtil.createExternalTexture();
            surfaceTexture = new SurfaceTexture(texture);
            surfaceTexture.setOnFrameAvailableListener(
                    surfaceTexture -> {
                        frameAvailable.set(true);
                        requestRender();
                    });
            onSurfaceTextureAvailable(surfaceTexture);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            initialized = false;
            this.width = width;
            this.height = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (videoProcessor == null) {
                return;
            }

            if (!initialized) {
                videoProcessor.initialize();
                initialized = true;
            }

            if (width != -1 && height != -1) {
                videoProcessor.setSurfaceSize(width, height);
                width = -1;
                height = -1;
            }

            if (frameAvailable.compareAndSet(true, false) && surfaceTexture != null) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(transformMatrix);
            }

            videoProcessor.draw(texture, transformMatrix);
        }
    }
}
