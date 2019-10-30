package com.dueeeke.dkplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.widget.controller.RotateInFullscreenController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

public class RotateRecyclerViewAdapter extends RecyclerView.Adapter<RotateRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;

    public RotateRecyclerViewAdapter(List<VideoBean> videos) {
        this.videos = videos;
    }

    @Override
    @NonNull
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_rotate, parent, false);
        return new VideoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {

        VideoBean videoBean = videos.get(position);
        ImageView thumb = holder.mThumb;
        Glide.with(thumb.getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(thumb);
        holder.mVideoView.setUrl(videoBean.getUrl());
        holder.controller.setTitle(videoBean.getTitle());
        holder.mVideoView.setVideoController(holder.controller);
        holder.title.setText(videoBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private VideoView mVideoView;
        private RotateInFullscreenController controller;
        private TextView title;
        private ImageView mThumb;

        VideoHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_player);
            //这段代码用于实现小屏时静音，全屏时有声音
            mVideoView.setOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
                @Override
                public void onPlayerStateChanged(int playerState) {
                    if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                        mVideoView.setMute(false);
                    } else if (playerState == VideoView.PLAYER_NORMAL) {
                        mVideoView.setMute(true);
                    }
                }

                @Override
                public void onPlayStateChanged(int playState) {
                    //小屏状态下播放出来之后，把声音关闭
                    if (playState == VideoView.STATE_PREPARED && !mVideoView.isFullScreen()) {
                        mVideoView.setMute(true);
                    }
                }
            });
            controller = new RotateInFullscreenController(itemView.getContext());
            mThumb = controller.findViewById(R.id.thumb);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}