
package com.example.fullscreenvlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import com.example.fullscreenvlibrary.listener.mediacontroller.MediaControllerListener;
import com.example.fullscreenvlibrary.playbackspeed.PlaybackSpeedManager;
import com.example.fullscreenvlibrary.playbackspeed.PlaybackSpeedOptions;
import com.example.fullscreenvlibrary.playbackspeed.PlaybackSpeedPopupMenuListener;

import static com.example.fullscreenvlibrary.Constants.DEFAULT_CONTROLLER_TIMEOUT;
import static com.example.fullscreenvlibrary.Constants.VIEW_TAG_CLICKED;


class VideoControllerView extends FrameLayout
        implements MediaController, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "VideoControllerView";

    @Nullable
    private MessageHandler handler;

    // Views
    private TextView endTime;
    private TextView currentTime;
    private SeekBar progress;
    private ImageButton startPauseButton;
    private ImageButton fastForwardButton;
    private ImageButton rewindButton;
    private ImageButton fullscreenButton;
    private TextView playbackSpeedButton;

    @Nullable
    private MediaControllerListener mediaControllerListener;

    // Flags
    private boolean isDragging;
    private boolean seekBackwardButtonVisible = false;
    private boolean seekForwardButtonVisible = false;
    private boolean playbackSpeedButtonVisible = false;

    private PlaybackSpeedManager playbackSpeedManager;
    private ControllerDrawableManager drawableManager;

    private VideoView videoView;

    private int progressBarColor = Color.WHITE;
    private int fastForwardDuration = Constants.FAST_FORWARD_DURATION;
    private int rewindDuration = Constants.REWIND_DURATION;

    private ViewTreeObserver.OnWindowFocusChangeListener onWindowFocusChangeListener =
            new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    if (videoView.isLandscape()) {
                        ((Activity) getContext())
                                .getWindow()
                                .getDecorView()
                                .setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                );
                    }
                }
            };

    public VideoControllerView(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.video_controller, this, true);
        init();
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.video_controller, this, true);
        init();
        setupXmlAttributes(attrs);
    }

    private void init() {
        if (!isInEditMode()) {
            setVisibility(INVISIBLE);
        }

        startPauseButton = findViewById(R.id.start_pause_media_button);
        fastForwardButton = findViewById(R.id.forward_media_button);
        rewindButton = findViewById(R.id.rewind_media_button);
        fullscreenButton = findViewById(R.id.fullscreen_media_button);
        playbackSpeedButton = findViewById(R.id.playback_speed_button);

        playbackSpeedManager = new PlaybackSpeedManager(getContext(), playbackSpeedButton);

        setupButtonListeners();

        progress = findViewById(R.id.progress_seek_bar);
        if (progress != null) {
            setProgressBarDrawablesColors();
            progress.setOnSeekBarChangeListener(this);
            progress.setMax(1000);
        }

        endTime = findViewById(R.id.time);
        currentTime = findViewById(R.id.time_current);
    }

    public void init(AttributeSet attrs) {
        setupXmlAttributes(attrs);

        updatePausePlay();
        updateFullScreenDrawable();
        updateFastForwardDrawable();
        updateRewindDrawable();

        handler = new MessageHandler(this);

        getViewTreeObserver().addOnWindowFocusChangeListener(onWindowFocusChangeListener);
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    /**
     * Shows the controller on screen. It will go away automatically after 'timeout' milliseconds
     * of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        if (!isShowing()) {
            startPauseButton.requestFocus();
            setProgress();
            setupButtonsVisibility();
            setVisibility(VISIBLE);
        }

        if (startPauseButton != null) {
            boolean isPlaying = videoView.isPlaying();
            Drawable playPauseDrawable = drawableManager.getPlayPauseDrawable(isPlaying);
            startPauseButton.setImageDrawable(playPauseDrawable);
        }

        updatePausePlay();
        updateFullScreenDrawable();

        // Cause the progress bar to be updated even if it's showing.
        // This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        if (handler == null) {
            return;
        }

        handler.show(timeout);
    }

    @Override
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    /**
     * Remove the controller from the screen.
     */
    @Override
    public void hide() {
        try {
            setVisibility(INVISIBLE);
            if (handler != null) {
                handler.hide();
            }
        } catch (IllegalArgumentException ignored) {
            Log.w("MediaController", "already removed");
        }
    }

    @Override
    public int setProgress() {
        if (isDragging) {
            return 0;
        }

        int position = videoView.getCurrentPosition();
        int duration = getDuration();
        if (progress != null) {
            if (duration > 0) {
                // Use long to avoid overflow
                long pos = Constants.ONE_MILLISECOND * position / duration;
                progress.setProgress((int) pos);
            }

            int percent = videoView.getBufferPercentage();
            progress.setSecondaryProgress(percent * 10);
        }

        if (endTime != null) {
            endTime.setText(stringForTime(duration));
        }

        if (currentTime != null) {
            currentTime.setText(stringForTime(position));
        }

        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        show(DEFAULT_CONTROLLER_TIMEOUT);
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        setButtonsEnabled(enabled);

        if (progress != null) {
            progress.setEnabled(enabled);
        }

        setupButtonsVisibility();
        super.setEnabled(enabled);
    }

    private void setButtonsEnabled(boolean isEnabled) {
        if (startPauseButton != null) {
            startPauseButton.setEnabled(isEnabled);
        }

        if (fastForwardButton != null) {
            fastForwardButton.setEnabled(isEnabled);
        }

        if (rewindButton != null) {
            rewindButton.setEnabled(isEnabled);
        }

        playbackSpeedManager.setPlaybackSpeedButtonEnabled(isEnabled);
    }

    /**
     * Updates the pause/play drawable of the video controller.
     */
    public void updatePausePlay() {
        if (startPauseButton != null) {
            boolean isPlaying = videoView.isPlaying();
            Drawable playPauseDrawable = drawableManager.getPlayPauseDrawable(isPlaying);
            startPauseButton.setImageDrawable(playPauseDrawable);
        }
    }

    /**
     * Changes the isDragging value.
     *
     * @param isDragging indicates if the progress bar is being dragged or not.
     */
    public void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    /**
     * Refreshes the progress bar.
     */
    public void refreshProgress() {
        if (handler != null) {
            handler.refresh();
        }
    }

    /**
     * Gets the video duration.
     *
     * @return the video duration
     */
    public int getDuration() {
        return videoView.getDuration();
    }

    /**
     * Seeks to a preferred position.
     *
     * @param position the selected position
     */
    public void seekTo(int position) {
        videoView.seekTo(position);
    }

    /**
     * Changes the current time.
     *
     * @param position the selected position
     */
    public void setCurrentTime(int position) {
        if (currentTime != null) {
            currentTime.setText(stringForTime(position));
        }
    }

    /**
     * Updates the SeekBar progress.
     *
     * @param position the selected position
     */
    public void updateSeekBarProgress(long position) {
        if (mediaControllerListener != null) {
            mediaControllerListener.onSeekBarProgressChanged(position);
        }
    }

    /**
     * Hides the video thumbnail image.
     */
    public void hideThumbnail() {
        videoView.hideThumbnail();
    }

    private void setupButtonListeners() {
        startPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaControllerListener != null) {
                    if (videoView.isPlaying()) {
                        mediaControllerListener.onPauseClicked();
                    } else {
                        mediaControllerListener.onPlayClicked();
                    }
                }

                doPauseResume();
                show(DEFAULT_CONTROLLER_TIMEOUT);
            }
        });

        fullscreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaControllerListener != null) {
                    mediaControllerListener.onFullscreenClicked();
                }

                view.setTag(VIEW_TAG_CLICKED);
                videoView.toggleFullscreen();
                show(DEFAULT_CONTROLLER_TIMEOUT);
            }
        });

        fastForwardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaControllerListener != null) {
                    mediaControllerListener.onFastForwardClicked();
                }

                videoView.seekBy(fastForwardDuration);
                setProgress();

                show(DEFAULT_CONTROLLER_TIMEOUT);
            }
        });

        rewindButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaControllerListener != null) {
                    mediaControllerListener.onRewindClicked();
                }

                videoView.seekBy(-rewindDuration);
                setProgress();

                show(DEFAULT_CONTROLLER_TIMEOUT);
            }
        });

        playbackSpeedManager.setPlaybackSpeedButtonOnClickListener(
                new PlaybackSpeedPopupMenuListener() {
                    @Override
                    public void onSpeedSelected(float speed, String text) {
                        // Update the Playback Speed Drawable according to the clicked menu item
                        playbackSpeedManager.setPlaybackSpeedText(text);
                        // Change the Playback Speed of the VideoMediaPlayer
                        videoView.changePlaybackSpeed(speed);
                        // Hide the VideoControllerView
                        hide();
                    }

                    @Override
                    public void onPopupMenuDismissed() {
                        show();
                    }

                    @Override
                    public void onPopupMenuShown() {
                        // Show the VideoControllerView and until hide is called
                        show(0);
                    }
                }
        );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        show(Constants.ONE_HOUR_MILLISECONDS);
        setIsDragging(true);

        // By removing these pending progress messages we make sure
        // that a) we won't update the progress while the user adjusts
        // the seekbar and b) once the user is done dragging the thumb
        // we will post one of these messages to the queue again and
        // this ensures that there will be exactly one message queued up.
        refreshProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
            return;
        }

        long duration = getDuration();
        long newPosition = (duration * progress) / Constants.ONE_MILLISECOND;

        seekTo((int) newPosition);
        setCurrentTime((int) newPosition);
        updateSeekBarProgress(newPosition);
        hideThumbnail();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setIsDragging(false);
        setProgress();
        updatePausePlay();
        show(DEFAULT_CONTROLLER_TIMEOUT);

        // Ensure that progress is properly updated in the future,
        // the call to show() does not guarantee this because it is a
        // no-op if we are already showing.
        refreshProgress();
    }

    private void setupXmlAttributes(AttributeSet attrs) {
        TypedArray styledAttrs = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.VideoControllerView,
                0,
                0
        );

        drawableManager = new ControllerDrawableManager(getContext(), styledAttrs);

        setupDrawables();
        setupProgressBar(styledAttrs);
        // Recycle the attributes
        styledAttrs.recycle();
    }

    private void setupDrawables() {
        // StartPause Button
        Drawable playDrawable = drawableManager.getPlayDrawable();
        startPauseButton.setImageDrawable(playDrawable);
        // Fullscreen Button
        Drawable enterFullscreenDrawable = drawableManager.getEnterFullscreenDrawable();
        fullscreenButton.setImageDrawable(enterFullscreenDrawable);
        // Rewind Button
        Drawable rewindDrawable = drawableManager.getRewindDrawable();
        rewindButton.setImageDrawable(rewindDrawable);
        // FastForward Button
        Drawable fastForwardDrawable = drawableManager.getFastForwardDrawable();
        fastForwardButton.setImageDrawable(fastForwardDrawable);
    }

    private void setupProgressBar(TypedArray a) {
        int color = a.getColor(R.styleable.VideoControllerView_progress_color, 0);
        if (color != 0) {
            // Set the default color
            progressBarColor = color;
        }
        setProgressBarDrawablesColors();
    }

    private void setProgressBarDrawablesColors() {
        setColorFilter(progress.getProgressDrawable(), progressBarColor);
        setColorFilter(progress.getThumb(), progressBarColor);
    }

    private void setColorFilter(@NonNull Drawable drawable, int color) {
        drawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(DEFAULT_CONTROLLER_TIMEOUT);
    }

    /**
     * Change the buttons visibility according to the flags in {@link FullscreenVideoMediaPlayer}.
     */
    private void setupButtonsVisibility() {
        if (startPauseButton != null && !videoView.canPause()) {
            startPauseButton.setEnabled(false);
            startPauseButton.setVisibility(INVISIBLE);
        }

        if (rewindButton != null && !seekBackwardButtonVisible) {
            rewindButton.setEnabled(false);
            rewindButton.setVisibility(INVISIBLE);
        }

        if (fastForwardButton != null && !seekForwardButtonVisible) {
            fastForwardButton.setEnabled(false);
            fastForwardButton.setVisibility(INVISIBLE);
        }

        playbackSpeedManager.hidePlaybackButton(playbackSpeedButtonVisible);
    }

    private static CharSequence stringForTime(int timeMs) {
        int totalSeconds = timeMs / Constants.ONE_SECOND_MILLISECONDS;
        int seconds = totalSeconds % Constants.ONE_MINUTE_SECONDS;
        int minutes = (totalSeconds / Constants.ONE_MINUTE_SECONDS) % Constants.ONE_MINUTE_SECONDS;
        int hours = totalSeconds / Constants.ONE_HOUR_SECONDS;
        return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
    }

    private void doPauseResume() {
        videoView.hideThumbnail();
        videoView.onPauseResume();
        updatePausePlay();
    }

    public void onDetach() {
        if (handler != null) {
            handler.onDestroy();
            handler = null;
        }

        videoView = null;
        mediaControllerListener = null;
        getViewTreeObserver().removeOnWindowFocusChangeListener(onWindowFocusChangeListener);
    }

    public void setEnterFullscreenDrawable(Drawable enterFullscreenDrawable) {
        drawableManager.setEnterFullscreenDrawable(enterFullscreenDrawable);
    }

    public void setExitFullscreenDrawable(Drawable exitFullscreenDrawable) {
        drawableManager.setExitFullscreenDrawable(exitFullscreenDrawable);
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = ContextCompat.getColor(getContext(), progressBarColor);
    }

    public void setPlayDrawable(Drawable playDrawable) {
        drawableManager.setPlayDrawable(playDrawable);
    }

    public void setPauseDrawable(Drawable pauseDrawable) {
        drawableManager.setPauseDrawable(pauseDrawable);
    }

    public void setFastForwardDuration(int fastForwardDuration) {
        this.fastForwardDuration = fastForwardDuration * 1000;
    }

    public void setRewindDuration(int rewindDuration) {
        this.rewindDuration = rewindDuration * 1000;
    }

    public void setFastForwardDrawable(Drawable fastForwardDrawable) {
        drawableManager.setFastForwardDrawable(fastForwardDrawable);
    }

    public void setRewindDrawable(Drawable rewindDrawable) {
        drawableManager.setRewindDrawable(rewindDrawable);
    }

    public void setPlaybackSpeedOptions(PlaybackSpeedOptions playbackSpeedOptions) {
        playbackSpeedManager.setPlaybackSpeedOptions(playbackSpeedOptions);
    }

    public void setOnMediaControllerListener(MediaControllerListener mediaControllerListener) {
        this.mediaControllerListener = mediaControllerListener;
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void hideProgress() {
        currentTime.setVisibility(View.GONE);
        endTime.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    public void hideFullscreenButton() {
        fullscreenButton.setVisibility(View.GONE);
    }

    void updateFullScreenDrawable() {
        if (fullscreenButton != null) {
            boolean isLandscape = videoView.isLandscape();
            Drawable fullscreenDrawable = drawableManager.getFullscreenDrawable(isLandscape);
            fullscreenButton.setImageDrawable(fullscreenDrawable);
        }
    }

    private void updateFastForwardDrawable() {
        if (fastForwardButton != null) {
            Drawable fastForwardDrawable = drawableManager.getFastForwardDrawable();
            fastForwardButton.setImageDrawable(fastForwardDrawable);
        }
    }

    private void updateRewindDrawable() {
        if (rewindButton != null) {
            Drawable rewindDrawable = drawableManager.getRewindDrawable();
            rewindButton.setImageDrawable(rewindDrawable);
        }
    }

    public boolean isSeekForwardButtonVisible() {
        return seekForwardButtonVisible;
    }

    public void setSeekForwardButtonVisible(boolean seekForwardButtonVisible) {
        this.seekForwardButtonVisible = seekForwardButtonVisible;
    }

    public boolean isSeekBackwardButtonVisible() {
        return seekBackwardButtonVisible;
    }

    public void setSeekBackwardButtonVisible(boolean seekBackwardButtonVisible) {
        this.seekBackwardButtonVisible = seekBackwardButtonVisible;
    }

    public boolean isPlaybackSpeedButtonVisible() {
        return playbackSpeedButtonVisible;
    }

    public void setPlaybackSpeedButtonVisible(boolean playbackSpeedButtonVisible) {
        this.playbackSpeedButtonVisible = playbackSpeedButtonVisible;
    }
}
