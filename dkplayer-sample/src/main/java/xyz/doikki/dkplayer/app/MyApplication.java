package xyz.doikki.dkplayer.app;

import androidx.multidex.MultiDexApplication;

import com.danikula.videocache.Logger;

import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.GlobalConfig;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.render.RenderFactory;

/**
 * app
 * Created by doikki on 2017/4/22.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //播放器配置，注意：此为全局配置，按需开启
        GlobalConfig.setDebuggable(BuildConfig.DEBUG);
        GlobalConfig.setRenderFactory(RenderFactory.textureViewRenderFactory());
//        GlobalConfig.setRenderFactory(RenderFactory.surfaceViewRenderFactory());
//        GlobalConfig.setPlayerFactory(IjkPlayerFactory.create());
//        /** 软解，支持格式较多，可通过自编译so扩展格式，结合 {@link xyz.doikki.dkplayer.widget.videoview.IjkVideoView} 使用更佳 */
        GlobalConfig.setPlayerFactory(ExoMediaPlayerFactory.create());

//        DKManager.setConfig(VideoViewConfig.newBuilder()
//                .setLogEnabled(BuildConfig.DEBUG) //调试的时候请打开日志，方便排错
//                /** 软解，支持格式较多，可通过自编译so扩展格式，结合 {@link xyz.doikki.dkplayer.widget.videoview.IjkVideoView} 使用更佳 */
////                .setPlayerFactory(IjkPlayerFactory.create())
////                .setPlayerFactory(AndroidMediaPlayerFactory.create()) //不推荐使用，兼容性较差
//                /** 硬解，支持格式看手机，请使用CpuInfoActivity检查手机支持的格式，结合 {@link xyz.doikki.dkplayer.widget.videoview.ExoVideoView} 使用更佳 */
//                .setPlayerFactory(ExoMediaPlayerFactory.create())
//                .setPlayerFactory(IjkPlayerFactory.create())
//                // 设置自己的渲染view，内部默认TextureView实现
////                .setRenderViewFactory(SurfaceRenderViewFactory.create())
//                // 根据手机重力感应自动切换横竖屏，默认false
////                .setEnableOrientation(true)
//                // 监听系统中其他播放器是否获取音频焦点，实现不与其他播放器同时播放的效果，默认true
////                .setEnableAudioFocus(false)
//                // 视频画面缩放模式，默认按视频宽高比居中显示在VideoView中
////                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
//                // 适配刘海屏，默认true
////                .setAdaptCutout(false)
//                // 移动网络下提示用户会产生流量费用，默认不提示，
//                // 如果要提示则设置成false并在控制器中监听STATE_START_ABORT状态，实现相关界面，具体可以参考PrepareView的实现
////                .setPlayOnMobileNetwork(false)
//                // 进度管理器，继承ProgressManager，实现自己的管理逻辑
////                .setProgressManager(new ProgressManagerImpl())
//                .build());

//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
//        }

        // VideoCache 日志
        Logger.setDebug(BuildConfig.DEBUG);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
