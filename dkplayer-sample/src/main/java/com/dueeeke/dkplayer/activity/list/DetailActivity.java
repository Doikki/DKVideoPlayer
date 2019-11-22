package com.dueeeke.dkplayer.activity.list;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.Tag;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

public class DetailActivity extends BaseActivity<VideoView> {

    private boolean isStartTransition;
    @Override
    protected int getTitleResId() {
        return R.string.str_seamless_play;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            isStartTransition = true;
        }
        return true;
    }

    @Override
    protected void initView() {
        super.initView();
        FrameLayout playerContainer = findViewById(R.id.player_container);
        ViewCompat.setTransitionName(playerContainer, "player_container");
        ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                if (isStartTransition) return;

                //注意以下过程需在共享元素动画结束后执行

                //拿到VideoView实例
                mVideoView = getVideoViewManager().get(Tag.SEAMLESS);
                //如果已经添加到某个父容器，就将其移除
                Utils.removeViewFormParent(mVideoView);
                //把播放器添加到页面的容器中
                playerContainer.addView(mVideoView);
                //设置新的控制器
                StandardVideoController controller = new StandardVideoController(DetailActivity.this);
                mVideoView.setVideoController(controller);

                Intent intent = getIntent();
                boolean seamlessPlay = intent.getBooleanExtra(IntentKeys.SEAMLESS_PLAY, false);
                String title = intent.getStringExtra(IntentKeys.TITLE);
                controller.addDefaultControlComponent(title, false);
                if (seamlessPlay) {
                    //无缝播放需还原Controller状态
                    controller.setPlayState(mVideoView.getCurrentPlayState());
                    controller.setPlayerState(mVideoView.getCurrentPlayerState());
                } else {
                    //不是无缝播放的情况
                    String url = intent.getStringExtra(IntentKeys.URL);
                    mVideoView.setUrl(url);
                    mVideoView.start();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //移除Controller
            mVideoView.setVideoController(null);
            mVideoView = null;
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mVideoView == null || !mVideoView.onBackPressed()) {
            supportFinishAfterTransition();
            isStartTransition = true;
        }
    }
}
