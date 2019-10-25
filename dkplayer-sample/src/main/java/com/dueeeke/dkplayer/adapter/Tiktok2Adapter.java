package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.TiktokBean;
import com.dueeeke.dkplayer.util.cache.PreloadManager;
import com.dueeeke.dkplayer.widget.controller.TikTokController;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.ArrayList;
import java.util.List;

public class Tiktok2Adapter extends PagerAdapter {

    /**
     * View缓存池，从ViewPager中移除的item将会存到这里面，用来复用
     */
    private List<View> mViewPool = new ArrayList<>();

    /**
     * 数据源
     */
    private List<TiktokBean> mVideoBeans;

    public Tiktok2Adapter(List<TiktokBean> videoBeans) {
        this.mVideoBeans = videoBeans;
    }

    @Override
    public int getCount() {
        return mVideoBeans == null ? 0 : mVideoBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        View view = null;
        if (mViewPool.size() > 0) {//取第一个进行复用
            view = mViewPool.get(0);
            mViewPool.remove(0);
        }

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_tik_tok_2, container, false);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        TiktokBean item = mVideoBeans.get(position);
        //开始预加载
        PreloadManager.getInstance(context).addPreloadTask(item.videoDownloadUrl, position);
        Glide.with(context)
                .load(item.coverImgUrl)
                .placeholder(android.R.color.white)
                .into(viewHolder.mThumb);
        viewHolder.mTitle.setText(item.title);
        viewHolder.mPosition = position;
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View itemView = (View) object;
        container.removeView(itemView);
        TiktokBean item = mVideoBeans.get(position);
        //取消预加载
        PreloadManager.getInstance(container.getContext()).removePreloadTask(item.videoDownloadUrl);
        //保存起来用来复用
        mViewPool.add(itemView);
    }

    /**
     * 借鉴ListView item复用方法
     */
    public static class ViewHolder {

        public TikTokController mTikTokController;
        public VideoView mVideoView;
        public int mPosition;
        public TextView mTitle;//标题
        public ImageView mThumb;//封面图

        ViewHolder(View itemView) {
            mVideoView = itemView.findViewById(R.id.video_view);
            mVideoView.setLooping(true);
            mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
            mTikTokController = new TikTokController(itemView.getContext());
            mVideoView.setVideoController(mTikTokController);
            mTitle = mTikTokController.findViewById(R.id.tv_title);
            mThumb = mTikTokController.findViewById(R.id.iv_thumb);
            itemView.setTag(this);
        }
    }
}
