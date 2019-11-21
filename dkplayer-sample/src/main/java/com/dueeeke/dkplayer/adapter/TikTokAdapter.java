package com.dueeeke.dkplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.util.cache.PreloadManager;
import com.dueeeke.dkplayer.widget.component.TikTokView;

import java.util.List;

@Deprecated
public class TikTokAdapter extends RecyclerView.Adapter<TikTokAdapter.VideoHolder> {

    private List<TiktokBean> videos;

    public TikTokAdapter(List<TiktokBean> videos) {
        this.videos = videos;
    }

    private static final String TAG = "TikTokAdapter";

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tik_tok, parent, false);
        return new VideoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        TiktokBean item = videos.get(position);
        Glide.with(holder.thumb.getContext())
                .load(item.coverImgUrl)
                .placeholder(android.R.color.white)
                .into(holder.thumb);
        holder.mPosition = position;
        PreloadManager.getInstance(holder.itemView.getContext()).addPreloadTask(item.videoDownloadUrl, position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VideoHolder holder) {
        super.onViewDetachedFromWindow(holder);
        TiktokBean item = videos.get(holder.mPosition);
        PreloadManager.getInstance(holder.itemView.getContext()).removePreloadTask(item.videoDownloadUrl);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoHolder extends RecyclerView.ViewHolder {

        private ImageView thumb;
        public TikTokView mTikTokView;
        public int mPosition;
        public FrameLayout mPlayerContainer;

        VideoHolder(View itemView) {
            super(itemView);
            mTikTokView = itemView.findViewById(R.id.tiktok_View);
            thumb = mTikTokView.findViewById(R.id.iv_thumb);
            mPlayerContainer = itemView.findViewById(R.id.container);
            itemView.setTag(this);
        }
    }
}