package com.example.fullscreenvideoviewtemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.fullscreenvlibrary.FullscreenVideoView;
import com.example.fullscreenvlibrary.orientation.LandscapeOrientation;
import com.example.fullscreenvlibrary.orientation.PortraitOrientation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FullscreenVideoView videoView;
    ScheduledExecutorService scheduledExecutorService;
    int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.fullscreenVideoView_m);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        String urlPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4";

        Log.d("ANDROID MASTER","Came here man at last compleyte 1");
        videoView.videoUrl(urlPath)
                .progressBarColor(R.color.colorAccent)
                .enableAutoStart()
                .landscapeOrientation(LandscapeOrientation.SENSOR)
                .portraitOrientation(PortraitOrientation.DEFAULT)
                .thumbnail(R.drawable.video_thumbnail);

        videoView.onInfoListnerPrepared(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    Log.d("ANDROID MASTER","Came here man at last compleyte 2");
                    ScheduleToastService();
                }
                return false;
            }
        });
    }

    private void ScheduleToastService(){

        final Runnable UpdateCurrentPosition = new Runnable() {

            @Override
            public void run() {
                try {
                    Log.d("ANDROID MASTER","Came here man at last compleyte 3");
                    if (videoView.isPlaying()) {
                        Log.d("ANDROID MASTER","Came here man at last compleyte 4");
                        mCurrentPosition = videoView.getCurrentPosition();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ANDROID MASTER","Came here man at last compleyte 5");
                                Log.d("ANDROID MASTER","Came here man "+ mCurrentPosition);
                                Toast.makeText(MainActivity.this, String.valueOf(mCurrentPosition / 1000) + " seconds", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };

        Log.d("ANDROID MASTER","Came here man at last compleyte 6");

        final ScheduledFuture<?> beeperHandle =
                scheduledExecutorService.scheduleAtFixedRate(UpdateCurrentPosition, 10, 10, TimeUnit.SECONDS);

        scheduledExecutorService.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, videoView.getDuration(), TimeUnit.SECONDS);

        Log.d("ANDROID MASTER","Came here man at last compleyte 7");
    }
}