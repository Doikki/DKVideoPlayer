package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videoplayer.player.PlayerConfig;

import java.util.ArrayList;
import java.util.List;

public class VideoListViewAdapter extends BaseAdapter {

    private List<VideoBean> videos = new ArrayList<>();
    private Context context;

    public VideoListViewAdapter(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        VideoBean videoBean = videos.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ijkVideoView.setPlayerConfig(viewHolder.mPlayerConfig);
        viewHolder.ijkVideoView.setUrl(videoBean.getUrl());
        viewHolder.ijkVideoView.setTitle(videoBean.getTitle());
        viewHolder.ijkVideoView.setVideoController(viewHolder.controller);
        Glide.with(context)
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(viewHolder.controller.getThumb());

        return convertView;
    }


    private class ViewHolder {
        private IjkVideoView ijkVideoView;
        private StandardVideoController controller;
        private PlayerConfig mPlayerConfig;

        ViewHolder(View itemView) {
            this.ijkVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(context);
            mPlayerConfig = new PlayerConfig.Builder()
//                    .enableCache()
//                    .autoRotate()
                    .addToPlayerManager()
                    .savingProgress()
                    .build();
        }
    }
}