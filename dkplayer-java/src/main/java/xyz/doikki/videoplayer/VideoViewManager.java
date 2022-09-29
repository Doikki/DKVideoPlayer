package xyz.doikki.videoplayer;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;

import xyz.doikki.videoplayer.player.ProgressManager;
import xyz.doikki.videoplayer.render.Render;
import xyz.doikki.videoplayer.render.RenderFactory;
import xyz.doikki.videoplayer.util.L;

/**
 * 视频播放器管理器，管理当前正在播放的VideoView，以及播放器配置
 * 你也可以用来保存常驻内存的VideoView，但是要注意通过Application Context创建，
 * 以免内存泄漏
 * <p>
 * todo：应该共享的是整个播放器，而不应该是VideoView
 */
public class VideoViewManager {

    /**
     * 保存VideoView的容器
     */
    private final LinkedHashMap<String, VideoView> mVideoViews = new LinkedHashMap<>();

    /**
     * 保存VideoView的容器
     */
    private static final LinkedHashMap<String, AVPlayer> mSharedPlayers = new LinkedHashMap<>();

    /**
     * 是否在移动网络下直接播放视频
     */
    private boolean mPlayOnMobileNetwork;

    /**
     * VideoViewManager实例
     */
    private static final VideoViewManager sInstance;

    /**
     * VideoViewConfig实例
     */
    private static VideoViewConfig sConfig;

    /**
     * 是否开启硬解码渲染优化，默认开启
     */
    private static boolean textureRenderOptimization = true;

    /**
     * 是否采用焦点模式：用于TV项目采用按键操作
     */
    private static boolean isFocusInTouchMode = false;

    private VideoViewManager() {
        mPlayOnMobileNetwork = sConfig.mPlayOnMobileNetwork;
    }

    static {
        sConfig = VideoViewConfig.newBuilder().build();
        sInstance = new VideoViewManager();
    }


    public static VideoViewManager instance() {
        return sInstance;
    }

    /**
     * 设置VideoViewConfig
     */
    public static void setConfig(@NonNull VideoViewConfig config) {
        sConfig = config;
    }

    /**
     * 获取VideoViewConfig
     */
    @NonNull
    public static VideoViewConfig getConfig() {
        return sConfig;
    }


    /**
     * 获取是否在移动网络下直接播放视频配置
     */
    public boolean playOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    /**
     * 设置是否在移动网络下直接播放视频
     */
    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        mPlayOnMobileNetwork = playOnMobileNetwork;
    }

    /**
     * 是否允许音频焦点
     *
     * @return
     */
    public static boolean enableAudioFocus() {
        return sConfig.enableAudioFocus;
    }

    /**
     * 获取全局进度管理器
     *
     * @return
     */
    public static ProgressManager getProgressManager() {
        return sConfig.mProgressManager;
    }

    /**
     * 获取播放界面缩放模式
     *
     * @return
     */
    public static int getScreenType() {
        return sConfig.mScreenScaleType;
    }


    /**
     * 开启{@link xyz.doikki.videoplayer.render.TextureViewRender} 渲染优化
     */
    public static void enableTextureRenderOptimization() {
        textureRenderOptimization = true;
    }

    /**
     * 关闭开启{@link xyz.doikki.videoplayer.render.TextureViewRender} 渲染优化
     */
    public static void disableMediaCodecTexture() {
        textureRenderOptimization = false;
    }

    /**
     * {@link xyz.doikki.videoplayer.render.TextureViewRender}是否启用渲染优化
     */
    public static boolean isTextureRenderOptimization() {
        return textureRenderOptimization;
    }

    /**
     * 添加VideoView
     *
     * @param tag 相同tag的VideoView只会保存一个，如果tag相同则会release并移除前一个
     */
    public void add(VideoView videoView, String tag) {
        if (!(videoView.getContext() instanceof Application)) {
            L.w("The Context of this VideoView is not an Application Context," +
                    "you must remove it after release,or it will lead to memory leek.");
        }
        VideoView old = get(tag);
        if (old != null) {
            old.release();
            remove(tag);
        }
        mVideoViews.put(tag, videoView);
    }


    public VideoView get(String tag) {
        return mVideoViews.get(tag);
    }

    public static boolean isFocusInTouchMode() {
        return isFocusInTouchMode;
    }

    public static void setFocusInTouchMode(boolean isFocusInTouchMode) {
        VideoViewManager.isFocusInTouchMode = isFocusInTouchMode;
    }

    public void remove(String tag) {
        mVideoViews.remove(tag);
    }

    public void removeAll() {
        mVideoViews.clear();
    }

    /**
     * 释放掉和tag关联的VideoView，并将其从VideoViewManager中移除
     */
    public void releaseByTag(String tag) {
        releaseByTag(tag, true);
    }

    public void releaseByTag(String tag, boolean isRemove) {
        VideoView videoView = get(tag);
        if (videoView != null) {
            videoView.release();
            if (isRemove) {
                remove(tag);
            }
        }
    }

    public boolean onBackPress(String tag) {
        VideoView videoView = get(tag);
        if (videoView == null) return false;
        return videoView.onBackPressed();
    }

    /**
     * 获取推荐或默认的播放器工厂（优先推荐的播放器）
     *
     * @param customFactory 如果不为空，则返回本对象，否则返回全局的工厂
     * @return
     */
    public static AVPlayerFactory<?> getPlayerFactory(@Nullable AVPlayerFactory<?> customFactory) {
        return customFactory == null ? sConfig.mPlayerFactory : customFactory;
    }

    /**
     * 创建播放器
     *
     * @param context
     * @param customFactory 自定义工厂，如果为null，则使用全局配置的工厂创建
     * @return
     */
    public static AVPlayer createMediaPlayer(@NonNull Context context, @Nullable AVPlayerFactory<?> customFactory) {
        return getPlayerFactory(customFactory).create(context);
    }

    /**
     * 获取或创建共享的播放器，通常用于播放器共享的情况（比如为了实现界面无缝切换效果）
     *
     * @param context
     * @param tag           共享标记
     * @param customFactory 自定义工厂
     * @return
     */
    public static AVPlayer getOrCreateSharedMediaPlayer(@NonNull Context context, @NonNull String tag, @Nullable AVPlayerFactory<?> customFactory) {
        AVPlayer cache = mSharedPlayers.get(tag);
        if (cache == null) {
            cache = createMediaPlayer(context, customFactory);
            mSharedPlayers.put(tag, cache);
        }
        return cache;
    }

    /**
     * 移除共享的播放器
     *
     * @param tag
     */
    public static void removeSharedMediaPlayer(String tag) {
        mSharedPlayers.remove(tag);
    }

    public static RenderFactory getRenderFactory(@Nullable RenderFactory customFactory) {
        return customFactory == null ? sConfig.mRenderViewFactory : customFactory;
    }

    /**
     * 创建视图层
     *
     * @param customFactory 自定义工厂，如果为null，则使用全局配置的工厂创建
     * @return
     */
    public static Render createRenderView(@NonNull Context context, @Nullable RenderFactory customFactory) {
        return getRenderFactory(customFactory).create(context);
    }

}
