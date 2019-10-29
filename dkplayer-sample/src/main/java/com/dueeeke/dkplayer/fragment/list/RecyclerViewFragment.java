package com.dueeeke.dkplayer.fragment.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends BaseFragment {

    private List<VideoBean> mVideos = new ArrayList<>();
    private VideoRecyclerViewAdapter mVideoRecyclerViewAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected void initViews() {
        super.initViews();
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideos);
        recyclerView.setAdapter(mVideoRecyclerViewAdapter);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                VideoView videoView = view.findViewById(R.id.video_player);
                if (videoView != null && !videoView.isFullScreen()) {
                    videoView.release();
                }
            }
        });

        View view = findViewById(R.id.add);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoRecyclerViewAdapter.addData(DataUtil.getVideoList());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<VideoBean> videoList = DataUtil.getVideoList();
        mVideos.addAll(videoList);
        mVideoRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        VideoViewManager.instance().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        VideoViewManager.instance().resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoViewManager.instance().release();
    }
}
