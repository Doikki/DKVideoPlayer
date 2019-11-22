package com.dueeeke.dkplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.adapter.listener.OnItemChildClickListener;
import com.dueeeke.videocontroller.component.PrepareView;

import java.util.List;

public class VideoListViewAdapter extends BaseAdapter {

    private List<VideoBean> videos;

    private OnItemChildClickListener mOnItemChildClickListener;

    private ViewGroup mListView;

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
        mListView = parent;
        ViewHolder viewHolder;
        VideoBean videoBean = videos.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(viewHolder.mThumb.getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.darker_gray)
                .into(viewHolder.mThumb);

        viewHolder.mTitle.setText(videoBean.getTitle());

        viewHolder.mPosition = position;

        return convertView;
    }


    public class ViewHolder implements View.OnClickListener {

        public int mPosition;
        public FrameLayout mPlayerContainer;
        public TextView mTitle;
        public ImageView mThumb;
        public PrepareView mPrepareView;

        ViewHolder(View itemView) {
            mPlayerContainer = itemView.findViewById(R.id.player_container);
            mTitle = itemView.findViewById(R.id.tv_title);
            mPrepareView = itemView.findViewById(R.id.prepare_view);
            mThumb = mPrepareView.findViewById(R.id.thumb);
            mPlayerContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemChildClickListener != null) {
                mOnItemChildClickListener.onItemChildClick(mPosition);
            }
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    public View getItemView(int position) {
        int count = mListView.getChildCount();
        for (int i = 0; i < count; i++){
            View itemView = mListView.getChildAt(i);
            ViewHolder viewHolder = (ViewHolder) itemView.getTag();
            if (position == viewHolder.mPosition) {
                return itemView;
            }
        }
        return null;
    }
}