
package com.example.fullscreenvlibrary;

import android.view.ViewGroup;

/**
 * Manages the communication between the FullscreenVideoView and its related classes.
 */
public interface VideoView extends VideoMediaPlayer, OrientationController {

    /**
     * Gets the parent ViewGroup of the view.
     *
     * @return the parent ViewGroup
     */
    ViewGroup getParentLayout();

    /**
     * Hides the thumbnail of the video.
     */
    void hideThumbnail();

    /**
     * Called when the orientation is changed.
     */
    void onOrientationChanged();

    /**
     * Called when the video view is in fullscreen mode.
     */
    void onFullscreenActivated();

    /**
     * Called when the video view is not in fullscreen mode anymore.
     */
    void onFullscreenDeactivated();

    /**
     * Toggles the visibility of system navigation bar and status bar.
     */
    void toggleSystemUiVisibility();

    /**
     * Toggles the visibility of the toolbar.
     *
     * @param isVisible Indicates whether the toolbar should become visible or not.
     */
    void toggleToolbarVisibility(boolean isVisible);

    /**
     * Changes the orientation of the device.
     *
     * @param orientation The orientation to use for the change
     *
     * @see com.example.fullscreenvlibrary.orientation.PortraitOrientation
     * @see com.example.fullscreenvlibrary.orientation.LandscapeOrientation
     */
    void changeOrientation(int orientation);

    /**
     * Focuses the video view.
     */
    void focus();

    /**
     * Clears the tag of the fullscreen button.
     */
    void clearFullscreenButtonTag();
}
