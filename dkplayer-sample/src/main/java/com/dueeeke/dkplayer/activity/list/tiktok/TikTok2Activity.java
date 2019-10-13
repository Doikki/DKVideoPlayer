package com.dueeeke.dkplayer.activity.list.tiktok;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowInsets;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.Tiktok2Adapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.widget.VerticalViewPager;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

import java.util.List;

/**
 * 模仿抖音短视频，使用VerticalViewPager实现，实现了预加载功能，推荐
 * Created by dueeeke on 2019/10/13.
 */

public class TikTok2Activity extends AppCompatActivity {

    private static final String TAG = "TikTok2Activity";
    private int mCurrentPosition;
    private int mPlayingPosition;
    private List<VideoBean> mVideoList;
    private Tiktok2Adapter mTiktok2Adapter;
    private VerticalViewPager mViewPager;

    /**
     * 当前正在播放的VideoView
     */
    private VideoView mCurrentPlayVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_tiktok_2);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_tiktok2);

        setStatusBarTransparent();

        mVideoList = DataUtil.getTikTokVideoList();
        initViewPager();

    }


    private void initViewPager() {
        mViewPager = findViewById(R.id.vvp);
        mTiktok2Adapter = new Tiktok2Adapter(mVideoList);
        mViewPager.setAdapter(mTiktok2Adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (mCurrentPosition == mPlayingPosition) return;

                View itemView = mTiktok2Adapter.getCurrentItemView();
                VideoView videoView = itemView.findViewById(R.id.video_view);
                videoView.release();
                videoView.clearOnVideoViewStateChangeListeners();

                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            startPlay();
                        }
                    });
                }
            }
        });


        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                startPlay();
            }
        });


    }

    private void startPlay() {

        View itemView = mTiktok2Adapter.getCurrentItemView();

        VideoView videoView = itemView.findViewById(R.id.video_view);
        //划到此item的时候预加载（prepare）可能没完成。故需要设置此监听，等预加载完成之后直接开始播放
        videoView.setOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PREPARED) {
                    videoView.getMediaPlayer().start();
                }
            }
        });

        videoView.start();

        mPlayingPosition = mCurrentPosition;
        mCurrentPlayVideoView = videoView;
    }

    /**
     * 把状态栏设成透明
     */
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = TikTok2Activity.this.getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //一定要这样写
        if (mCurrentPlayVideoView != null)
            mCurrentPlayVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //一定要这样写
        if (mCurrentPlayVideoView != null)
            mCurrentPlayVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //一定要这样写
        VideoViewManager.instance().release();
    }

    public void addData(View view) {
        mVideoList.addAll(DataUtil.getTikTokVideoList());
        mTiktok2Adapter.notifyDataSetChanged();
    }
}
