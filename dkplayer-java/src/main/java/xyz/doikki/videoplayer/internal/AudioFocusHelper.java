package xyz.doikki.videoplayer.internal;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import xyz.doikki.videoplayer.DKVideoView;

/**
 * 音频焦点 帮助类
 * @see #requestFocus()
 * @see #abandonFocus()
 */
public final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final WeakReference<DKVideoView> mWeakVideoView;

    private final AudioManager mAudioManager;

    private boolean mStartRequested = false;
    private boolean mPausedForLoss = false;
    private int mCurrentFocus = 0;

    public AudioFocusHelper(@NonNull DKVideoView videoView) {
        mWeakVideoView = new WeakReference<>(videoView);
        mAudioManager = (AudioManager) videoView.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        if (mCurrentFocus == focusChange) {
            return;
        }
        //这里应该先改变状态，然后在post，否则在极短时间内存在理论上的多次post
        mCurrentFocus = focusChange;

        //由于onAudioFocusChange有可能在子线程调用，
        //故通过此方式切换到主线程去执行
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {//进行异常捕获，避免因为音频焦点导致crash
                    handleAudioFocusChange(focusChange);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleAudioFocusChange(int focusChange) {
        final DKVideoView videoView = mWeakVideoView.get();
        if (videoView == null) {
            return;
        }
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
     * 请求音频焦点
     */
    public void requestFocus() {
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
     * 放弃音频焦点
     */
    public void abandonFocus() {
        if (mAudioManager == null) {
            return;
        }
        mStartRequested = false;
        mAudioManager.abandonAudioFocus(this);
    }
}