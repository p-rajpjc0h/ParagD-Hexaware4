
package com.example.fullscreenvlibrary;

import android.os.Handler;
import android.os.Message;

class MessageHandler extends Handler {
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private MediaController mediaController;

    MessageHandler(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mediaController == null) {
            return;
        }

        if (msg.what == FADE_OUT) {
            mediaController.hide();
        } else { // SHOW_PROGRESS
            int position = mediaController.setProgress();
            if (!mediaController.isDragging() &&
                    mediaController.isShowing() &&
                    mediaController.isPlaying()) {
                Message message = obtainMessage(SHOW_PROGRESS);
                sendMessageDelayed(message, 1000 - (position % 1000));
            }
        }
    }

    /**
     * Shows the progress bar.
     *
     * Checks if the progress bar should be hidden or shown.
     *
     * If it has to be shown - remove the fade out message and send a delayed one with
     * the requested timeout.
     *
     * If it has to be hidden - just remove the fade out message.
     *
     * @param timeout The timeout for progress bar hide in milliseconds.
     */
    void show(int timeout) {
        refresh();

        Message msg = obtainMessage(FADE_OUT);
        if (timeout != 0) {
            removeMessages(FADE_OUT);
            sendMessageDelayed(msg, timeout);
        } else {
            removeMessages(FADE_OUT);
        }
    }

    /**
     * Refreshes the progress bar by sending a SHOW_PROGRESS empty message to the handler.
     */
    void refresh() {
        sendEmptyMessage(SHOW_PROGRESS);
    }

    /**
     * Hides the progress bar.
     *
     * Uses the {@link #removeMessages(int) removeMessages} Handler method.
     */
    void hide() {
        removeMessages(SHOW_PROGRESS);
    }

    /**
     * Destroys references.
     * Called when a view is detached or destroyed.
     */
    void onDestroy() {
        mediaController = null;
    }
}
