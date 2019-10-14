package com.dueeeke.dkplayer.activity.list;

import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.videoplayer.player.VideoViewManager;

public class BaseListActivity extends BaseActivity {

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.instance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoViewManager.instance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoViewManager.instance().release();
    }

    @Override
    public void onBackPressed() {
        if (!VideoViewManager.instance().onBackPressed()){
            super.onBackPressed();
        }
    }
}
