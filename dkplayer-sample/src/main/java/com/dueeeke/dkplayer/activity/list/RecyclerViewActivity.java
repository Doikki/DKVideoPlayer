package com.dueeeke.dkplayer.activity.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class RecyclerViewActivity extends BaseListActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_recycler_view;
    }

    @Override
    protected void initView() {
        VideoRecyclerViewAdapter adapter = new VideoRecyclerViewAdapter(DataUtil.getVideoList());
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
                adapter.addData(DataUtil.getVideoList());
            }
        });
    }

}
