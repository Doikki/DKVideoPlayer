package com.devlin_n.yyplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayerManager;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIST");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoAdapter(getVideoList(), this));
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                YinYangPlayer magicVideoView = (YinYangPlayer) view.findViewById(R.id.video_view);
                if (magicVideoView != null) {
                    magicVideoView.release();
                }
            }
        });
    }

    public List<VideoBean> getVideoList() {
        List<VideoBean> videoList = new ArrayList<>();
        videoList.add(new VideoBean("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4"));

        videoList.add(new VideoBean("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4"));

        videoList.add(new VideoBean("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4"));

        videoList.add(new VideoBean("针织方便面，这可能是史上最不方便的方便面",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4"));

        videoList.add(new VideoBean("宵夜的下午茶，办公室不只有KPI，也有诗和远方",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4"));

        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
        return videoList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        YinYangPlayer currentVideoView = YinYangPlayerManager.instance().getCurrentVideoView();
        if (currentVideoView != null){
            currentVideoView.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (!YinYangPlayerManager.instance().onBackPressed()){
            super.onBackPressed();
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {


        private List<VideoBean> videos;
        private Context context;

        private VideoAdapter(List<VideoBean> videos, Context context) {
            this.videos = videos;
            this.context = context;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            return new VideoHolder(itemView);

        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {

            VideoBean videoBean = videos.get(position);
            StandardVideoController magicVideoController = new StandardVideoController(context);
            magicVideoController.showTopContainer();
            Glide.with(context)
                    .load(videoBean.getThumb())
                    .asBitmap()
                    .animate(R.anim.anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .into(magicVideoController.getThumb());
            holder.magicVideoView
                    .enableCache()
                    .autoRotate()
//                    .useAndroidMediaPlayer()
                    .addToPlayerManager()
                    .setUrl(videoBean.getUrl())
                    .setTitle(videoBean.getTitle())
                    .setVideoController(magicVideoController);

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoHolder extends RecyclerView.ViewHolder {

            private YinYangPlayer magicVideoView;

            public VideoHolder(View itemView) {
                super(itemView);
                magicVideoView = (YinYangPlayer) itemView.findViewById(R.id.video_view);
                int widthPixels = getResources().getDisplayMetrics().widthPixels;
                magicVideoView.setLayoutParams(new LinearLayout.LayoutParams(widthPixels, widthPixels / 16 * 9));
            }
        }
    }
}
