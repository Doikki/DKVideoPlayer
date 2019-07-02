package com.dueeeke.dkplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;

    public VideoRecyclerViewAdapter(List<VideoBean> videos) {
        this.videos = videos;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_auto_play, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        VideoBean videoBean = videos.get(position);
        ImageView thumb = holder.controller.getThumb();
        Glide.with(thumb.getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.white)
                .into(thumb);
        holder.ijkVideoView.setUrl(videoBean.getUrl());
//        holder.ijkVideoView.setCustomMediaPlayer(new ExoMediaPlayer(holder.ijkVideoView.getContext()));
        holder.controller.setTitle(videoBean.getTitle());
        holder.ijkVideoView.setVideoController(holder.controller);
        holder.title.setText(videoBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private IjkVideoView ijkVideoView;
        private StandardVideoController controller;
        private TextView title;

        VideoHolder(View itemView) {
            super(itemView);
            ijkVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(itemView.getContext());
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}