/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dueeeke.dkplayer.util;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * A helper class for periodically updating a {@link TextView} with debug information obtained from
 * a {@link VideoView}.
 */
public class DebugTextViewHelper implements OnVideoViewStateChangeListener, Runnable {

    private static final int REFRESH_INTERVAL_MS = 1000;

    private final VideoView player;
    private final TextView textView;

    private boolean started;

    public DebugTextViewHelper(VideoView player, TextView textView) {
        this.player = player;
        this.textView = textView;
    }

    /**
     * Starts periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public final void start() {
        if (started) {
            return;
        }
        started = true;
        player.addOnVideoViewStateChangeListener(this);
        updateAndPost();
    }

    /**
     * Stops periodic updates of the {@link TextView}. Must be called from the application's main
     * thread.
     */
    public final void stop() {
        if (!started) {
            return;
        }
        started = false;
        player.removeOnVideoViewStateChangeListener(this);
        textView.removeCallbacks(this);
    }

    // Runnable implementation.

    @Override
    public final void run() {
        updateAndPost();
    }

    // Protected methods.

    @SuppressLint("SetTextI18n")
    protected final void updateAndPost() {
        textView.setText(getDebugString());
        textView.removeCallbacks(this);
        textView.postDelayed(this, REFRESH_INTERVAL_MS);
    }

    /**
     * Returns the debugging information string to be shown by the target {@link TextView}.
     */
    protected String getDebugString() {
        return getCurrentPlayer() + getPlayStateString();
    }

    /**
     * Returns a string containing player state debugging information.
     */
    protected String getPlayStateString() {
        String playStateString;
        switch (player.getCurrentPlayState()) {
            default:
            case VideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case VideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

    protected String getCurrentPlayer() {
        String player;
        Object playerFactory = Utils.getCurrentPlayerFactory();
        if (playerFactory instanceof ExoMediaPlayerFactory) {
            player = "ExoPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else {
            player = "MediaPlayer";
        }
        return String.format("player: %s ", player);
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        updateAndPost();
    }
}
