package com.dueeeke.videoplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 音频焦点改变监听
 */
final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

        private WeakReference<VideoView> mWeakVideoView;

        private AudioManager mAudioManager;

        private boolean startRequested = false;
        private boolean pausedForLoss = false;
        private int currentFocus = 0;

        AudioFocusHelper(@NonNull VideoView videoView) {
            mWeakVideoView = new WeakReference<>(videoView);
            mAudioManager = (AudioManager) videoView.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            VideoView videoView = mWeakVideoView.get();

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        videoView.start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mWeakVideoView != null && !videoView.isMute())//恢复音量
                        videoView.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://焦点暂时丢失
                    if (videoView.isPlaying()) {
                        pausedForLoss = true;
                        videoView.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://此时需降低音量
                    if (mWeakVideoView != null && videoView.isPlaying() && !videoView.isMute()) {
                        videoView.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        /**
         * Requests to obtain the audio focus
         */
        void requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return;
            }

            if (mAudioManager == null) {
                return;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return;
            }

            startRequested = true;
        }

        /**
         * Requests the system to drop the audio focus
         */
        void abandonFocus() {

            if (mAudioManager == null) {
                return;
            }

            startRequested = false;
            mAudioManager.abandonAudioFocus(this);
        }
    }