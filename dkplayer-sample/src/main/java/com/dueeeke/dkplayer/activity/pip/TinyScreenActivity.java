package com.dueeeke.dkplayer.activity.pip;

import android.content.pm.ActivityInfo;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.adapter.listener.OnItemChildClickListener;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.listener.SimpleOnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

/**
 * 小窗播放
 * Created by dueeeke on 2017/5/31.
 */
public class TinyScreenActivity extends BaseActivity<VideoView> implements OnItemChildClickListener {

    private StandardVideoController mController;
    private List<VideoBean> mVideos;
    private LinearLayoutManager mLinearLayoutManager;
    private TitleView mTitleView;
    private int mCurPos = -1;

    @Override
    protected int getTitleResId() {
        return R.string.str_tiny_screen;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initView() {
        mVideoView = new VideoView(this);
        mVideoView.setOnVideoViewStateChangeListener(new SimpleOnVideoViewStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    if (mVideoView.isTinyScreen()) {
                        mVideoView.stopTinyScreen();
                        releaseVideoView();
                    }
                }
            }
        });
        mController = new StandardVideoController(this);
        addControlComponent();

        initRecyclerView();
    }

    private void addControlComponent() {
        CompleteView completeView = new CompleteView(this);
        ErrorView errorView = new ErrorView(this);
        mTitleView = new TitleView(this);
        mController.addControlComponent(completeView, errorView, mTitleView);
        mController.addControlComponent(new VodControlView(this));
        mController.addControlComponent(new GestureView(this));
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mVideos = DataUtil.getVideoList();
        VideoRecyclerViewAdapter adapter = new VideoRecyclerViewAdapter(mVideos);
        adapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                VideoRecyclerViewAdapter.VideoHolder holder = (VideoRecyclerViewAdapter.VideoHolder) view.getTag();
                int position = holder.mPosition;
                if (position == mCurPos) {
                    startPlay(position, false);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                VideoRecyclerViewAdapter.VideoHolder holder = (VideoRecyclerViewAdapter.VideoHolder) view.getTag();
                int position = holder.mPosition;
                if (position == mCurPos && !mVideoView.isFullScreen()) {
                    mVideoView.startTinyScreen();
                    mVideoView.setVideoController(null);
                    mController.setPlayState(VideoView.STATE_IDLE);
                }
            }
        });
    }

    @Override
    public void onItemChildClick(int position) {
        startPlay(position, true);
    }

    /**
     * 开始播放
     *
     * @param position 列表位置
     */
    protected void startPlay(int position, boolean isRelease) {
        if (mVideoView.isTinyScreen())
            mVideoView.stopTinyScreen();
        if (mCurPos != -1 && isRelease) {
            releaseVideoView();
        }
        VideoBean videoBean = mVideos.get(position);
        mVideoView.setUrl(videoBean.getUrl());
        mTitleView.setTitle(videoBean.getTitle());
        View itemView = mLinearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        //注意：要先设置控制才能去设置控制器的状态。
        mVideoView.setVideoController(mController);
        mController.setPlayState(mVideoView.getCurrentPlayState());

        VideoRecyclerViewAdapter.VideoHolder viewHolder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
        //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController.addControlComponent(viewHolder.mPrepareView, true);
        Utils.removeViewFormParent(mVideoView);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        mVideoView.start();
        mCurPos = position;
    }

    private void releaseVideoView() {
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPos = -1;
    }
}
