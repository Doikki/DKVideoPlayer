package xyz.doikki.dkplayer.activity.extend;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.bean.VideoBean;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.widget.component.PlayerMonitor;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.VideoView;

import java.util.List;

/**
 * 连续播放一个列表
 * Created by Doikki on 2017/4/7.
 */

public class PlayListActivity extends BaseActivity {

    private List<VideoBean> data = DataUtil.getVideoList();

    private StandardVideoController mController;
    private TitleView mTitleView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_layout_common;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_play_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);
        mController = new StandardVideoController(this);
        addControlComponents();
        mController.addControlComponent(new PlayerMonitor());

        //加载第一条数据
        VideoBean videoBean = data.get(0);
        mVideoView.setDataSource(videoBean.getUrl());
        mTitleView.setTitle(videoBean.getTitle());
        mVideoView.setVideoController(mController);

        //监听播放结束
        mVideoView.addOnStateChangeListener(new VideoView.OnStateChangeListener() {
            private int mCurrentVideoPosition;
            @Override
            public void onPlayerStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (data != null) {
                        mCurrentVideoPosition++;
                        if (mCurrentVideoPosition >= data.size()) return;
                        mVideoView.release();
                        //重新设置数据
                        VideoBean videoBean = data.get(mCurrentVideoPosition);
                        mVideoView.setDataSource(videoBean.getUrl());
                        mTitleView.setTitle(videoBean.getTitle());
                        mVideoView.setVideoController(mController);
                        //开始播放
                        mVideoView.start();
                    }
                }
            }
        });

        mVideoView.start();
    }

    private void addControlComponents() {
        CompleteView completeView = new CompleteView(this);
        ErrorView errorView = new ErrorView(this);
        PrepareView prepareView = new PrepareView(this);
        prepareView.setClickStart();
        mTitleView = new TitleView(this);
        VodControlView vodControlView = new VodControlView(this);
        GestureView gestureView = new GestureView(this);
        mController.addControlComponent(completeView, errorView, prepareView, mTitleView, vodControlView, gestureView);
    }
}
