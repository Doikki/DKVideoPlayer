package xyz.doikki.dkplayer.activity.list.tiktok;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.adapter.TikTokAdapter;
import xyz.doikki.dkplayer.bean.TiktokBean;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.Utils;
import xyz.doikki.dkplayer.util.cache.PreloadManager;
import xyz.doikki.dkplayer.widget.controller.TikTokController;
import xyz.doikki.dkplayer.widget.render.TikTokRenderViewFactory;
import xyz.doikki.videoplayer.VideoView;
import xyz.doikki.videoplayer.util.L;

/**
 * 模仿抖音短视频, 使用RecyclerView实现
 * Created by Doikki on 2018/1/6.
 * @deprecated 推荐 {@link TikTok2Activity}
 */
@Deprecated
public class TikTokActivity extends BaseActivity<VideoView> {

    private TikTokController mController;
    private int mCurPos;
    private RecyclerView mRecyclerView;
    private List<TiktokBean> mVideoList = new ArrayList<>();
    private TikTokAdapter mTikTokAdapter;

    private static final String KEY_INDEX = "index";
    private int mIndex;

    public static void start(Context context, int index) {
        Intent i = new Intent(context, TikTokActivity.class);
        i.putExtra(KEY_INDEX, index);
        context.startActivity(i);
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_tiktok_1;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tiktok;
    }

    @Override
    protected void initView() {
        super.initView();
        setStatusBarTransparent();
        mVideoView = new VideoView(this);
        //以下只能二选一，看你的需求
        mVideoView.setRenderFactory(TikTokRenderViewFactory.create());
//        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
        mVideoView.setLooping(true);
        mController = new TikTokController(this);
        mVideoView.setVideoController(mController);

        initRecyclerView();

        addData(null);

        Intent extras = getIntent();
        mIndex = extras.getIntExtra(KEY_INDEX, 0);
        mRecyclerView.scrollToPosition(mIndex);
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.rv);

        mTikTokAdapter = new TikTokAdapter(mVideoList);
        ViewPagerLayoutManager layoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mTikTokAdapter);
        layoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                //自动播放第index条
                startPlay(mIndex);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                if (mCurPos == position) {
                    mVideoView.release();
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (mCurPos == position) return;
                startPlay(position);
            }
        });
    }

    private void startPlay(int position) {
        View itemView = mRecyclerView.getChildAt(0);
        TikTokAdapter.VideoHolder viewHolder = (TikTokAdapter.VideoHolder) itemView.getTag();
        mVideoView.release();
        Utils.removeViewFormParent(mVideoView);
        TiktokBean item = mVideoList.get(position);
        String playUrl = PreloadManager.getInstance(this).getPlayUrl(item.videoDownloadUrl);
        L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
        mVideoView.setDataSource(playUrl);
        //请点进去看isDissociate的解释
        mController.addControlComponent(viewHolder.mTikTokView, true);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        mVideoView.start();
        mCurPos = position;
    }

    public void addData(View view) {
        mVideoList.addAll(DataUtil.getTiktokDataFromAssets(this));
        mTikTokAdapter.notifyDataSetChanged();
    }
}
