package com.dueeeke.dkplayer.activity.list;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.VideoView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * 自动播放
 * Created by Devlin_n on 2017/5/31.
 */

public class AutoPlayRecyclerViewActivity extends BaseListActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_auto_play_recycler_view;
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new VideoRecyclerViewAdapter(DataUtil.getVideoList()));
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                VideoView videoView = view.findViewById(R.id.video_player);
                if (videoView != null && !videoView.isFullScreen()) {
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: called");
//                    int tag = (int) videoView.getTag();
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: position: " + tag);
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
                            return;
                        }
                    }
                }
            }
        });

        recyclerView.post(() -> {
            //自动播放第一个
            View view = recyclerView.getChildAt(0);
            VideoView videoView = view.findViewById(R.id.video_player);
            videoView.start();
        });

    }
}
