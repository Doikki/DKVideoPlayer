package xyz.doikki.dkplayer.activity.extend;

import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager;
import xyz.doikki.dkplayer.widget.component.AdControlView;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.VideoView;

/**
 * 广告
 * Created by Doikki on 2017/4/7.
 */

public class ADActivity extends BaseActivity<VideoView> {

    private static final String URL_AD = "https://gslb.miaopai.com/stream/IR3oMYDhrON5huCmf7sHCfnU5YKEkgO2.mp4";

    private StandardVideoController mController;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_layout_common;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_ad;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);
        mController = new StandardVideoController(this);
        AdControlView adControlView = new AdControlView(this);
        adControlView.setListener(new AdControlView.AdControlListener() {
            @Override
            public void onAdClick() {
                Toast.makeText(ADActivity.this, "广告点击跳转", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSkipAd() {
                playVideo();
            }
        });
        mController.addControlComponent(adControlView);

        HttpProxyCacheServer cacheServer = ProxyVideoCacheManager.getProxy(this);
        String proxyUrl = cacheServer.getProxyUrl(URL_AD);
        mVideoView.setDataSource(proxyUrl);
        mVideoView.setVideoController(mController);

        //监听播放结束
        mVideoView.addOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playState) {
                if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                    playVideo();
                }
            }
        });

        mVideoView.start();
    }

    /**
     * 播放正片
     */
    private void playVideo() {
        mVideoView.release();
        mController.removeAllControlComponent();
        mController.addDefaultControlComponent("正片", false);
        //重新设置数据
        mVideoView.setDataSource(DataUtil.SAMPLE_URL);
        //开始播放
        mVideoView.start();
    }
}
