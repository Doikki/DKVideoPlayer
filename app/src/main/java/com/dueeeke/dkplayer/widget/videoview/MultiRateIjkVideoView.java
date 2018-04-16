package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.dueeeke.dkplayer.interf.MultiRateMediaPlayerControl;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.L;

import java.util.List;

/**
 * 多码率
 * Created by xinyu on 2018/4/16.
 */

public class MultiRateIjkVideoView extends IjkVideoView implements MultiRateMediaPlayerControl{
    private List<MultiRateVideoModel> mMultiRateVideoModels;

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
    public List<MultiRateVideoModel> getMultiRateData() {
        return mMultiRateVideoModels;
    }

    @Override
    public void switchRate(String type) {
        L.d(type);
        for (MultiRateVideoModel item : mMultiRateVideoModels) {
            if (item.type.equals(type) && !item.url.equals(mCurrentUrl)) {
                mCurrentUrl = item.url;
                addDisplay();
                getCurrentPosition();
                startPrepare(true);
                break;
            }
        }
    }

    public void setMultiRateVideos(List<MultiRateVideoModel> videos) {
        this.mMultiRateVideoModels = videos;
        MultiRateVideoModel multiRateVideoModel = videos.get(0);
        if (multiRateVideoModel != null) {
            this.mCurrentUrl = multiRateVideoModel.url;
        }
    }

    public static class MultiRateVideoModel {
        public String type;
        public String url;

        public MultiRateVideoModel(String type, String url) {
            this.type = type;
            this.url = url;
        }
    }
}
