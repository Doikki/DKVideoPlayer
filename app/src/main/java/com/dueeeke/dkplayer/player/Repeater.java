/*
 * Copyright (C) 2016 Brian Wernick
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

package com.dueeeke.dkplayer.player;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

/**
 * A method repeater to easily perform update functions on a timed basis.
 * <b>NOTE:</b> the duration between repeats may not be exact.
 */
public class Repeater {
    protected static final String HANDLER_THREAD_NAME = "ExoMedia_Repeater_HandlerThread";
    protected static final int DEFAULT_REPEAT_DELAY = 33; // ~30 fps

    public interface RepeatListener {
        void onRepeat();
    }

    protected volatile boolean repeaterRunning = false;
    protected int repeatDelay = DEFAULT_REPEAT_DELAY;

    protected Handler delayedHandler;
    protected HandlerThread handlerThread;
    protected boolean useHandlerThread = false;

    protected RepeatListener listener;
    protected PollRunnable pollRunnable = new PollRunnable();

    public Repeater() {
        this(true);
    }

    /**
     * @param processOnStartingThread True if the repeating process should be handled on the same thread that created the Repeater
     */
    public Repeater(boolean processOnStartingThread) {
        if (processOnStartingThread) {
            delayedHandler = new Handler();
            return;
        }

        useHandlerThread = true;
    }

    /**
     * @param handler The Handler to use for the repeating process
     */
    public Repeater(Handler handler) {
        delayedHandler = handler;
    }

    /**
     * Sets the amount of time between method invocation.
     *
     * @param milliSeconds The time between method calls [default: {@value #DEFAULT_REPEAT_DELAY}]
     */
    public void setRepeaterDelay(int milliSeconds) {
        repeatDelay = milliSeconds;
    }

    /**
     * Retrieves the amount of time between method invocation.
     *
     * @return The millisecond time between method calls
     */
    public int getRepeaterDelay() {
        return repeatDelay;
    }

    /**
     * Starts the repeater
     */
    public void start() {
        if (!repeaterRunning) {
            repeaterRunning = true;

            if (useHandlerThread) {
                handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
                handlerThread.start();
                delayedHandler = new Handler(handlerThread.getLooper());
            }

            pollRunnable.performPoll();
        }
    }

    /**
     * Stops the repeater
     */
    public void stop() {
        if (handlerThread != null) {
            handlerThread.quit();
        }

        repeaterRunning = false;
    }

    /**
     * Determines if the Repeater is currently running
     *
     * @return True if the repeater is currently running
     */
    public boolean isRunning() {
        return repeaterRunning;
    }

    /**
     * Sets the listener to be notified for each repeat
     *
     * @param listener The listener or null
     */
    public void setRepeatListener(@Nullable RepeatListener listener) {
        this.listener = listener;
    }

    protected class PollRunnable implements Runnable {
        @Override
        public void run() {
            if (listener != null) {
                listener.onRepeat();
            }

            if (repeaterRunning) {
                performPoll();
            }
        }

        public void performPoll() {
            delayedHandler.postDelayed(pollRunnable, repeatDelay);
        }
    }
}
