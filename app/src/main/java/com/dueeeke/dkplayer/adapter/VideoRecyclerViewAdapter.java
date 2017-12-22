package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {


        private List<VideoBean> videos;
        private Context context;

        public VideoRecyclerViewAdapter(List<VideoBean> videos, Context context) {
            this.videos = videos;
            this.context = context;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_video_auto_play, parent, false);
            return new VideoHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final VideoHolder holder, int position) {

            VideoBean videoBean = videos.get(position);
            Glide.with(context)
                    .load(videoBean.getThumb())
                    .crossFade()
                    .placeholder(android.R.color.darker_gray)
                    .into(holder.controller.getThumb());
            holder.ijkVideoView
                    .enableCache()
                    .autoRotate()
//                    .useAndroidMediaPlayer()
                    .addToPlayerManager()
                    .setUrl(videoBean.getUrl())
                    .setTitle(videoBean.getTitle())
                    .setVideoController(holder.controller);
            holder.title.setText(videoBean.getTitle());
            holder.ijkVideoView.setTag(position);

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoHolder extends RecyclerView.ViewHolder {

            private IjkVideoView ijkVideoView;
            private StandardVideoController controller;
            private TextView title;

            VideoHolder(View itemView) {
                super(itemView);
                ijkVideoView = (IjkVideoView) itemView.findViewById(R.id.video_player);
                int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
                ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));
                controller = new StandardVideoController(context);
                ijkVideoView.setVideoController(controller);
                title = itemView.findViewById(R.id.tv_title);
            }
        }
    }