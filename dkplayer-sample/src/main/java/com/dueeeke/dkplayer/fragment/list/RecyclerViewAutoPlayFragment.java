package com.dueeeke.dkplayer.fragment.list;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class RecyclerViewAutoPlayFragment extends BaseFragment {

    private List<VideoBean> mVideos = new ArrayList<>();
    private VideoRecyclerViewAdapter mVideoRecyclerViewAdapter;
    private RecyclerView mRecyclerView;

    /**
     * 当前正在播放的VideoView
     */
    private VideoView mCurrentVideoView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mRecyclerView = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideos);
        mRecyclerView.setAdapter(mVideoRecyclerViewAdapter);

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                VideoView videoView = view.findViewById(R.id.video_player);
                if (videoView != null && !videoView.isFullScreen()) {
                    videoView.release();
                }
            }
        });

        View add = findViewById(R.id.add);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoRecyclerViewAdapter.addData(DataUtil.getVideoList());
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem, visibleCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        autoPlayVideo(recyclerView);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;//记录可视区域item个数
            }

            private void autoPlayVideo(RecyclerView view) {
                //循环遍历可视区域videoview,如果完全可见就开始播放
                for (int i = 0; i < visibleCount; i++) {
                    if (view == null || view.getChildAt(i) == null) continue;
                    VideoView videoView = view.getChildAt(i).findViewById(R.id.video_player);
                    if (videoView != null) {
                        Rect rect = new Rect();
                        videoView.getLocalVisibleRect(rect);
                        int videoHeight = videoView.getHeight();
                        if (rect.top == 0 && rect.bottom == videoHeight) {
                            videoView.start();
                            mCurrentVideoView = videoView;
                            break;
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        List<VideoBean> videoList = DataUtil.getVideoList();
        mVideos.addAll(videoList);
        mVideoRecyclerViewAdapter.notifyDataSetChanged();

        mRecyclerView.post(() -> {
            //自动播放第一个
            View view = mRecyclerView.getChildAt(0);
            VideoView videoView = view.findViewById(R.id.video_player);
            videoView.start();
            mCurrentVideoView = videoView;
        });
    }

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurrentVideoView != null) {
            mCurrentVideoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentVideoView != null) {
            mCurrentVideoView.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCurrentVideoView != null) {
            mCurrentVideoView.release();
        }
    }
}
