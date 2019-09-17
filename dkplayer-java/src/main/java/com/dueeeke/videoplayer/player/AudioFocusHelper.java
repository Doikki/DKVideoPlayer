package com.dueeeke.videoplayer.player;

import android.content.Context;
import android.media.AudioManager;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 音频焦点改变监听
 */
final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

        private WeakReference<VideoView> mWeakVideoView;

        private AudioManager mAudioManager;

        private boolean mStartRequested = false;
        private boolean mPausedForLoss = false;
        private int mCurrentFocus = 0;

        AudioFocusHelper(@NonNull VideoView videoView) {
            mWeakVideoView = new WeakReference<>(videoView);
            mAudioManager = (AudioManager) videoView.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (mCurrentFocus == focusChange) {
                return;
            }

            VideoView videoView = mWeakVideoView.get();

            if (videoView == null) {
                return;
            }

            mCurrentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (mStartRequested || mPausedForLoss) {
                        videoView.start();
                        mStartRequested = false;
                        mPausedForLoss = false;
                    }
                    if (!videoView.isMute())//恢复音量
                        videoView.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://焦点暂时丢失
                    if (videoView.isPlaying()) {
                        mPausedForLoss = true;
                        videoView.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://此时需降低音量
                    if (videoView.isPlaying() && !videoView.isMute()) {
                        videoView.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        /**
         * Requests to obtain the audio focus
         */
        void requestFocus() {
            if (mCurrentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return;
            }

            if (mAudioManager == null) {
                return;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                mCurrentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return;
            }

            mStartRequested = true;
        }

        /**
         * Requests the system to drop the audio focus
         */
        void abandonFocus() {

            if (mAudioManager == null) {
                return;
            }

            mStartRequested = false;
            mAudioManager.abandonAudioFocus(this);
        }
    }