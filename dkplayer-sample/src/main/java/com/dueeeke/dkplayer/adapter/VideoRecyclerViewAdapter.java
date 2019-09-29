package com.dueeeke.dkplayer.adapter;

import android.support.annotation.NonNull;
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
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos = new ArrayList<>();

//    private ProgressManagerImpl mProgressManager;

//    private PlayerFactory mPlayerFactory = ExoMediaPlayerFactory.create(MyApplication.getInstance());

    public VideoRecyclerViewAdapter(List<VideoBean> videos) {
        this.videos.addAll(videos);
    }

    @Override
    @NonNull
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_auto_play, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {

        VideoBean videoBean = videos.get(position);
        ImageView thumb = holder.controller.getThumb();
        Glide.with(thumb.getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.white)
                .into(thumb);
        holder.mVideoView.setUrl(videoBean.getUrl());
        holder.controller.setTitle(videoBean.getTitle());
        holder.mVideoView.setVideoController(holder.controller);
        holder.title.setText(videoBean.getTitle());

        //保存播放进度
//        if (mProgressManager == null)
//            mProgressManager = new ProgressManagerImpl();
//        holder.mVideoView.setProgressManager(mProgressManager);
//        holder.mVideoView.setCustomMediaPlayer(new ExoMediaPlayer(holder.itemView.getContext()));
//        holder.mVideoView.setPlayerFactory(mPlayerFactory);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void addData(List<VideoBean> videoList) {
        int size = videos.size();
        videos.addAll(videoList);
        //使用此方法添加数据，使用notifyDataSetChanged会导致正在播放的视频中断
        notifyItemRangeChanged(size, videos.size());
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private VideoView mVideoView;
        private StandardVideoController controller;
        private TextView title;

        VideoHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            mVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(itemView.getContext());
            controller.setEnableOrientation(true);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}