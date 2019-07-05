package com.dueeeke.dkplayer.util;

import android.text.TextUtils;

import com.dueeeke.videoplayer.player.ProgressManager;

import java.util.LinkedHashMap;

public class ProgressManagerImpl extends ProgressManager {

    private static LinkedHashMap<Integer, Long> progressMap = new LinkedHashMap<>();

    @Override
    public void saveProgress(String url, long progress) {
        if (TextUtils.isEmpty(url)) return;
        if (progress == 0) {
            clearSavedProgressByUrl(url);
            return;
        }
        progressMap.put(url.hashCode(), progress);
    }

    @Override
    public long getSavedProgress(String url) {
        return TextUtils.isEmpty(url) ? 0 : progressMap.containsKey(url.hashCode()) ? progressMap.get(url.hashCode()) : 0;
    }

    public void clearAllSavedProgress() {
        progressMap.clear();
    }

    public void clearSavedProgressByUrl(String url) {
        progressMap.remove(url.hashCode());
    }
}
