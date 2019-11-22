package com.dueeeke.dkplayer.fragment.list;

import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.dkplayer.widget.controller.PortraitWhenFullScreenController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videoplayer.listener.SimpleOnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 全屏后手动横屏，并不完美，仅做参考
 */
public class RecyclerViewPortraitFragment extends RecyclerViewAutoPlayFragment {

    @Override
    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mVideoView.setOnVideoViewStateChangeListener(new SimpleOnVideoViewStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mLastPos = mCurPos;
                    mCurPos = -1;
                }
            }
        });
        mController = new PortraitWhenFullScreenController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new GestureView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    public void onItemChildClick(int position) {
        mVideoView.startFullScreen();
        super.onItemChildClick(position);
    }
}
