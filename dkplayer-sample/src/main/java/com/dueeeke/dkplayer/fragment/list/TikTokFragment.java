package com.dueeeke.dkplayer.fragment.list;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.viewpager.widget.ViewPager;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.Tiktok2Adapter;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.cache.PreloadManager;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.dkplayer.widget.VerticalViewPager;
import com.dueeeke.dkplayer.widget.controller.TikTokController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

import java.util.ArrayList;
import java.util.List;

public class TikTokFragment extends BaseFragment {

    private int mCurrentPosition;
    private int mPlayingPosition;
    private List<TiktokBean> mVideoList = new ArrayList<>();
    private Tiktok2Adapter mTiktok2Adapter;
    private VerticalViewPager mViewPager;

    private PreloadManager mPreloadManager;

    /**
     * VerticalViewPager是否反向滑动
     */
    private boolean mIsReverseScroll;

    private VideoView mVideoView;
    private TikTokController mController;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tiktok2;
    }

    @Override
    protected void initViews() {
        super.initViews();
        initViewPager();
        initVideoView();
        mPreloadManager = PreloadManager.getInstance(getContext());
    }

    private void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mVideoView.setLooping(true);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);

        mController = new TikTokController(getActivity());
        mVideoView.setVideoController(mController);
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.vvp);
        mViewPager.setOffscreenPageLimit(4);
        mTiktok2Adapter = new Tiktok2Adapter(mVideoList);
        mViewPager.setAdapter(mTiktok2Adapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position > mPlayingPosition) {
                    mIsReverseScroll = false;
                } else if (position < mPlayingPosition) {
                    mIsReverseScroll = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
                if (position == mPlayingPosition) return;
                startPlay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (mCurrentPosition == mPlayingPosition) return;
                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mPreloadManager.resumePreload(mCurrentPosition, mIsReverseScroll);
                } else {
                    mPreloadManager.pausePreload(mCurrentPosition, mIsReverseScroll);
                }
            }
        });
    }

    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i ++) {
            View itemView = mViewPager.getChildAt(i);
            Tiktok2Adapter.ViewHolder viewHolder = (Tiktok2Adapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoView.release();
                removeVideoViewFromParent();

                TiktokBean tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.videoDownloadUrl);
                L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                mVideoView.setUrl(playUrl);
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                mPlayingPosition = position;
                break;
            }
        }
    }

    private void removeVideoViewFromParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        //模拟请求数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TiktokBean> tiktokBeans = DataUtil.getTiktokDataFromAssets(getActivity());
                mVideoList.addAll(tiktokBeans);

                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        mTiktok2Adapter.notifyDataSetChanged();
                        startPlay(0);
                    }
                });
            }
        }).start();

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
        mPreloadManager.removeAllPreloadTask();

        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(getActivity());
    }

}
