
package com.example.fullscreenvlibrary.listener;

/**
 * Listener for error events.
 */
public interface OnErrorListener {

    /**
     * Called when an error has occurred
     *
     * @param exception holds information about the error
     */
    void onError(FullscreenVideoViewException exception);
}
