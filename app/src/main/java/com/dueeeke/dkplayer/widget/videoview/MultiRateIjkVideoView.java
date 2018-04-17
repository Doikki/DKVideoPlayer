package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.interf.MultiRateMediaPlayerControl;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.L;

import java.util.LinkedHashMap;

/**
 * 多码率
 * Created by xinyu on 2018/4/16.
 */

public class MultiRateIjkVideoView extends IjkVideoView implements MultiRateMediaPlayerControl{
    private LinkedHashMap<String, String> mMultiRateVideoModels;

    public MultiRateIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public MultiRateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiRateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LinkedHashMap<String, String> getMultiRateData() {
        return mMultiRateVideoModels;
    }

    @Override
    public void switchRate(String type) {
        L.d(type);
        String url = mMultiRateVideoModels.get(type);
        if (url.equals(mCurrentUrl)) return;
        mCurrentUrl = url;
        addDisplay();
        getCurrentPosition();
        startPrepare(true);
    }

    public void setMultiRateVideos(LinkedHashMap<String, String> videos) {
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
