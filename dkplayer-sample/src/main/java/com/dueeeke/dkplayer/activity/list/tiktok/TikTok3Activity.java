package com.dueeeke.dkplayer.activity.list.tiktok;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.adapter.Tiktok3Adapter;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.dkplayer.util.cache.PreloadManager;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.dkplayer.widget.controller.TikTokController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

import java.util.ArrayList;
import java.util.List;


/**
 * 模仿抖音短视频，使用ViewPager2实现，(实验性)
 * Created by dueeeke on 2019/12/04.
 */

public class TikTok3Activity extends BaseActivity<VideoView> {

    /**
     * 当前播放位置
     */
    private int mCurPos;
    private List<TiktokBean> mVideoList = new ArrayList<>();
    private Tiktok3Adapter mTiktok3Adapter;
    private ViewPager2 mViewPager;

    private PreloadManager mPreloadManager;

    /**
     * VerticalViewPager是否反向滑动
     */
    private boolean mIsReverseScroll;

    private TikTokController mController;

    private static final String KEY_INDEX = "index";
    private RecyclerView mViewPagerImpl;

    public static void start(Context context, int index) {
        Intent i = new Intent(context, TikTok3Activity.class);
        i.putExtra(KEY_INDEX, index);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tiktok3;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_tiktok_3;
    }

    @Override
    protected void initView() {
        super.initView();
        setStatusBarTransparent();
        initViewPager();
        initVideoView();
        mPreloadManager = PreloadManager.getInstance(this);

        addData(null);
        Intent extras = getIntent();
        int index = extras.getIntExtra(KEY_INDEX, 0);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    startPlay(0);
                } else {
                    mViewPager.setCurrentItem(index, false);
                }
            }
        });
    }

    private void initVideoView() {
        mVideoView = new VideoView(this);
        mVideoView.setLooping(true);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);

        mController = new TikTokController(this);
        mVideoView.setVideoController(mController);
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.vp2);
        mViewPager.setOffscreenPageLimit(4);
        mTiktok3Adapter = new Tiktok3Adapter(mVideoList);
        mViewPager.setAdapter(mTiktok3Adapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mIsReverseScroll = position < mCurPos;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mCurPos) return;
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        startPlay(position);
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
                } else {
                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
                }
            }
        });

        //ViewPage2内部是通过RecyclerView去实现的，它位于ViewPager2的第0个位置
        mViewPagerImpl = (RecyclerView) mViewPager.getChildAt(0);
    }

    private void startPlay(int position) {
        int count = mViewPagerImpl.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = mViewPagerImpl.getChildAt(i);
            Tiktok3Adapter.ViewHolder viewHolder = (Tiktok3Adapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoView.release();
                Utils.removeViewFormParent(mVideoView);
                TiktokBean tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.videoDownloadUrl);
                L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                mVideoView.setUrl(playUrl);
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                mCurPos = position;
                break;
            }
        }
    }

    public void addData(View view) {
        mVideoList.addAll(DataUtil.getTiktokDataFromAssets(this));
        mTiktok3Adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreloadManager.removeAllPreloadTask();
        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(this);
    }
}
