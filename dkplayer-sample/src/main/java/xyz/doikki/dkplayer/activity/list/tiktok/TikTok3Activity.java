package xyz.doikki.dkplayer.activity.list.tiktok;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.adapter.Tiktok3Adapter;
import xyz.doikki.dkplayer.bean.TiktokBean;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.Utils;
import xyz.doikki.dkplayer.util.cache.PreloadManager;
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager;
import xyz.doikki.dkplayer.widget.VerticalViewPager;
import xyz.doikki.dkplayer.widget.controller.TikTokController;
import xyz.doikki.dkplayer.widget.render.TikTokRenderViewFactory;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.util.L;


/**
 * 模仿抖音短视频，使用ViewPager2实现，(实验性)
 * Created by Doikki on 2019/12/04.
 */

public class TikTok3Activity extends BaseActivity<DKVideoView> {

    /**
     * 当前播放位置
     */
    private int mCurPos;
    private List<TiktokBean> mVideoList = new ArrayList<>();
    private Tiktok3Adapter mTiktok3Adapter;
    private ViewPager2 mViewPager;

    private PreloadManager mPreloadManager;

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
        mVideoView = new DKVideoView(this);
        mVideoView.setLooping(true);
        //以下只能二选一，看你的需求
        mVideoView.setRenderFactory(TikTokRenderViewFactory.create());
//        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);

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

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
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
                if (state == VerticalViewPager.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }
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
                mVideoView.setDataSource(playUrl);
                //请点进去看isDissociate的解释
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                mCurPos = position;
                break;
            }
        }
    }

    public void addData(View view) {
        int size = mVideoList.size();
        mVideoList.addAll(DataUtil.getTiktokDataFromAssets(this));
        //使用此方法添加数据，使用notifyDataSetChanged会导致正在播放的视频中断
        mTiktok3Adapter.notifyItemRangeChanged(size, mVideoList.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreloadManager.removeAllPreloadTask();
        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(this);
    }
}
