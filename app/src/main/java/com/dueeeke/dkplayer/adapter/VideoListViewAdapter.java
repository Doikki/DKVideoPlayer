package com.dueeeke.dkplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

public class VideoListViewAdapter extends BaseAdapter {

    private List<VideoBean> videos;

    public VideoListViewAdapter(List<VideoBean> videos) {
        this.videos = videos;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ijkVideoView.setUrl(videoBean.getUrl());
        viewHolder.ijkVideoView.setVideoController(viewHolder.controller);
        viewHolder.controller.setTitle(videoBean.getTitle());
        Glide.with(viewHolder.controller.getThumb().getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(viewHolder.controller.getThumb());
        return convertView;
    }


    private class ViewHolder {
        private VideoView ijkVideoView;
        private StandardVideoController controller;

        ViewHolder(View itemView) {
            this.ijkVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(itemView.getContext());
        }
    }
}