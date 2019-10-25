package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.interf.DefinitionMediaPlayerControl;
import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.LinkedHashMap;

/**
 * 清晰度切换
 * Created by xinyu on 2018/4/16.
 */

public class DefinitionVideoView<P extends AbstractPlayer> extends VideoView<P> implements DefinitionMediaPlayerControl {
    private LinkedHashMap<String, String> mDefinitionMap;
    private String mCurrentDefinition;

    public DefinitionVideoView(@NonNull Context context) {
        super(context);
    }

    public DefinitionVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefinitionVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LinkedHashMap<String, String> getDefinitionData() {
        return mDefinitionMap;
    }

    @Override
    public void switchDefinition(String definition) {
        String url = mDefinitionMap.get(definition);
        if (definition.equals(mCurrentDefinition)) return;
        mUrl = url;
        getCurrentPosition();
        startPrepare(true);
        mCurrentDefinition = definition;
    }

    public void setDefinitionVideos(LinkedHashMap<String, String> videos) {
        this.mDefinitionMap = videos;
        this.mUrl = getValueFromLinkedMap(videos, 0);
    }

    public static String getValueFromLinkedMap(LinkedHashMap<String, String> map, int index) {
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }

}
