package com.dueeeke.dkplayer.activity.list;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.SeamlessRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.SeamlessPlayHelper;
import com.dueeeke.dkplayer.widget.controller.SeamlessController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

/**
 * 无缝播放, 模仿哔哩哔哩
 */

public class SeamlessPlayActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private List<VideoBean> mVideoList;
    private boolean mSkipToDetail;
    private SeamlessController mSeamlessController;
    private int mCurrentPlayPosition = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("无缝播放");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        mVideoView = SeamlessPlayHelper.getInstance().getVideoView();
        mSeamlessController = new SeamlessController(this);
        mVideoView.setVideoController(mSeamlessController);

        RecyclerView recyclerView = findViewById(R.id.rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mVideoList = DataUtil.getVideoList();
        SeamlessRecyclerViewAdapter seamlessRecyclerViewAdapter = new SeamlessRecyclerViewAdapter(mVideoList, this);
        recyclerView.setAdapter(seamlessRecyclerViewAdapter);
        seamlessRecyclerViewAdapter.setOnItemClickListener(position -> {

            mSkipToDetail = true;
            //移除Controller
            mVideoView.setVideoController(null);
            //重置Controller
            mSeamlessController.resetController();
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle bundle = new Bundle();

            if (mCurrentPlayPosition == position) {
                //需要无缝播放
                bundle.putBoolean(IntentKeys.SEAMLESS_PLAY, true);
            } else {
                //无需无缝播放，把相应数据传到详情页
                mVideoView.release();
                VideoBean videoBean = mVideoList.get(position);
                bundle.putBoolean(IntentKeys.SEAMLESS_PLAY, false);
                bundle.putString(IntentKeys.URL, videoBean.getUrl());
                bundle.putString(IntentKeys.TITLE, videoBean.getTitle());
            }
            intent.putExtras(bundle);
            startActivity(intent, bundle);
            //重置当前播放位置
            mCurrentPlayPosition = -1;
        });


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                FrameLayout playerContainer = view.findViewById(R.id.player_container);
                VideoView videoView = view.findViewById(R.id.video_player);
                if (videoView != null && !videoView.isFullScreen()) {
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: called");
//                    int tag = (int) videoView.getTag();
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: position: " + tag);

                    if (playerContainer != null) {
                        playerContainer.removeView(videoView);
                    }

                    videoView.release();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem, visibleCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE: //滚动停止
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
                //循环遍历可视区域playerContainer,如果完全可见就把播放器添加到里面，并开始播放
                for (int i = 0; i < visibleCount; i++) {
                    View itemView = view.getChildAt(i);
                    if (itemView == null) continue;
                    FrameLayout playerContainer = itemView.findViewById(R.id.player_container);
                    Rect rect = new Rect();
                    playerContainer.getLocalVisibleRect(rect);
                    int videoHeight = playerContainer.getHeight();
                    if (rect.top == 0 && rect.bottom == videoHeight) {
                        int position = (int) itemView.getTag();
                        if (mCurrentPlayPosition == position) break;
                        removePlayerFormParent();
                        mVideoView.release();
                        VideoBean videoBean = mVideoList.get(position);
                        mVideoView.setUrl(videoBean.getUrl());
                        mSeamlessController.resetController();
                        mVideoView.setVideoController(mSeamlessController);
                        mVideoView.start();
                        playerContainer.addView(mVideoView);
                        mCurrentPlayPosition = position;
                        break;
                    }
                }
            }
        });

        recyclerView.post(() -> {
            //自动播放第一个
            VideoBean videoBean = mVideoList.get(0);
            mVideoView.setUrl(videoBean.getUrl());
            mVideoView.start();
            mCurrentPlayPosition = 0;

            View view = recyclerView.getChildAt(0);
            FrameLayout playerContainer = view.findViewById(R.id.player_container);
            playerContainer.addView(mVideoView);
        });

    }

    /**
     * 将播放器从父控件中移除
     */
    private void removePlayerFormParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(mVideoView);
        }
    }

    private OnVideoViewStateChangeListener mOnVideoViewStateChangeListener = new OnVideoViewStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {

        }

        @Override
        public void onPlayStateChanged(int playState) {
            //小屏状态下播放出来之后，把声音关闭
            if (playState == VideoView.STATE_PREPARED && !mVideoView.isFullScreen()) {
                mVideoView.setMute(true);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (!mSkipToDetail) {
            mVideoView.pause();
        } else {
            mSkipToDetail = false;
        }
        mVideoView.removeOnVideoViewStateChangeListener(mOnVideoViewStateChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mSkipToDetail) {
            mVideoView.resume();
        }
        mVideoView.addOnVideoViewStateChangeListener(mOnVideoViewStateChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removePlayerFormParent();
        mVideoView.setVideoController(null);
        mVideoView.release();
        SeamlessPlayHelper.getInstance().release();

    }

}
