package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.VideoView;

import com.accelerate.napknbook.models.Agent;
import com.accelerate.napknbook.utils.ResourceMapSingleton;

import java.util.HashMap;

public class GearActivity extends AppCompatActivity {


    VideoView videoView ;
    Agent agent ;
    String aspectRatioString ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        agent = getIntent().getParcelableExtra("agent");


        // Get display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Get closest aspect ratio
        int aspectRatio = (int)(((float) height / width)*100) ;
        int closest = findClosestAspectRatio(aspectRatio);

        aspectRatioString = Integer.toString(findClosestAspectRatio(aspectRatio));

        startVideoView();
    }


    public int findClosestAspectRatio(int aspectRatio) {

        int[] aspectRatios = {177, 200, 216, 222, 112, 150} ;

        int closest = aspectRatios[0] ;
        int minDiff = Math.abs(aspectRatios[0] - aspectRatio);

        for (int i = 1; i < aspectRatios.length; i++) {
            int diff = Math.abs(aspectRatios[i] - aspectRatio);
            if (diff < minDiff) {
                minDiff = diff;
                closest = aspectRatios[i];
            }
        }


        return closest ;
    }

    void startVideoView() {



        String characterClipFilename = agent.getCharacterType() + "_workshop" + "_" + aspectRatioString + ".mp4" ;


        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(characterClipFilename);

        videoView = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + resourceId;
        //String path = "android.resource://" + getPackageName() + "/" + R.raw.zarrari;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }
        videoView.setVideoURI(Uri.parse(path));



        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start(); // Restart the video when it ends
            }
        });


        videoView.start();

    }
}