package com.dueeeke.dkplayer.fragment.list;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoListViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.interf.OnItemChildClickListener;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.listener.SimpleOnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends BaseFragment implements OnItemChildClickListener {

    private List<VideoBean> mVideos = new ArrayList<>();
    private VideoListViewAdapter mVideoListViewAdapter;

    private VideoView mVideoView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_list_view;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mVideoView = new VideoView(getActivity());
        StandardVideoController controller = new StandardVideoController(getActivity());
        controller.setEnableOrientation(true);
        mVideoView.setVideoController(controller);
        mVideoView.setEnableParallelPlay(true);
        ListView listView = findViewById(R.id.lv);
        mVideoListViewAdapter = new VideoListViewAdapter(mVideos);
        mVideoListViewAdapter.setOnItemChildClickListener(this);
        listView.setAdapter(mVideoListViewAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private View firstView; //记录当前屏幕中第一个可见的item对象
            private View lastView; //记录当前屏幕中最后个可见的item对象
            private int lastFirstVisibleItem; //记录当前屏幕中第一个可见的item的position
            private int lastVisibleItem; // 记录屏幕中最后一个可见的item的position
            private boolean scrollFlag;// 记录滑动状态

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        scrollFlag = true;
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) { // 避免不必要的执行
                    //如果记录的 屏幕中第一个可见的item的position 已经小于当前屏幕中第一个可见item的position，表示item已经完全滑出屏幕了
                    //这种情况一般出现在ListView上滑的时候，故此时我们可以把firstView上的播放器停止
                    if (lastFirstVisibleItem < firstVisibleItem) {
                        gcView(firstView);
                        //通过firstVisibleItem + visibleItemCount - 1我们可以得到当前屏幕上最后一个item的position
                        //如果屏幕中最后一个可见的item的position已经大于当前屏幕上最后一个item的position，表示item已经完全滑出屏幕了
                        //这种情况一般出现在ListView下滑的时候，故此时我们可以把lastView上的播放器停止
                    } else if (lastVisibleItem > firstVisibleItem + visibleItemCount - 1) {
                        gcView(lastView);
                    }
                    lastFirstVisibleItem = firstVisibleItem;
                    lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                    firstView = view.getChildAt(0);
                    lastView = view.getChildAt(visibleItemCount - 1);
                }
            }

            private void gcView(View gcView) {
                if (gcView != null) {
                    FrameLayout playerContainer = gcView.findViewById(R.id.player_container);
                    View view = playerContainer.getChildAt(0);
                    if (view != null && view == mVideoView && !mVideoView.isFullScreen()) {
                        mVideoView.release();
                        removeVideoViewFromParent();
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
        mVideoListViewAdapter.notifyDataSetChanged();
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

    private static final String TAG = "ListViewFragment";
    @Override
    public void onItemChildClick(View view, int position) {
        Log.d(TAG, "onItemChildClick: ");
        mVideoView.release();
        removeVideoViewFromParent();

        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());

        View itemView = mVideoListViewAdapter.getItemView(position);
        VideoListViewAdapter.ViewHolder viewHolder = (VideoListViewAdapter.ViewHolder) itemView.getTag();
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
    }

    private void removeVideoViewFromParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
        }
    }
}
