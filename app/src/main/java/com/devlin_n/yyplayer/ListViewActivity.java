package com.devlin_n.yyplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.devlin_n.yin_yang_player.controller.StandardVideoController;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yin_yang_player.player.YinYangPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devlin_n on 2017/6/14.
 */

public class ListViewActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIST");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(new VideoAdapter(getVideoList()));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstVisibleItem = -1, lastVisibleItem;
            private View fistView, lastView;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (this.firstVisibleItem < firstVisibleItem) {
                    this.firstVisibleItem = firstVisibleItem;
                    this.lastVisibleItem = firstVisibleItem + visibleItemCount;
                    GCView(fistView);
                    fistView = view.getChildAt(0);
                    lastView = view.getChildAt(visibleItemCount - 1);
                } else if (this.lastVisibleItem > (firstVisibleItem + visibleItemCount)) {
                    this.firstVisibleItem = firstVisibleItem;
                    this.lastVisibleItem = firstVisibleItem + visibleItemCount;
                    GCView(lastView);
                    fistView = view.getChildAt(0);
                    lastView = view.getChildAt(visibleItemCount - 1);
                }

            }

            private void GCView(View gcView) {
                if (gcView != null) {
                    YinYangPlayer yinYangPlayer = (YinYangPlayer) gcView.findViewById(R.id.video_player);
                    if (yinYangPlayer != null) {
                        yinYangPlayer.release();
                    }

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        YinYangPlayer currentVideoView = YinYangPlayerManager.instance().getCurrentVideoView();
        if (currentVideoView != null) {
            currentVideoView.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (!YinYangPlayerManager.instance().onBackPressed()) {
            super.onBackPressed();
        }
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

        return videoList;
    }


    private class VideoAdapter extends BaseAdapter {

        private List<VideoBean> videos = new ArrayList<>();

        public VideoAdapter(List<VideoBean> videos) {
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
            ViewHolder viewHolder;
            VideoBean videoBean = videos.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.item_video, listView, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                Log.d("@@@@@@@@@", "getView: null");
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                Log.d("@@@@@@@@@", "getView: no null");
            }

            viewHolder.yinYangPlayer
                    .enableCache()
                    .autoRotate()
//                    .useAndroidMediaPlayer()
                    .addToPlayerManager()
                    .setUrl(videoBean.getUrl())
                    .setTitle(videoBean.getTitle())
                    .setVideoController(viewHolder.controller);
            Glide.with(ListViewActivity.this)
                    .load(videoBean.getThumb())
                    .asBitmap()
                    .animate(R.anim.anim_alpha_in)
                    .placeholder(android.R.color.darker_gray)
                    .into(viewHolder.controller.getThumb());

            return convertView;
        }


        class ViewHolder {
            private YinYangPlayer yinYangPlayer;
            private StandardVideoController controller;

            ViewHolder(View itemView) {
                this.yinYangPlayer = (YinYangPlayer) itemView.findViewById(R.id.video_player);
                controller = new StandardVideoController(ListViewActivity.this);
            }
        }
    }
}
