package com.accelerate.napknbook.demo;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import androidx.media3.ui.PlayerView;

import com.accelerate.napknbook.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;



@UnstableApi public class BeirutExplosionDemoActivity extends AppCompatActivity {


    //VideoView minimapVideoView ;
    //VideoView internetMapVideoView ;
    //VideoView angleVideoView ;
    Button cancelDeepScanButton ;
    Button deepScanButton ;
    Button confirmDeepScanButton ;
    int activeAngle = 1;
    TextView closeButton ;
    ConstraintLayout internetmapConstraintLayout ;
    Button openInternetmapButton ;
    ConstraintLayout confirmDeepScanConstraintLayout ;
    Button angle1Button ;
    Button angle2Button ;
    Button angle3Button ;
    Button angle4Button ;
    Button angle5Button ;
    long currentPosition = 5000;
    PlayerView minimapPlayerView, internetmapPlayerView, anglePlayerView ;
    ExoPlayer minimapExoplayer, internetmapExoplayer, angleExoplayer ;
    ImageView profilePictureImageView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beirut_explosion_demo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        TextView ratingsTextView = findViewById(R.id.textView5);
        ratingsTextView.setPaintFlags(ratingsTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ratingsTextView.setText("Ratings");

        //minimapVideoView = findViewById(R.id.minimapVideoView);
        //angleVideoView = findViewById(R.id.angleVideoView);
        //internetMapVideoView = findViewById(R.id.internetMapVideoView);



        setupUI();

        setupExoplayers();

        startExoplayers(currentPosition);
        //startVideoViews(currentPosition);

    }


    @OptIn(markerClass = UnstableApi.class)
    void  setupExoplayers() {

        minimapPlayerView = findViewById(R.id.minimapPlayerView);
        internetmapPlayerView = findViewById(R.id.internetmapPlayerView);
        anglePlayerView = findViewById(R.id.anglePlayerView);

        minimapExoplayer = new ExoPlayer.Builder(this).build();
        internetmapExoplayer = new ExoPlayer.Builder(this).build();
        angleExoplayer = new ExoPlayer.Builder(this).build();

        minimapPlayerView.setPlayer(minimapExoplayer);
        internetmapPlayerView.setPlayer(internetmapExoplayer);
        anglePlayerView.setPlayer(angleExoplayer);

        minimapExoplayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        internetmapExoplayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        angleExoplayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);

        anglePlayerView.setShowFastForwardButton(false);
        anglePlayerView.setShowNextButton(false);
        anglePlayerView.setShowRewindButton(false);
        anglePlayerView.setShowPreviousButton(false);

        internetmapPlayerView.setShowFastForwardButton(false);
        internetmapPlayerView.setShowNextButton(false);
        internetmapPlayerView.setShowRewindButton(false);
        internetmapPlayerView.setShowPreviousButton(false);

        angleExoplayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying) {
                    internetmapExoplayer.play();
                    minimapExoplayer.play();
                }
                else {

                    internetmapExoplayer.pause();
                    minimapExoplayer.pause();

                }
            }
        });

        angleExoplayer.addListener(new Player.Listener() {
            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    currentPosition = angleExoplayer.getCurrentPosition();
                    minimapExoplayer.seekTo(currentPosition);
                    internetmapExoplayer.seekTo(currentPosition);
                }

            }
        });


    }





    void startExoplayers(long currentPosition) {


        String minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_1;
        String internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_1;
        String angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_1; //TODO FIX

        switch (activeAngle) {

            case 1:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_1;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_1;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_1;
                break;
            case 2:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_2;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_2;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_2;
                break;
            case 3:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_3;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_3;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_3;
                break;
            case 4:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_4;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_4;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_4;
                break;
            case 5:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_5;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_5;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_5;
                break;

        }

        MediaItem minimapMediaItem = MediaItem.fromUri(minimap_path);
        MediaItem internetmapMediaItem = MediaItem.fromUri(internetmap_path);
        MediaItem angleMediaItem = MediaItem.fromUri(angle_path);

        minimapExoplayer.setMediaItem(minimapMediaItem);
        internetmapExoplayer.setMediaItem(internetmapMediaItem);
        angleExoplayer.setMediaItem(angleMediaItem);

        minimapExoplayer.prepare();
        internetmapExoplayer.prepare();
        angleExoplayer.prepare();

        minimapExoplayer.seekTo(currentPosition);
        internetmapExoplayer.seekTo(currentPosition);
        angleExoplayer.seekTo(currentPosition);

        minimapExoplayer.play();
        internetmapExoplayer.play();
        angleExoplayer.play();



    }

    /*
    void startVideoViews(int currentPosition) {

        String minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_1;
        String internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_1;
        String angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_1;

        switch (activeAngle) {

            case 1:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_1;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_1;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_1;
                break;
            case 2:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_2;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_2;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_2;
                break;
            case 3:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_3;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_3;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_3;
                break;
            case 4:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_4;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_4;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_4;
                break;
            case 5:
                minimap_path = "android.resource://" + getPackageName() + "/" + R.raw.minimap_5;
                internetmap_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_internetmap_5;
                angle_path = "android.resource://" + getPackageName() + "/" + R.raw.beirut_explosion_5;
                break;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            minimapVideoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
            internetMapVideoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
            angleVideoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }

        minimapVideoView.setVideoURI(Uri.parse(minimap_path));
        internetMapVideoView.setVideoURI(Uri.parse(internetmap_path));
        angleVideoView.setVideoURI(Uri.parse(angle_path));

        minimapVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                minimapVideoView.start(); // Restart the video when it ends

            }
        });

        internetMapVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                internetMapVideoView.start(); // Restart the video when it ends
            }
        });

        angleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                angleVideoView.start(); // Restart the video when it ends
            }
        });

        minimapVideoView.seekTo(currentPosition);
        internetMapVideoView.seekTo(currentPosition);
        angleVideoView.seekTo(currentPosition);

        minimapVideoView.start();
        internetMapVideoView.start();
        angleVideoView.start();

    }


 */

    @Override
    protected void onResume() {
        super.onResume();
        //minimapExoplayer.play();
        //minimapVideoView.start();
        //angleVideoView.start();
        //internetMapVideoView.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release minimapExoplayer
        if (minimapExoplayer != null) {
            minimapExoplayer.release();
            minimapExoplayer = null;
        }

        // Release internetmapExoplayer
        if (internetmapExoplayer != null) {
            internetmapExoplayer.release();
            internetmapExoplayer = null;
        }

        // Release angleExoplayer
        if (angleExoplayer != null) {
            angleExoplayer.release();
            angleExoplayer = null;
        }
    }
    void setupUI() {

        profilePictureImageView = findViewById(R.id.botImageView);



        Glide.with(getApplicationContext())
                .load(R.drawable.profile_icon)
                .transform(new CircleCrop())
                .into(profilePictureImageView);


        internetmapConstraintLayout = findViewById(R.id.internetMapConstraintLayout);


        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });

        openInternetmapButton = findViewById(R.id.openInternetmapButton);
        openInternetmapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetmapConstraintLayout.setVisibility(View.VISIBLE);
            }
        });

        deepScanButton = findViewById(R.id.deepScanButton);
        deepScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeepScanConstraintLayout.setVisibility(View.VISIBLE);
            }
        });

        confirmDeepScanConstraintLayout = findViewById(R.id.confirmDeepScanConstraintLayout);
        cancelDeepScanButton = findViewById(R.id.cancelDeepScanButton);
        cancelDeepScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeepScanConstraintLayout.setVisibility(View.GONE);
            }
        });

        angle1Button = findViewById(R.id.angle1Button);
        angle1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = angleExoplayer.getCurrentPosition();
                activeAngle = 1 ;
                startExoplayers(currentPosition);
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });

        angle2Button = findViewById(R.id.angle2Button);
        angle2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = angleExoplayer.getCurrentPosition();
                activeAngle = 2 ;
                startExoplayers(currentPosition);
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });

        angle3Button = findViewById(R.id.angle3Button);
        angle3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = angleExoplayer.getCurrentPosition();
                activeAngle = 3 ;
                startExoplayers(currentPosition);
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });

        angle4Button = findViewById(R.id.angle4Button);
        angle4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = angleExoplayer.getCurrentPosition();
                activeAngle = 4 ;
                startExoplayers(currentPosition);
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });

        angle5Button = findViewById(R.id.angle5Button);
        angle5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = angleExoplayer.getCurrentPosition();
                activeAngle = 5 ;
                startExoplayers(currentPosition);
                internetmapConstraintLayout.setVisibility(View.GONE);
            }
        });
    }



}