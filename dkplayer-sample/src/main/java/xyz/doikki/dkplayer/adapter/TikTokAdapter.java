package xyz.doikki.dkplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.bean.TiktokBean;
import xyz.doikki.dkplayer.util.cache.PreloadManager;
import xyz.doikki.dkplayer.widget.component.TikTokView;

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
        holder.mThumb.setScaleType(item.videoWidth > item.videoHeight ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.mThumb.getContext())
                .load(item.coverImgUrl)
                .placeholder(android.R.color.white)
                .into(holder.mThumb);
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

        private ImageView mThumb;
        public TikTokView mTikTokView;
        public int mPosition;
        public FrameLayout mPlayerContainer;

        VideoHolder(View itemView) {
            super(itemView);
            mTikTokView = itemView.findViewById(R.id.tiktok_View);
            mThumb = mTikTokView.findViewById(R.id.iv_thumb);
            mPlayerContainer = itemView.findViewById(R.id.container);
            itemView.setTag(this);
        }
    }
}