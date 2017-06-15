package com.devlin_n.yyplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yyplayer.R;
import com.devlin_n.yyplayer.bean.VideoBean;

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

        viewHolder.yinYangPlayer
                .enableCache()
                .autoRotate()
//                    .useAndroidMediaPlayer()
                .addToPlayerManager()
                .setUrl(videoBean.getUrl())
                .setTitle(videoBean.getTitle())
                .setVideoController(viewHolder.controller);
        Glide.with(context)
                .load(videoBean.getThumb())
                .asBitmap()
                .animate(R.anim.anim_alpha_in)
                .placeholder(android.R.color.darker_gray)
                .into(viewHolder.controller.getThumb());

        return convertView;
    }


    private class ViewHolder {
        private YinYangPlayer yinYangPlayer;
        private StandardVideoController controller;

        ViewHolder(View itemView) {
            this.yinYangPlayer = (YinYangPlayer) itemView.findViewById(R.id.video_player);
            controller = new StandardVideoController(context);
        }
    }
}