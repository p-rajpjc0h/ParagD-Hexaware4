
package com.example.fullscreenvlibrary.playbackspeed;

/**
 * Listener for events of the PlaybackSpeedPopupMenu.
 */
public interface PlaybackSpeedPopupMenuListener {

    /**
     * A playback speed is selected.
     *
     * @param speed the value of the speed
     * @param text the speed value converted to string
     */
    void onSpeedSelected(float speed, String text);

    /**
     * The PopupMenu is dismissed.
     */
    void onPopupMenuDismissed();

    /**
     * The PopupMenu is shown.
     */
    void onPopupMenuShown();
}
