package com.dueeeke.dkplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.bean.VideoBean;
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
    private List<VideoBean> mVideoBeans;

    /**
     * 当前可见的itemView
     */
    private View mCurrentItemView;

    public Tiktok2Adapter(List<VideoBean> videoBeans) {
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
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_tik_tok_2, container, false);
            viewHolder.mVideoView = view.findViewById(R.id.video_view);
            viewHolder.mVideoView.setLooping(true);
            //以下设置都必须设置
            viewHolder.mVideoView.setEnableParallelPlay(true);
            viewHolder.mVideoView.setPlayOnPrepared(false);
            viewHolder.mVideoView.setEnableAudioFocus(false);
            viewHolder.mTikTokController = new TikTokController(context);
            viewHolder.mVideoView.setVideoController(viewHolder.mTikTokController);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        VideoBean item = mVideoBeans.get(position);
        viewHolder.mVideoView.setUrl(item.getUrl());
        ImageView thumb = viewHolder.mTikTokController.getThumb();
        Glide.with(context)
                .load(item.getThumb())
                .placeholder(android.R.color.white)
                .into(thumb);
        //直接开始播放，此时视频会开始加载（prepare），由于setPlayOnPrepared(false)，视频在准备完成之后不会自己开始播放，这样就实现了预加载。
        viewHolder.mVideoView.start();

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View itemView = (View) object;
        container.removeView(itemView);

        //加入缓存池时先把视频release掉
        ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        viewHolder.mVideoView.release();
        //移除监听
        viewHolder.mVideoView.clearOnVideoViewStateChangeListeners();
        //保存起来用来复用
        mViewPool.add(itemView);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentItemView = (View) object;
    }

    public View getCurrentItemView() {
        return mCurrentItemView;
    }


    /**
     * 借鉴ListView item复用方法
     */
    private class ViewHolder {
        public TikTokController mTikTokController;
        public VideoView mVideoView;

    }
}
