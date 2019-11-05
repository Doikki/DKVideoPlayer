package com.dueeeke.dkplayer.fragment.list;

import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class RecyclerViewAutoPlayFragment extends RecyclerViewFragment {

    @Override
    protected void initView() {
        super.initView();
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
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;//记录可视区域item个数
            }

            private void autoPlayVideo(RecyclerView view) {
                if (view == null) return;
                //循环遍历可视区域videoview,如果完全可见就开始播放
                for (int i = 0; i < visibleCount; i++) {
                    View itemView = view.getChildAt(i);
                    if (itemView == null) continue;
                    FrameLayout playerContainer = itemView.findViewById(R.id.player_container);
                    if (playerContainer != null) {
                        Rect rect = new Rect();
                        playerContainer.getLocalVisibleRect(rect);
                        int height = playerContainer.getHeight();
                        if (rect.top == 0 && rect.bottom == height) {
                            VideoRecyclerViewAdapter.VideoHolder holder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
                            startPlay(holder.mPosition);
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

        mRecyclerView.post(() -> {
            //自动播放第一个
            startPlay(0);
        });
    }
}
