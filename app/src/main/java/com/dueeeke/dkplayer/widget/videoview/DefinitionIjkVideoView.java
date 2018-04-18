package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.interf.DefinitionMediaPlayerControl;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.LinkedHashMap;

/**
 * 清晰度切换
 * Created by xinyu on 2018/4/16.
 */

public class DefinitionIjkVideoView extends IjkVideoView implements DefinitionMediaPlayerControl {
    private LinkedHashMap<String, String> mMultiRateVideoModels;
    private String mCurrentDefinition;

    public DefinitionIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public DefinitionIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefinitionIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LinkedHashMap<String, String> getDefinitionData() {
        return mMultiRateVideoModels;
    }

    @Override
    public void switchDefinition(String definition) {
        String url = mMultiRateVideoModels.get(definition);
        if (definition.equals(mCurrentDefinition)) return;
        mCurrentUrl = url;
        addDisplay();
        getCurrentPosition();
        startPrepare(true);
        mCurrentDefinition = definition;
    }

    public void setDefinitionVideos(LinkedHashMap<String, String> videos) {
        this.mMultiRateVideoModels = videos;
        this.mCurrentUrl = getValueFromLinkedMap(videos, 0);
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
