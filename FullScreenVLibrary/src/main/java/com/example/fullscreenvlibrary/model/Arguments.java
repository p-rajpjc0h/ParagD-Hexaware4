
package com.example.fullscreenvlibrary.model;

import android.graphics.drawable.Drawable;

import com.example.fullscreenvlibrary.orientation.LandscapeOrientation;
import com.example.fullscreenvlibrary.orientation.PortraitOrientation;
import com.example.fullscreenvlibrary.playbackspeed.PlaybackSpeedOptions;

/**
 * Arguments model class to store FullscreenVideoView arguments and read from them later.
 */
public class Arguments {
    public boolean autoStartEnabled = false;
    public Drawable enterFullscreenDrawable = null;
    public Drawable exitFullscreenDrawable = null;
    public Drawable playDrawable = null;
    public Drawable pauseDrawable = null;
    public Drawable fastForwardDrawable = null;
    public Drawable rewindDrawable = null;
    public int progressBarColor = -1;
    public int fastForwardSeconds = -1;
    public int rewindSeconds = -1;
    public LandscapeOrientation landscapeOrientation = null;
    public PortraitOrientation portraitOrientation = null;
    public boolean disablePause = false;
    public boolean addSeekForwardButton = false;
    public boolean addSeekBackwardButton = false;
    public boolean addPlaybackSpeedButton = false;
    public PlaybackSpeedOptions playbackSpeedOptions = null;
    public int thumbnailResId = -1;
    public boolean hideProgress = false;
    public boolean hideFullscreenButton = false;
    public int seekToTimeMillis = -1;
}
