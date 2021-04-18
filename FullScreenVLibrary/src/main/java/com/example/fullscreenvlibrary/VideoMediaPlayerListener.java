package com.example.fullscreenvlibrary;

import android.media.MediaPlayer;

import com.example.fullscreenvlibrary.model.MediaPlayerError;

public interface VideoMediaPlayerListener {

    /**
     * Called when the media player is in prepared state.
     *
     * @param mediaPlayer The media player which plays the video
     * @param videoWidth The width of the video
     * @param videoHeight The height of the video
     * @param isAutoStartEnabled Indicates whether the auto start is enabled or not
     */

    void onInfoListnerPrepared(MediaPlayer.OnInfoListener listener);

    void onMediaPlayerPrepared(
            MediaPlayer mediaPlayer,
            int videoWidth,
            int videoHeight,
            boolean isAutoStartEnabled
    );
    /**
     * Called when an error occurs in the media player.
     *
     * @param error The media player error
     */
    void onMediaPlayerError(MediaPlayerError error);

    /**
     * Called when the video playing has completed.
     */
    void onMediaPlayerCompletion();
}
