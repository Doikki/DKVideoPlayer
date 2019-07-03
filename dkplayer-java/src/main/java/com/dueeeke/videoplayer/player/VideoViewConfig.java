package com.dueeeke.videoplayer.player;

import android.support.annotation.Nullable;

/**
 * 播放器全局配置
 */
public class VideoViewConfig {

    public static Builder newBuilder() {
        return new Builder();
    }

    public final boolean mPlayOnMobileNetwork;

    public final boolean mEnableMediaCodec;

    public final boolean mUsingSurfaceView;

    public final boolean mAutoRotate;

    public final boolean mEnableAudioFocus;

    public final boolean mEnableParallelPlay;

    public final boolean mIsEnableLog;

    public final ProgressManager mProgressManager;

    public final PlayerFactory mPlayerFactory;


    private VideoViewConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mAutoRotate = builder.mAutoRotate;
        mUsingSurfaceView = builder.mUsingSurfaceView;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableMediaCodec = builder.mEnableMediaCodec;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mProgressManager = builder.mProgressManager;
        mEnableParallelPlay = builder.mEnableParallelPlay;
        mPlayerFactory = builder.mPlayerFactory;
    }


    public final static class Builder {

        private boolean mIsEnableLog;
        private boolean mPlayOnMobileNetwork;
        private boolean mUsingSurfaceView;
        private boolean mAutoRotate;
        private boolean mEnableMediaCodec;
        private boolean mEnableAudioFocus = true;
        private boolean mEnableParallelPlay;
        private ProgressManager mProgressManager;
        private PlayerFactory mPlayerFactory;

        /**
         * 是否通过重力感应切换全屏/半屏播放器， 默认不开启
         */
        public Builder setAutoRotate(boolean autoRotate) {
            mAutoRotate = autoRotate;
            return this;
        }

        /**
         * 是否启用SurfaceView，默认不启用
         */
        public Builder setUsingSurfaceView(boolean usingSurfaceView) {
            mUsingSurfaceView = usingSurfaceView;
            return this;
        }

        /**
         * 是否使用MediaCodec进行解码（硬解码），默认不开启，使用软解
         */
        public Builder setEnableMediaCodec(boolean enableMediaCodec) {
            mEnableMediaCodec = enableMediaCodec;
            return this;
        }

        /**
         * 在移动环境下调用start()后是否继续播放，默认不继续播放
         */
        public Builder setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
            mPlayOnMobileNetwork = playOnMobileNetwork;
            return this;
        }

        /**
         * 是否开启AudioFocus监听， 默认开启
         */
        public Builder setEnableAudioFocus(boolean enableAudioFocus) {
            mEnableAudioFocus = enableAudioFocus;
            return this;
        }

        /**
         * 设置进度管理器，用于保存播放进度
         */
        public Builder setProgressManager(@Nullable ProgressManager progressManager) {
            mProgressManager = progressManager;
            return this;
        }

        /**
         * 支持多开
         */
        public Builder setEnableParallelPlay(boolean enableParallelPlay) {
            mEnableParallelPlay = enableParallelPlay;
            return this;
        }

        /**
         * 是否打印日志
         */
        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        /**
         * 自定义播放核心
         */
        public Builder setPlayerFactory(PlayerFactory playerFactory) {
            mPlayerFactory = playerFactory;
            return this;
        }

        public VideoViewConfig build() {
            return new VideoViewConfig(this);
        }
    }
}
