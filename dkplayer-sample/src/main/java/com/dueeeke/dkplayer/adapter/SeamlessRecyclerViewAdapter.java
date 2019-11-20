package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videocontroller.component.PrepareView;

import java.util.List;

public class SeamlessRecyclerViewAdapter extends RecyclerView.Adapter<SeamlessRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public SeamlessRecyclerViewAdapter(List<VideoBean> videos, Context context) {
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
        Glide.with(context)
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.white)
                .into(holder.mThumb);
        holder.title.setText(videoBean.getTitle());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView mThumb;

        public PrepareView mPrepareView;

        VideoHolder(View itemView) {
            super(itemView);
            mPrepareView = itemView.findViewById(R.id.prepare_view);
            mThumb = mPrepareView.findViewById(R.id.thumb);
            title = itemView.findViewById(R.id.tv_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}