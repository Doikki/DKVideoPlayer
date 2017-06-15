package com.devlin_n.yyplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yyplayer.R;
import com.devlin_n.yyplayer.bean.VideoBean;

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
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            return new VideoHolder(itemView);

        }

        @Override
        public void onBindViewHolder(final VideoHolder holder, int position) {

            VideoBean videoBean = videos.get(position);
            StandardVideoController controller = new StandardVideoController(context);
            Glide.with(context)
                    .load(videoBean.getThumb())
                    .asBitmap()
                    .animate(R.anim.anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .into(controller.getThumb());
            holder.yinYangPlayer
                    .enableCache()
                    .autoRotate()
//                    .useAndroidMediaPlayer()
                    .addToPlayerManager()
                    .setUrl(videoBean.getUrl())
                    .setTitle(videoBean.getTitle())
                    .setVideoController(controller);

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoHolder extends RecyclerView.ViewHolder {

            private YinYangPlayer yinYangPlayer;
            private StandardVideoController controller;

            public VideoHolder(View itemView) {
                super(itemView);
                yinYangPlayer = (YinYangPlayer) itemView.findViewById(R.id.video_player);
                int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
                yinYangPlayer.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));
                controller = new StandardVideoController(context);
                yinYangPlayer.setVideoController(controller);
            }
        }
    }