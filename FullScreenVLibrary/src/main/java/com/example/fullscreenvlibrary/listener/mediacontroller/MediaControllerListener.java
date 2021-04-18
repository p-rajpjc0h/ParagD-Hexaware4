
package com.example.fullscreenvlibrary.listener.mediacontroller;

/**
 * Listener for media controller events.
 */
public interface MediaControllerListener {

    /**
     * The play button has been clicked.
     */
    void onPlayClicked();

    /**
     * The pause button has been clicked.
     */
    void onPauseClicked();

    /**
     * The rewind button has been clicked.
     */
    void onRewindClicked();

    /**
     * The fast forward button has been clicked.
     */
    void onFastForwardClicked();

    /**
     * The fullscreen button is clicked.
     */
    void onFullscreenClicked();

    /**
     * The SeekBar progress is changed from user interaction.
     *
     * @param progressMs the progress in milliseconds
     */
    void onSeekBarProgressChanged(long progressMs);
}
