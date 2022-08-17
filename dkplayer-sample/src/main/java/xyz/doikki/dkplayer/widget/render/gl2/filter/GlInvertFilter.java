package xyz.doikki.dkplayer.widget.render.gl2.filter;



public class GlInvertFilter extends GlFilter {
    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform lowp sampler2D sTexture;" +
                    "void main() {" +
                    "lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
                    "gl_FragColor = vec4((1.0 - color.rgb), color.w);" +
                    "}";

    public GlInvertFilter() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }
}
