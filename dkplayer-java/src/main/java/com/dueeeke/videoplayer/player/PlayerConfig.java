package com.dueeeke.videoplayer.player;

/**
 * 播放器配置类
 * Created by xinyu on 2018/4/3.
 */

public class PlayerConfig {

    public boolean isLooping;//是否循环播放
    public boolean mAutoRotate;//是否旋转屏幕
    public boolean addToPlayerManager;//是否添加到播放管理器
    public boolean usingSurfaceView;//是否使用TextureView
    public boolean enableMediaCodec;//是否启用硬解码
    public boolean savingProgress;//是否保存进度
    public AbstractPlayer mAbstractPlayer = null;//自定义播放核心
    public boolean disableAudioFocus;//关闭AudioFocus监听


    private PlayerConfig(PlayerConfig origin) {
        this.isLooping = origin.isLooping;
        this.mAutoRotate = origin.mAutoRotate;
        this.addToPlayerManager = origin.addToPlayerManager;
        this.usingSurfaceView = origin.usingSurfaceView;
        this.enableMediaCodec = origin.enableMediaCodec;
        this.mAbstractPlayer = origin.mAbstractPlayer;
        this.savingProgress = origin.savingProgress;
        this.disableAudioFocus = origin.disableAudioFocus;
    }

    private PlayerConfig() {

    }

    public static class Builder {

        private PlayerConfig target;

        public Builder() {
            target = new PlayerConfig();
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
        public Builder setCustomMediaPlayer(AbstractPlayer abstractPlayer) {
            target.mAbstractPlayer = abstractPlayer;
            return this;
        }

        /**
         * 保存播放进度
         */
        public Builder savingProgress() {
            target.savingProgress = true;
            return this;
        }

        /**
         * 关闭AudioFocus监听
         */
        public Builder disableAudioFocus() {
            target.disableAudioFocus = true;
            return this;
        }

        public PlayerConfig build() {
            return new PlayerConfig(target);
        }
    }
}
