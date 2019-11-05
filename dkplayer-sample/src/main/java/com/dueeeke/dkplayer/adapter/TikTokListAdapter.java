package com.dueeeke.dkplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.list.tiktok.TikTok2Activity;
import com.dueeeke.dkplayer.bean.TiktokBean;

import java.util.List;

public class TikTokListAdapter extends RecyclerView.Adapter<TikTokListAdapter.TikTokListViewHolder> {

    public List<TiktokBean> data;

    public TikTokListAdapter(List<TiktokBean> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TikTokListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tiktok_list, parent, false);
        return new TikTokListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TikTokListViewHolder holder, int position) {
        TiktokBean item = data.get(position);
        holder.mTitle.setText(item.title);
        Glide.with(holder.mThumb.getContext())
                .load(item.coverImgUrl)
                .into(holder.mThumb);

        holder.mPosition = position;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class TikTokListViewHolder extends RecyclerView.ViewHolder {

        public ImageView mThumb;
        public TextView mTitle;

        public int mPosition;

        public TikTokListViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.iv_thumb);
            mTitle = itemView.findViewById(R.id.tv_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TikTok2Activity.start(itemView.getContext(), mPosition);
//                    TikTokActivity.start(itemView.getContext(), mPosition);
                }
            });
        }
    }
}
