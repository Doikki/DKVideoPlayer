package com.devlin_n.dcplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.devlin_n.videoplayer.controller.StandardVideoController;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.dcplayer.R;
import com.devlin_n.dcplayer.bean.VideoBean;

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

        viewHolder.ijkVideoView
                .enableCache()
                .autoRotate()
//                    .useAndroidMediaPlayer()
                .addToPlayerManager()
                .setUrl(videoBean.getUrl())
                .setTitle(videoBean.getTitle())
                .setVideoController(viewHolder.controller);
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

        ViewHolder(View itemView) {
            this.ijkVideoView = (IjkVideoView) itemView.findViewById(R.id.video_player);
            controller = new StandardVideoController(context);
        }
    }
}