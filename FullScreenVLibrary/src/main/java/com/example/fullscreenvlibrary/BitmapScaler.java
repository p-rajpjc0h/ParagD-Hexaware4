package com.example.fullscreenvlibrary;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class BitmapScaler {

    static Bitmap scaleImage(Resources resources, int thumbnailResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, thumbnailResId, options);

        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, thumbnailResId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int requiredHeight = 500;
        int requiredWidth = 500;

        if (height > requiredHeight || width > requiredWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= requiredHeight
                    && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
