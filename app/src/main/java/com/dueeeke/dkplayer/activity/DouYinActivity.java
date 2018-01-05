package com.dueeeke.dkplayer.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.DouYinAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 模仿抖音短视频
 * Created by xinyu on 2018/1/6.
 */

public class DouYinActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private static final String TAG = "DouYinActivity";
    private IjkVideoView mIjkVideoView;
    private int mCurrentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);

        setStatusBarTransparent();

        mRecyclerView = findViewById(R.id.rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new DouYinAdapter(getVideoList(), this));
        new GravityPagerSnapHelper(Gravity.BOTTOM, true, position -> {
            Log.d(TAG, "onSnap: " + position);
            if (position == mCurrentPosition) return;
            if (mIjkVideoView != null) mIjkVideoView.start();
            mCurrentPosition = position;
        }).attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                Log.d(TAG, "onChildViewAttachedToWindow: ");
                mIjkVideoView = view.findViewById(R.id.video_player);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Log.d(TAG, "onChildViewDetachedFromWindow: ");
            }
        });
        mRecyclerView.post(() -> {
            View view = mRecyclerView.getChildAt(0);
            IjkVideoView ijkVideoView = view.findViewById(R.id.video_player);
            ijkVideoView.start();

        });
    }

    /**
     * 把状态栏设成透明
     */
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = DouYinActivity.this.getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                return defaultInsets.replaceSystemWindowInsets(
                        defaultInsets.getSystemWindowInsetLeft(),
                        0,
                        defaultInsets.getSystemWindowInsetRight(),
                        defaultInsets.getSystemWindowInsetBottom());
            });
            ViewCompat.requestApplyInsets(decorView);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
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

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.instance().releaseVideoPlayer();
    }
}
