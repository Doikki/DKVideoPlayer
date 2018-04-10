package com.dueeeke.videoplayer.player;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器配置类
 * Created by xinyu on 2018/4/3.
 */

public class PlayerConfig {

    public boolean usingAndroidMediaPlayer;//是否使用AndroidMediaPlayer
    public boolean isLooping;//是否循环播放
    public boolean mAutoRotate;//是否旋转屏幕
    public boolean isCache;//是否开启缓存
    public boolean addToPlayerManager;//是否添加到播放管理器
    public boolean usingSurfaceView;//是否使用TextureView
    public boolean enableMediaCodec;//是否启用硬解码
    public BaseMediaEngine mBaseMediaEngine = null;//自定义播放核心


    private PlayerConfig(PlayerConfig origin) {
        this.usingAndroidMediaPlayer = origin.usingAndroidMediaPlayer;
        this.isLooping = origin.isLooping;
        this.mAutoRotate = origin.mAutoRotate;
        this.isCache = origin.isCache;
        this.addToPlayerManager = origin.addToPlayerManager;
        this.usingSurfaceView = origin.usingSurfaceView;
        this.enableMediaCodec = origin.enableMediaCodec;
        this.mBaseMediaEngine = origin.mBaseMediaEngine;
    }

    private PlayerConfig() {

    }

    public static class Builder {

        private PlayerConfig target;

        public Builder() {
            target = new PlayerConfig();
        }

        /**
         * 开启缓存
         */
        public Builder enableCache() {
            target.isCache = true;
            return this;
        }

        /**
         * 添加到{@link VideoViewManager},如需集成到RecyclerView或ListView请开启此选项
         */
        public Builder addToPlayerManager() {
            target.addToPlayerManager = true;
            return this;
        }

        /**
         * 启用SurfaceView
         */
        public Builder usingSurfaceView() {
            target.usingSurfaceView = true;
            return this;
        }

        /**
         * 启用{@link android.media.MediaPlayer},如不调用默认使用 {@link IjkMediaPlayer}
         */
        public Builder usingAndroidMediaPlayer() {
            target.usingAndroidMediaPlayer = true;
            return this;
        }

        /**
         * 设置自动旋转
         */
        public Builder autoRotate() {
            target.mAutoRotate = true;
            return this;
        }

        /**
         * 开启循环播放
         */
        public Builder setLooping() {
            target.isLooping = true;
            return this;
        }

        /**
         * 开启硬解码，只对IjkPlayer有效
         */
        public Builder enableMediaCodec() {
            target.enableMediaCodec = true;
            return this;
        }

        /**
         * 设置自定义播放核心
         */
        public Builder setCustomMediaEngine(BaseMediaEngine baseMediaEngine) {
            target.mBaseMediaEngine = baseMediaEngine;
            return this;
        }


        public PlayerConfig build() {
            return new PlayerConfig(target);
        }
    }
}
