package xyz.doikki.videoplayer.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 可播放在线和本地url
 * Created by Doikki on 2022/7/18.
 */
public class VideoView extends BaseVideoView<AbstractPlayer> {
    public VideoView(@NonNull Context context) {
        super(context);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
