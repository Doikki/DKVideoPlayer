package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.ProgressManagerImpl;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkPlayer;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoListViewAdapter extends BaseAdapter {

    private List<VideoBean> videos;
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

        viewHolder.ijkVideoView.setUrl(videoBean.getUrl());
        viewHolder.ijkVideoView.setVideoController(viewHolder.controller);
        viewHolder.controller.setTitle(videoBean.getTitle());
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
            this.ijkVideoView = itemView.findViewById(R.id.video_player);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            ijkVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels * 9 / 16 + 1));
            controller = new StandardVideoController(context);
            ijkVideoView.addToVideoViewManager();
            //保存播放进度
            ijkVideoView.setProgressManager(new ProgressManagerImpl());
            ijkVideoView.setCustomMediaPlayer(new IjkPlayer(context) {
                @Override
                public void setOptions() {
                    //精准seek
                    mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                }
            });
        }
    }
}