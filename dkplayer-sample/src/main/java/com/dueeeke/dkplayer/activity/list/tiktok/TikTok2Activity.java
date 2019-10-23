package com.dueeeke.dkplayer.activity.list.tiktok;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.adapter.Tiktok2Adapter;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.cache.PreloadManager;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.dkplayer.widget.VerticalViewPager;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.util.L;

import java.util.List;


/**
 * 模仿抖音短视频，使用VerticalViewPager实现，实现了预加载功能，推荐
 * Created by dueeeke on 2019/10/13.
 */

public class TikTok2Activity extends BaseActivity {

    private int mCurrentPosition;
    private int mPlayingPosition;
    private List<TiktokBean> mVideoList;
    private Tiktok2Adapter mTiktok2Adapter;
    private VerticalViewPager mViewPager;

    private PreloadManager mPreloadManager;

    /**
     * VerticalViewPager是否反向滑动
     */
    private boolean mIsReverseScroll;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tiktok2;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_tiktok_2;
    }

    @Override
    protected void initView() {
        super.initView();
        setStatusBarTransparent();
        mVideoList = DataUtil.getTiktokDataFromAssets(this);
        initViewPager();
        mPreloadManager = PreloadManager.getInstance(this);
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


        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                startPlay(0);
            }
        });


    }

    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i ++) {
            View itemView = mViewPager.getChildAt(i);
            Tiktok2Adapter.ViewHolder viewHolder = (Tiktok2Adapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                VideoView videoView = itemView.findViewById(R.id.video_view);
                TiktokBean tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.videoDownloadUrl);
                L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                videoView.setUrl(playUrl);
                videoView.start();
                mPlayingPosition = position;
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.instance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoViewManager.instance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoViewManager.instance().release();
        mPreloadManager.removeAllPreloadTask();

        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(this);
    }

    public void addData(View view) {
        mVideoList.addAll(DataUtil.getTiktokDataFromAssets(this));
        mTiktok2Adapter.notifyDataSetChanged();
    }
}
