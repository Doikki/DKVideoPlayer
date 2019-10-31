package com.dueeeke.dkplayer.fragment.list;

import android.view.View;

import com.dueeeke.dkplayer.widget.controller.RotateInFullscreenController;
import com.dueeeke.videocontroller.CompleteView;
import com.dueeeke.videocontroller.ErrorView;
import com.dueeeke.videoplayer.player.VideoView;

public class RecyclerViewRotateFragment extends RecyclerViewAutoPlayFragment {

    @Override
    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mController = new RotateInFullscreenController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    public void onItemChildClick(View view, int position) {
        super.onItemChildClick(view, position);
        mVideoView.startFullScreen();
    }
}
