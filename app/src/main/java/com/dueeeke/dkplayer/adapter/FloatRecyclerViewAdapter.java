package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;

import java.util.List;

public class FloatRecyclerViewAdapter extends RecyclerView.Adapter<FloatRecyclerViewAdapter.VideoHolder> {


    private List<VideoBean> videos;
    private Context context;
    private OnChildViewClickListener mOnChildViewClickListener;

    public FloatRecyclerViewAdapter(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_float_video, parent, false);
        return new VideoHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {
        VideoBean videoBean = videos.get(position);
        holder.title.setText(videoBean.getTitle());
        holder.mPlayerContainer.setTag(R.id.key_position, position);
        Glide.with(context).load(videoBean.getThumb()).into(holder.mThumb);
        holder.mThumb.setOnClickListener(v -> {
            if (mOnChildViewClickListener != null) mOnChildViewClickListener.onChildViewClick(holder.itemView, holder.mThumb, position);
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView mThumb;
        private FrameLayout mPlayerContainer;

        VideoHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            mThumb = itemView.findViewById(R.id.thumb);
            mPlayerContainer = itemView.findViewById(R.id.player_container);
        }
    }

    public void setOnChildViewClickListener(OnChildViewClickListener onChildViewClickListener) {
        mOnChildViewClickListener = onChildViewClickListener;
    }

    public interface OnChildViewClickListener{
        void onChildViewClick(View itemView, View childView, int position);
    }
}