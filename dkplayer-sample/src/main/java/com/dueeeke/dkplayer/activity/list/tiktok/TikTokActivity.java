package com.dueeeke.dkplayer.activity.list.tiktok;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.adapter.TikTokAdapter;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.widget.controller.TikTokController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * 模仿抖音短视频, 使用RecyclerView实现
 * Created by dueeeke on 2018/1/6.
 * @deprecated 推荐 {@link TikTok2Activity}
 */
@Deprecated
public class TikTokActivity extends BaseActivity<VideoView> {

    private TikTokController mController;
    private int mCurrentPosition;
    private RecyclerView mRecyclerView;
    private List<TiktokBean> mVideoList = new ArrayList<>();
    private TikTokAdapter mTikTokAdapter;

    private static final String KEY_INDEX = "index";
    private int mIndex;

    public static void start(Context context, int index) {
        Intent i = new Intent(context, TikTokActivity.class);
        i.putExtra(KEY_INDEX, index);
        context.startActivity(i);
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_tiktok_1;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tiktok;
    }

    @Override
    protected void initView() {
        super.initView();
        setStatusBarTransparent();
        mVideoView = new VideoView(this);
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
        mVideoView.setLooping(true);
        mController = new TikTokController(this);
        mVideoView.setVideoController(mController);

        initRecyclerView();

        addData(null);

        Intent extras = getIntent();
        mIndex = extras.getIntExtra(KEY_INDEX, 0);
        mRecyclerView.scrollToPosition(mIndex);
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.rv);

        mTikTokAdapter = new TikTokAdapter(mVideoList);
        ViewPagerLayoutManager layoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTikTokAdapter);
        layoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                //自动播放第index条
                startPlay(mIndex);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                if (mCurrentPosition == position) {
                    mVideoView.release();
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (mCurrentPosition == position) return;
                startPlay(position);
                mCurrentPosition = position;
            }
        });
    }

    private void startPlay(int position) {
        View itemView = mRecyclerView.getChildAt(0);
        TikTokAdapter.VideoHolder viewHolder = (TikTokAdapter.VideoHolder) itemView.getTag();
        mVideoView.release();
        removeVideoViewFromParent();
        TiktokBean item = mVideoList.get(position);
        L.i("startPlay: " + "position: " + position + "  url: " + item.videoDownloadUrl);
        mVideoView.setUrl(item.videoDownloadUrl);
        mController.addControlComponent(viewHolder.mTikTokView, true);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        mVideoView.start();
    }

    private void removeVideoViewFromParent() {
        ViewParent parent = mVideoView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mVideoView);
        }
    }

    public void addData(View view) {
        mVideoList.addAll(DataUtil.getTiktokDataFromAssets(this));
        mTikTokAdapter.notifyDataSetChanged();
    }
}
