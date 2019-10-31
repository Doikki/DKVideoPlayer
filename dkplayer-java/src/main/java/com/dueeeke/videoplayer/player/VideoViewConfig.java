package com.dueeeke.videoplayer.player;


import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.render.RenderViewFactory;
import com.dueeeke.videoplayer.render.TextureRenderViewFactory;

/**
 * 播放器全局配置
 */
public class VideoViewConfig {

    public static Builder newBuilder() {
        return new Builder();
    }

    public final boolean mPlayOnMobileNetwork;

    public final boolean mEnableOrientation;

    public final boolean mEnableAudioFocus;

    public final boolean mEnableParallelPlay;

    public final boolean mIsEnableLog;

    public final ProgressManager mProgressManager;

    public final PlayerFactory mPlayerFactory;

    public final int mScreenScaleType;

    public final RenderViewFactory mRenderViewFactory;

    private VideoViewConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableOrientation = builder.mEnableOrientation;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mProgressManager = builder.mProgressManager;
        mEnableParallelPlay = builder.mEnableParallelPlay;
        mScreenScaleType = builder.mScreenScaleType;
        if (builder.mPlayerFactory == null) {
            //默认为AndroidMediaPlayer
            mPlayerFactory = AndroidMediaPlayerFactory.create();
        } else {
            mPlayerFactory = builder.mPlayerFactory;
        }
        if (builder.mRenderViewFactory == null) {
            //默认使用TextureView渲染视频
            mRenderViewFactory = TextureRenderViewFactory.create();
        } else {
            mRenderViewFactory = builder.mRenderViewFactory;
        }
    }


    public final static class Builder {

        private boolean mIsEnableLog;
        private boolean mPlayOnMobileNetwork;
        private boolean mEnableOrientation;
        private boolean mEnableAudioFocus = true;
        private boolean mEnableParallelPlay;
        private ProgressManager mProgressManager;
        private PlayerFactory mPlayerFactory;
        private int mScreenScaleType;
        private RenderViewFactory mRenderViewFactory;

        /**
         * 是否监听设备方向来切换全屏/半屏， 默认不开启
         */
        public Builder setEnableOrientation(boolean enableOrientation) {
            mEnableOrientation = enableOrientation;
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
         * @deprecated 此api已经无效，你需要自己去控制同时只有一个VideoView在播放的效果
         */
        @Deprecated
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

        /**
         * 设置视频比例
         */
        public Builder setScreenScaleType(int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        /**
         * 自定义RenderView
         */
        public Builder setRenderViewFactory(RenderViewFactory renderViewFactory) {
            mRenderViewFactory = renderViewFactory;
            return this;
        }

        public VideoViewConfig build() {
            return new VideoViewConfig(this);
        }
    }
}
