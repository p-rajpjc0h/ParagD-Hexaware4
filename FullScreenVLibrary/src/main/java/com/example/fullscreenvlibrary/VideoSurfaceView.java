

package com.example.fullscreenvlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

class VideoSurfaceView extends SurfaceView {
    private int previousHeight;
    private int previousWidth;

    public VideoSurfaceView(Context context) {
        super(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateLayoutParams(int videoWidth, int videoHeight) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        resetLayoutParams(layoutParams);
        previousHeight = layoutParams.height;
        previousWidth = layoutParams.width;
        // Get the Display Metrics
        DeviceDimensionsManager deviceDimensionsManager = DeviceDimensionsManager.getInstance();
        // Get the width of the screen
        Context context = getContext();
        int screenWidth = deviceDimensionsManager.getDisplayWidth(context);
        int screenHeight = deviceDimensionsManager.getDisplayHeight(context);
        // Get the SurfaceView layout parameters
        FrameLayout.LayoutParams surfaceViewLayoutParams = (FrameLayout.LayoutParams) layoutParams;
        if ((float) videoHeight / screenHeight > (float) videoWidth / screenWidth) {
            surfaceViewLayoutParams.height = screenHeight;
            // Set the width of the SurfaceView to match the aspect ratio of the video
            // be sure to cast these as floats otherwise the calculation will likely be 0
            surfaceViewLayoutParams.width = (int) (((float) videoWidth / videoHeight) * screenHeight);
        } else {
            // Set the width of the SurfaceView to the width of the screen
            surfaceViewLayoutParams.width = screenWidth;
            // Set the height of the SurfaceView to match the aspect ratio of the video
            // be sure to cast these as floats otherwise the calculation will likely be 0
            surfaceViewLayoutParams.height = (int) (((float) videoHeight / videoWidth) * screenWidth);
        }
        // Change the gravity to center
        surfaceViewLayoutParams.gravity = Gravity.CENTER;
        // Commit the layout parameters
        setLayoutParams(surfaceViewLayoutParams);
    }

    private void resetLayoutParams(ViewGroup.LayoutParams layoutParams) {
        FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) layoutParams;
        frameLayoutParams.height = previousHeight;
        frameLayoutParams.width = previousWidth;
        setLayoutParams(layoutParams);
    }
}
