package com.devlin_n.magicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devlin_n.magic_player.player.MagicVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIST");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoAdapter(getVideoList(), this));
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                MagicVideoView magicVideoView = (MagicVideoView) view.findViewById(R.id.video_view);
                if (magicVideoView != null) {
                    magicVideoView.release();
                }
            }
        });
    }

    public List<String> getVideoList() {
        List<String> videoList = new ArrayList<>();
        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");

        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4");

        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4");

        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4");

        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4");

        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        videoList.add(
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        return videoList;
    }


    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {


        private List<String> videos;
        private Context context;

        private VideoAdapter(List<String> videos, Context context) {
            this.videos = videos;
            this.context = context;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            return new VideoHolder(itemView);

        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {


            holder.magicVideoView.init().setUrl(videos.get(position)).enableCache().setVideoController(MagicVideoView.VOD).autoRotate();

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoHolder extends RecyclerView.ViewHolder {

            private MagicVideoView magicVideoView;

            public VideoHolder(View itemView) {
                super(itemView);
                magicVideoView = (MagicVideoView) itemView.findViewById(R.id.video_view);
            }
        }
    }
}
