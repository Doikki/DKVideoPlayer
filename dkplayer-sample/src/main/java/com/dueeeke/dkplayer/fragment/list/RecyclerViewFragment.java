package com.dueeeke.dkplayer.fragment.list;

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
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.SimpleOnVideoViewStateChangeListener;
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

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mVideoView = new VideoView(getActivity());
        mVideoView.setEnableParallelPlay(false);
        StandardVideoController controller = new StandardVideoController(getActivity());
        controller.setEnableOrientation(true);
        mVideoView.setVideoController(controller);

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
                    mVideoView.release();
                    removeVideoViewFromParent();
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
        mVideoView.pause();
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
        mVideoView.release();
        removeVideoViewFromParent();

        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());

        View itemView = mLinearLayoutManager.findViewByPosition(position);
        VideoRecyclerViewAdapter.VideoHolder viewHolder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
        mVideoView.setOnVideoViewStateChangeListener(new SimpleOnVideoViewStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                super.onPlayStateChanged(playState);
                switch (playState) {
                    case VideoView.STATE_PREPARING:
                        viewHolder.mStartPlay.setVisibility(View.GONE);
                        viewHolder.mLoading.setVisibility(View.VISIBLE);
                        break;
                    case VideoView.STATE_PLAYING:
                        viewHolder.mStartPlay.setVisibility(View.GONE);
                        viewHolder.mLoading.setVisibility(View.GONE);
                        viewHolder.mThumb.setVisibility(View.GONE);
                        break;
                    case VideoView.STATE_IDLE:
                        viewHolder.mLoading.setVisibility(View.GONE);
                        viewHolder.mStartPlay.setVisibility(View.VISIBLE);
                        viewHolder.mThumb.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        mVideoView.start();
        mCurPosition = position;
    }

    private void removeVideoViewFromParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
            mCurPosition = -1;
        }
    }
}
