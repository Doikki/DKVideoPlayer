package xyz.doikki.dkplayer.widget.render.gl2.chooser;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_RENDERABLE_TYPE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;

import android.opengl.GLSurfaceView;
import android.os.Build;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


public class GLConfigChooser implements GLSurfaceView.EGLConfigChooser {

    private final int[] configSpec;
    private final int redSize;
    private final int greenSize;
    private final int blueSize;
    private final int alphaSize;
    private final int depthSize;
    private final int stencilSize;

    private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

    private static final boolean USE_RGB_888 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    public GLConfigChooser() {
        this(
                USE_RGB_888 ? 8 : 5,
                USE_RGB_888 ? 8 : 6,
                USE_RGB_888 ? 8 : 5,
                0,
                0,
                0,
                EGL_CONTEXT_CLIENT_VERSION
        );
    }

    public GLConfigChooser(
            final int redSize,
            final int greenSize,
            final int blueSize,
            final int alphaSize,
            final int depthSize,
            final int stencilSize,
            final int version) {
        configSpec = filterConfigSpec(new int[]{
                EGL_RED_SIZE, redSize,
                EGL_GREEN_SIZE, greenSize,
                EGL_BLUE_SIZE, blueSize,
                EGL_ALPHA_SIZE, alphaSize,
                EGL_DEPTH_SIZE, depthSize,
                EGL_STENCIL_SIZE, stencilSize,
                EGL_NONE
        }, version);
        this.redSize = redSize;
        this.greenSize = greenSize;
        this.blueSize = blueSize;
        this.alphaSize = alphaSize;
        this.depthSize = depthSize;
        this.stencilSize = stencilSize;
    }

    private static final int EGL_OPENGL_ES2_BIT = 4;

    private int[] filterConfigSpec(final int[] configSpec, final int version) {
        if (version != 2) {
            return configSpec;
        }

        final int len = configSpec.length;
        final int[] newConfigSpec = new int[len + 2];
        System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
        newConfigSpec[len - 1] = EGL_RENDERABLE_TYPE;
        newConfigSpec[len] = EGL_OPENGL_ES2_BIT;
        newConfigSpec[len + 1] = EGL_NONE;
        return newConfigSpec;
    }

    //////////////////////////////////////////////////////////////////////////

    @Override
    public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display) {
        final int[] num_config = new int[1];
        if (!egl.eglChooseConfig(display, configSpec, null, 0, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        final int config_size = num_config[0];
        if (config_size <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }

        final EGLConfig[] configs = new EGLConfig[config_size];
        if (!egl.eglChooseConfig(display, configSpec, configs, config_size, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        final EGLConfig config = chooseConfig(egl, display, configs);
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }

    private EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display, final EGLConfig[] configs) {
        for (final EGLConfig config : configs) {
            final int d = findConfigAttrib(egl, display, config, EGL_DEPTH_SIZE, 0);
            final int s = findConfigAttrib(egl, display, config, EGL_STENCIL_SIZE, 0);
            if ((d >= depthSize) && (s >= stencilSize)) {
                final int r = findConfigAttrib(egl, display, config, EGL_RED_SIZE, 0);
                final int g = findConfigAttrib(egl, display, config, EGL_GREEN_SIZE, 0);
                final int b = findConfigAttrib(egl, display, config, EGL_BLUE_SIZE, 0);
                final int a = findConfigAttrib(egl, display, config, EGL_ALPHA_SIZE, 0);
                if ((r == redSize) && (g == greenSize) && (b == blueSize) && (a == alphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private int findConfigAttrib(final EGL10 egl, final EGLDisplay display, final EGLConfig config, final int attribute, final int defaultValue) {
        final int[] value = new int[1];
        if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
            return value[0];
        }
        return defaultValue;
    }
}
