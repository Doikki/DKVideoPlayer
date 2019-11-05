package com.dueeeke.dkplayer.fragment.list;

import android.content.pm.ActivityInfo;
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
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends BaseFragment implements OnItemChildClickListener {

    private List<VideoBean> mVideos = new ArrayList<>();
    private VideoListViewAdapter mVideoListViewAdapter;

    private VideoView mVideoView;
    private StandardVideoController mController;
    private int mCurPosition = -1;
    private TitleView mTitleView;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_view;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = new VideoView(getActivity());
        mController = new StandardVideoController(getActivity());
        mController.addControlComponent(new ErrorView(getActivity()));
        mController.addControlComponent(new CompleteView(getActivity()));
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);

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
                        resetVideoView();
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
        if (mCurPosition == position) return;
        if (mCurPosition != -1) {
            resetVideoView();
        }

        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());
        mTitleView.setTitle(videoBean.getTitle());
        View itemView = mVideoListViewAdapter.getItemView(position);
        VideoListViewAdapter.ViewHolder viewHolder = (VideoListViewAdapter.ViewHolder) itemView.getTag();
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
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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
