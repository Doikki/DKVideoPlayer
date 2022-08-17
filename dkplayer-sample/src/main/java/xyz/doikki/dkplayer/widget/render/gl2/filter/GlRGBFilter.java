package xyz.doikki.dkplayer.widget.render.gl2.filter;

import android.opengl.GLES20;

/**
 * Adjusts the individual RGB channels of an image
 * red: Normalized values by which each color channel is multiplied. The range is from 0.0 up, with 1.0 as the default.
 * green:
 * blue:
 */
public class GlRGBFilter extends GlFilter {

    private static final String RGB_FRAGMENT_SHADER = "" +
            "precision mediump float;" +
            " varying vec2 vTextureCoord;\n" +
            "  \n" +
            " uniform lowp sampler2D sTexture;\n" +
            "  uniform highp float red;\n" +
            "  uniform highp float green;\n" +
            "  uniform highp float blue;\n" +
            "  uniform lowp float brightness;\n" +
            "  uniform lowp float saturation;\n" +
            "  uniform lowp float contrast;\n" +
            "  \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            "  void main()\n" +
            "  {\n" +
            "      highp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "      lowp vec4 textureOtherColor = texture2D(sTexture, vTextureCoord);\n" +
            "      lowp float luminance = dot(textureOtherColor.rgb, luminanceWeighting);\n" +
            "      lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.r * red, textureColor.g * green, textureColor.b * blue, 1.0);\n" +
            "      gl_FragColor = vec4((textureOtherColor.rgb + vec3(brightness)), textureOtherColor.w);\n" +
            "      gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureOtherColor.w);\n" +
            "      gl_FragColor = vec4(((textureOtherColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureOtherColor.w);\n" +
            "  }\n";

    public GlRGBFilter() {
        super(DEFAULT_VERTEX_SHADER, RGB_FRAGMENT_SHADER);
    }

    private float red = 1f;
    private float green = 1f;
    private float blue = 1f;
    private float brightness = 0f;
    private float saturation = 1f;
    private float contrast = 1.2f;

    public void setRed(float red) {
        this.red = red;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("red"), red);
        GLES20.glUniform1f(getHandle("green"), green);
        GLES20.glUniform1f(getHandle("blue"), blue);
        GLES20.glUniform1f(getHandle("brightness"), brightness);
        GLES20.glUniform1f(getHandle("saturation"), saturation);
        GLES20.glUniform1f(getHandle("contrast"), contrast);
    }
}
