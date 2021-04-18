
package com.example.fullscreenvlibrary.playbackspeed;

/**
 * Playback speed selected listener.
 */
public interface OnPlaybackSpeedSelectedListener {

    /**
     * Called when a speed is selected from the popup menu.
     *
     * @param speed The speed value represented as float.
     * @param text The speed text.
     */
    void onSpeedSelected(float speed, String text);
}
