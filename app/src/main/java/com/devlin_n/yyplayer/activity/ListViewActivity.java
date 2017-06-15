package com.devlin_n.yyplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;
import com.devlin_n.yinyangplayer.player.YinYangPlayerManager;
import com.devlin_n.yyplayer.R;
import com.devlin_n.yyplayer.adapter.VideoListViewAdapter;
import com.devlin_n.yyplayer.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devlin_n on 2017/6/14.
 */

public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIST");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ListView listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(new VideoListViewAdapter(getVideoList(), this));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private View fistView, lastView;
            private int lastFirstVisibleItem = -1;//必须初始化为小于0的数
            private int lastVisibleItem;
            private boolean scrollFlag;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        scrollFlag = true;
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollFlag) {
                    if (lastFirstVisibleItem < firstVisibleItem) {
                        GCView(fistView);
                        fistView = view.getChildAt(0);
                        lastView = view.getChildAt(visibleItemCount - 1);
                    } else if (lastVisibleItem > firstVisibleItem + visibleItemCount - 1) {
                        GCView(lastView);
                        fistView = view.getChildAt(0);
                        lastView = view.getChildAt(visibleItemCount - 1);
                    }
                    lastFirstVisibleItem = firstVisibleItem;
                    lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                }
            }

            private void GCView(View gcView) {
                if (gcView != null) {
                    YinYangPlayer yinYangPlayer = (YinYangPlayer) gcView.findViewById(R.id.video_player);
                    if (yinYangPlayer != null && !yinYangPlayer.isFullScreen()) {
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
        YinYangPlayer currentVideoPlayer = YinYangPlayerManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer != null) {
            currentVideoPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (!YinYangPlayerManager.instance().onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                YinYangPlayerManager.instance().getCurrentVideoPlayer().startFloatWindow();
            } else {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
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
}
