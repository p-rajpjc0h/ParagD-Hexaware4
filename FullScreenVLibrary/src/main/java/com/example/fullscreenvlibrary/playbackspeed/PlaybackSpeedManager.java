
package com.example.fullscreenvlibrary.playbackspeed;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import static android.view.View.INVISIBLE;

/**
 * Operates with the playback speed.
 */
public class PlaybackSpeedManager {

    private TextView playbackSpeedButton;
    private PlaybackSpeedPopupMenu popupMenu;

    public PlaybackSpeedManager(Context context, TextView playbackSpeedButton) {
        this.playbackSpeedButton = playbackSpeedButton;
        // Initialize the PopupMenu
        popupMenu = new PlaybackSpeedPopupMenu(context, playbackSpeedButton);
    }

    public void setPlaybackSpeedButtonOnClickListener(
            final PlaybackSpeedPopupMenuListener playbackSpeedPopupMenuListener
    ) {
        playbackSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.setOnSpeedSelectedListener(new OnPlaybackSpeedSelectedListener() {
                    @Override
                    public void onSpeedSelected(float speed, String text) {
                        playbackSpeedPopupMenuListener.onSpeedSelected(speed, text);
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        playbackSpeedPopupMenuListener.onPopupMenuDismissed();
                    }
                });

                // Show the PopupMenu
                popupMenu.show();

                playbackSpeedPopupMenuListener.onPopupMenuShown();
            }
        });
    }

    public void setPlaybackSpeedButtonEnabled(boolean isEnabled) {
        if (playbackSpeedButton != null) {
            playbackSpeedButton.setEnabled(isEnabled);
        }
    }

    public void hidePlaybackButton(boolean showPlaybackSpeedButton) {
        if (playbackSpeedButton != null && !showPlaybackSpeedButton) {
            playbackSpeedButton.setEnabled(false);
            playbackSpeedButton.setVisibility(INVISIBLE);
        }
    }

    public void setPlaybackSpeedText(String text) {
        playbackSpeedButton.setText(text);
    }

    public void setPlaybackSpeedOptions(PlaybackSpeedOptions playbackSpeedOptions) {
        popupMenu.setPlaybackSpeedOptions(playbackSpeedOptions);
    }
}
