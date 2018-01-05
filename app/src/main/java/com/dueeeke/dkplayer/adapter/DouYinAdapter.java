package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.widget.controller.DouYinController;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

public class DouYinAdapter extends RecyclerView.Adapter<DouYinAdapter.VideoHolder> {


        private List<VideoBean> videos;
        private Context context;

        public DouYinAdapter(List<VideoBean> videos, Context context) {
            this.videos = videos;
            this.context = context;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_douyin, parent, false);
            return new VideoHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final VideoHolder holder, int position) {

            VideoBean videoBean = videos.get(position);
            Glide.with(context)
                    .load(videoBean.getThumb())
                    .into(holder.mDouYinController.getThumb());
            holder.ijkVideoView
                    .addToPlayerManager()
                    .setUrl(videoBean.getUrl())
                    .setLooping()
                    .setVideoController(holder.mDouYinController);
            holder.ijkVideoView.setTag(position);

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        public class VideoHolder extends RecyclerView.ViewHolder {

            private IjkVideoView ijkVideoView;
            private DouYinController mDouYinController;

            VideoHolder(View itemView) {
                super(itemView);
                ijkVideoView = itemView.findViewById(R.id.video_player);
                mDouYinController = new DouYinController(context);
            }
        }
    }