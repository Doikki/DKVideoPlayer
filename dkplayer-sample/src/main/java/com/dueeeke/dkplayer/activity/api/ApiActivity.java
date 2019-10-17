package com.dueeeke.dkplayer.activity.api;

import android.content.Intent;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.IntentKeys;

/**
 * 基础API相关Demo
 * Created by xinyu on 2018/1/3.
 */

public class ApiActivity extends BaseActivity {

//    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
    private static final String VOD_URL = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4";
//    private static final String VOD_URL = "http://155.138.214.164/test/1.mp4";
//    private static final String VOD_URL = "http://youku163.zuida-bofang.com/20190113/24356_0cbc9d8f/index.m3u8";
//    private static final String VOD_URL = "http://sohu.zuida-163sina.com/20190303/ZRkvZAiK/index.m3u8";
//    private static final String VOD_URL = "http://vfile.hshan.com/2018/1524/9156/4430/152491564430.ssm/152491564430.m3u8";
//    private static final String VOD_URL = "https://aweme.snssdk.com/aweme/v1/play/?video_id=3fdb4876a7f34bad8fa957db4b5ed159&line=0&ratio=720p&media_type=4&vr_type=0";
//    private static final String VOD_URL = "http://videofile2.cutv.com/mg/010061_t/2019/07/23/G15/G15fgfflggklinnkggoez2_cug.mp4.m3u8";
    //断线自动重连,需加上ijkhttphook:
//    private static final String VOD_URL = "ijkhttphook:http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
//    private static final String LIVE_URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
//    private static final String LIVE_URL = "http://ivi.bupt.edu.cn/hls/sztv.m3u8";
//    private static final String LIVE_URL = "http://220.161.87.62:8800/hls/0/index.m3u8";
//    private static final String LIVE_URL = "http://ivi.bupt.edu.cn/hls/cctv6.m3u8";
    private static final String LIVE_URL = "rtmp://media3.sinovision.net:1935/live/livestream";
    //断线自动重连,需加上ijklivehook:
//    private static final String LIVE_URL = "ijklivehook:rtmp://live.hkstv.hk.lxdns.com/live/hks";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_api;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_api;
    }

    public void skipToVodPlayer(View view) {
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String url = "file://" + path + File.separator + "test.mp4";
//        String url = "file:///storage/emulated/0/Android/data/com.dueeeke.dkplayer/cache/video-cache/56b84ac750c6959155b6b6e4d9f01b98.mp4";
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(IntentKeys.URL, VOD_URL);
        intent.putExtra(IntentKeys.IS_LIVE, false);
        intent.putExtra(IntentKeys.TITLE, "点播");
        startActivity(intent);
    }

    public void skipToLivePlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(IntentKeys.URL, LIVE_URL);
        intent.putExtra(IntentKeys.IS_LIVE, true);
        intent.putExtra(IntentKeys.TITLE, "直播");
        startActivity(intent);
    }

    public void skipToConcatPlay(View view) {
        startActivity(new Intent(this, ConcatPlayActivity.class));
    }

    public void skipToDefinitionPlayer(View view) {
        startActivity(new Intent(this, DefinitionPlayerActivity.class));
    }

    public void skipToRawOrAssets(View view) {
        startActivity(new Intent(this, PlayRawAssetsActivity.class));
    }

    public void multiPlayer(View view) {
        startActivity(new Intent(this, ParallelPlayActivity.class));
    }
}
