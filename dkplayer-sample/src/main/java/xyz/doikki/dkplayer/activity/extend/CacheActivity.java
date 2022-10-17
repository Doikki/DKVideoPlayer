package xyz.doikki.dkplayer.activity.extend;

import com.danikula.videocache.HttpProxyCacheServer;
import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.BaseActivity;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.DKVideoView;

public class CacheActivity extends BaseActivity<DKVideoView> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_layout_common;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_cache;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.video_view);
        HttpProxyCacheServer cacheServer = ProxyVideoCacheManager.getProxy(this);
        String proxyUrl = cacheServer.getProxyUrl(DataUtil.SAMPLE_URL);
        mVideoView.setDataSource(proxyUrl);
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent(getString(R.string.str_cache), false);
        mVideoView.setVideoController(controller);
        mVideoView.start();

        //删除url对应默认缓存文件
//        ProxyVideoCacheManager.clearDefaultCache(this, URL);
        //清除缓存文件中的所有缓存
//        ProxyVideoCacheManager.clearAllCache(this);
    }
}
