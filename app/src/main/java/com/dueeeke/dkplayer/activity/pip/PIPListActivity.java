package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.dkplayer.PIPManager;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.FloatRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 悬浮播放终极版
 * Created by Devlin_n on 2017/5/31.
 */

public class PIPListActivity extends AppCompatActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private FrameLayout mPlayer, mThumb;
    private PIPManager mPIPManager;
    private IjkVideoView mIjkVideoView;
    private StandardVideoController mStandardVideoController;
    private List<VideoBean> mVideoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("PIP");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPIPManager = PIPManager.getInstance();
        mIjkVideoView = mPIPManager.getIjkVideoView();
        mStandardVideoController = new StandardVideoController(this);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVideoList = getVideoList();
        FloatRecyclerViewAdapter floatRecyclerViewAdapter = new FloatRecyclerViewAdapter(mVideoList, this);
        floatRecyclerViewAdapter.setOnChildViewClickListener(this);
        recyclerView.setAdapter(floatRecyclerViewAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                FrameLayout player = view.findViewById(R.id.player_container);
                FrameLayout thumb = view.findViewById(R.id.layout_thumb);
                if (player == null || thumb == null) return;
                int position = (int) player.getTag(R.id.key_position);
                if (position == mPIPManager.getPlayingPosition()) {
                    mPIPManager.stopFloatWindow();
                    thumb.setVisibility(View.GONE);
                    mStandardVideoController.setPlayState(mIjkVideoView.getCurrentPlayState());
                    mStandardVideoController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(mStandardVideoController);
                    player.addView(mIjkVideoView);
                    mThumb = thumb;
                    mPlayer = player;
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                FrameLayout player = view.findViewById(R.id.player_container);
                FrameLayout thumb = view.findViewById(R.id.layout_thumb);
                if (player == null || thumb == null) return;
                int position = (int) player.getTag(R.id.key_position);
                if (position == mPIPManager.getPlayingPosition()) {
                    if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
                    startFloatWindow();
                }
            }
        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
//            if (FloatWindowManager.getInstance().checkPermission(this)) {
//                mPIPManager.startFloatWindow();
//            } else {
//                Toast.makeText(PIPListActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void startFloatWindow() {
        if (FloatWindowManager.getInstance().checkPermission(this)) {
            mPIPManager.startFloatWindow();
        } else {
            mPIPManager.reset();
            FloatWindowManager.getInstance().applyPermission(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPIPManager.reset();
    }

    @Override
    public void onBackPressed() {
        if (mPIPManager.onBackPress()) return;
        super.onBackPressed();

    }

    public List<VideoBean> getVideoList() {
        List<VideoBean> videoList = new ArrayList<>();
        videoList.add(new VideoBean("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4"));

        videoList.add(new VideoBean("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4"));

        videoList.add(new VideoBean("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4"));

        videoList.add(new VideoBean("针织方便面，这可能是史上最不方便的方便面",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4"));

        videoList.add(new VideoBean("宵夜的下午茶，办公室不只有KPI，也有诗和远方",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4"));

        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));

        videoList.add(new VideoBean("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4"));

        videoList.add(new VideoBean("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4"));

        videoList.add(new VideoBean("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4"));

        videoList.add(new VideoBean("针织方便面，这可能是史上最不方便的方便面",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4"));

        videoList.add(new VideoBean("宵夜的下午茶，办公室不只有KPI，也有诗和远方",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4"));

        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));

        return videoList;
    }

    @Override
    public void onChildViewClick(View itemView, View childView, int position) {
        if (mPIPManager.getPlayingPosition() == position) return;
        if (mPIPManager.isStartFloatWindow()) mPIPManager.stopFloatWindow();
        if (mPlayer != null) mPlayer.removeAllViews();
        if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
        FrameLayout player = itemView.findViewById(R.id.player_container);
        FrameLayout thumb = itemView.findViewById(R.id.layout_thumb);
        VideoBean videoBean = mVideoList.get(position);
        mIjkVideoView.release();
        mIjkVideoView.setUrl(videoBean.getUrl());
        Glide.with(this).load(videoBean.getThumb()).into(mStandardVideoController.getThumb());
        mIjkVideoView.setVideoController(mStandardVideoController);
        mIjkVideoView.start();
        player.addView(mIjkVideoView);
        thumb.setVisibility(View.GONE);
        mPIPManager.setPlayingPosition(position);
        mPlayer = player;
        mThumb = thumb;

    }
}
