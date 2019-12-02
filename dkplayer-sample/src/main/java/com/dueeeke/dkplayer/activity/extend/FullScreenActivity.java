package com.dueeeke.dkplayer.activity.extend;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 全屏播放
 * Created by dueeeke on 2017/4/21.
 */

public class FullScreenActivity extends BaseActivity<VideoView> {

    private StandardVideoController mController;

    @Override
    protected View getContentView() {
        mVideoView = new VideoView(this);
        adaptCutoutAboveAndroidP();
        return mVideoView;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_fullscreen_directly;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView.startFullScreen();
        mVideoView.setUrl(DataUtil.SAMPLE_URL);
        mController = new StandardVideoController(this);
        mController.addControlComponent(new CompleteView(this));
        mController.addControlComponent(new ErrorView(this));
        mController.addControlComponent(new PrepareView(this));

        TitleView titleView = new TitleView(this);
        // 我这里改变了返回按钮的逻辑，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        titleView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setTitle(getString(R.string.str_fullscreen_directly));
        mController.addControlComponent(titleView);
        VodControlView vodControlView = new VodControlView(this);
        // 我这里隐藏了全屏按钮并且调整了边距，我不推荐这样做，我这样只是为了方便，
        // 如果你想对某个组件进行定制，直接将该组件的代码复制一份，改成你想要的样子
        vodControlView.findViewById(R.id.fullscreen).setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vodControlView.findViewById(R.id.total_time).getLayoutParams();
        lp.rightMargin = PlayerUtils.dp2px(this, 16);
        mController.addControlComponent(vodControlView);
        mController.addControlComponent(new GestureView(this));
        mVideoView.setVideoController(mController);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
        mVideoView.start();
    }

    private void adaptCutoutAboveAndroidP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }


    @Override
    public void onBackPressed() {
        if (!mController.isLocked()) {
            finish();
        }
    }
}
