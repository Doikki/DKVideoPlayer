package com.dueeeke.dkplayer.fragment.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.list.DetailActivity;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.Tag;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * 无缝播放
 */
public class SeamlessPlayFragment extends RecyclerViewAutoPlayFragment {

    private boolean mSkipToDetail;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initView() {
        super.initView();

        //提前添加到VideoViewManager，供详情使用
        getVideoViewManager().add(mVideoView, Tag.SEAMLESS);

        mAdapter.setOnItemClickListener(position -> {
            mSkipToDetail = true;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            Bundle bundle = new Bundle();
            VideoBean videoBean = mVideos.get(position);
            if (mCurPos == position) {
                //需要无缝播放
                bundle.putBoolean(IntentKeys.SEAMLESS_PLAY, true);
                bundle.putString(IntentKeys.TITLE, videoBean.getTitle());
            } else {
                //无需无缝播放，把相应数据传到详情页
                mVideoView.release();
                //需要把控制器还原
                mController.setPlayState(VideoView.STATE_IDLE);
                bundle.putBoolean(IntentKeys.SEAMLESS_PLAY, false);
                bundle.putString(IntentKeys.URL, videoBean.getUrl());
                bundle.putString(IntentKeys.TITLE, videoBean.getTitle());
                mCurPos = position;
            }
            intent.putExtras(bundle);
            View sharedView = mLinearLayoutManager.findViewByPosition(position).findViewById(R.id.player_container);
            //使用共享元素动画
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity(), sharedView, "player_container");
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        });
    }

    @Override
    protected void startPlay(int position) {
        mVideoView.setVideoController(mController);
        super.startPlay(position);
    }

    @Override
    protected void pause() {
        if (!mSkipToDetail) {
            super.pause();
        }
    }

    @Override
    protected void resume() {
        if (mSkipToDetail) {
            //还原播放器
            View itemView = mLinearLayoutManager.findViewByPosition(mCurPos);
            VideoRecyclerViewAdapter.VideoHolder viewHolder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
            mController.addControlComponent(viewHolder.mPrepareView, true);
            mVideoView = getVideoViewManager().get(Tag.SEAMLESS);
            mController.setPlayState(mVideoView.getCurrentPlayState());
            mController.setPlayerState(mVideoView.getCurrentPlayerState());
            mVideoView.setVideoController(mController);
            Utils.removeViewFormParent(mVideoView);
            viewHolder.mPlayerContainer.addView(mVideoView, 0);
            mSkipToDetail = false;
        } else {
            super.resume();
        }
    }
}
