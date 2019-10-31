package com.dueeeke.dkplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.interf.OnItemChildClickListener;
import com.dueeeke.videocontroller.PrepareView;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {

    private List<VideoBean> videos;

//    private ProgressManagerImpl mProgressManager;

//    private PlayerFactory mPlayerFactory = IjkPlayerFactory.create();

    private OnItemChildClickListener mOnItemChildClickListener;

    public VideoRecyclerViewAdapter(List<VideoBean> videos) {
        this.videos = videos;
    }

    @Override
    @NonNull
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {

        VideoBean videoBean = videos.get(position);

        Glide.with(holder.mThumb.getContext())
                .load(videoBean.getThumb())
                .crossFade()
                .placeholder(android.R.color.white)
                .into(holder.mThumb);
        holder.mTitle.setText(videoBean.getTitle());

        holder.mPosition = position;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void addData(List<VideoBean> videoList) {
        int size = videos.size();
        videos.addAll(videoList);
        //使用此方法添加数据，使用notifyDataSetChanged会导致正在播放的视频中断
        notifyItemRangeChanged(size, videos.size());
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int mPosition;
        public FrameLayout mPlayerContainer;
        public TextView mTitle;
        public ImageView mThumb;

        VideoHolder(View itemView) {
            super(itemView);
            mPlayerContainer = itemView.findViewById(R.id.player_container);
            mTitle = itemView.findViewById(R.id.tv_title);
            PrepareView prepareView = itemView.findViewById(R.id.prepare_view);
            mThumb = prepareView.findViewById(R.id.thumb);
            mPlayerContainer.setOnClickListener(this);
            itemView.setTag(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemChildClickListener != null) {
                mOnItemChildClickListener.onItemChildClick(v, mPosition);
            }
        }
    }


    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }
}