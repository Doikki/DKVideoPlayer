package com.dueeeke.dkplayer.fragment.list;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.interf.OnItemChildClickListener;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videocontroller.CompleteView;
import com.dueeeke.videocontroller.ErrorView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends BaseFragment implements OnItemChildClickListener {

    protected List<VideoBean> mVideos = new ArrayList<>();
    protected VideoRecyclerViewAdapter mVideoRecyclerViewAdapter;

    protected VideoView mVideoView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerView mRecyclerView;

    private int mCurPosition = -1;
    protected StandardVideoController mController;
    protected ErrorView mErrorView;
    protected CompleteView mCompleteView;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        super.initViews();

        initVideoView();

        mRecyclerView = findViewById(R.id.rv);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideos);
        mVideoRecyclerViewAdapter.setOnItemChildClickListener(this);
        mRecyclerView.setAdapter(mVideoRecyclerViewAdapter);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                FrameLayout playerContainer = view.findViewById(R.id.player_container);
                View v = playerContainer.getChildAt(0);
                if (v != null && v == mVideoView && !mVideoView.isFullScreen()) {
                    resetVideoView();
                }
            }
        });

        View view = findViewById(R.id.add);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoRecyclerViewAdapter.addData(DataUtil.getVideoList());
            }
        });
    }

    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mController = new StandardVideoController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    protected void initData() {
        super.initData();
        List<VideoBean> videoList = DataUtil.getVideoList();
        mVideos.addAll(videoList);
        mVideoRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        resetVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }

    @Override
    public void onItemChildClick(View view, int position) {
        startPlay(position);
    }

    protected void startPlay(int position) {
        if (mCurPosition == position) return;
        if (mCurPosition != -1) {
            resetVideoView();
        }

        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());

        View itemView = mLinearLayoutManager.findViewByPosition(position);
        VideoRecyclerViewAdapter.VideoHolder viewHolder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
        int count = viewHolder.mPlayerContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewHolder.mPlayerContainer.getChildAt(i);
            if (v instanceof IControlComponent) {
                mController.addControlComponent((IControlComponent) v, true);
            }
        }
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        mVideoView.start();
        mCurPosition = position;
    }

    private void resetVideoView() {
        mVideoView.release();
        mVideoView.stopFullScreen();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        removeVideoViewFromParent();
    }

    private void removeVideoViewFromParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
            mCurPosition = -1;
        }
    }
}
