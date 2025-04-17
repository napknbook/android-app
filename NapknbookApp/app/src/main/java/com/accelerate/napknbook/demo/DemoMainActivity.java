package com.accelerate.napknbook.demo;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import com.accelerate.napknbook.GoldActivity;
import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.R;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.SettingsActivity;
import com.accelerate.napknbook.isUserActivity;
import com.accelerate.napknbook.models.Character;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DemoMainActivity extends AppCompatActivity {

    ArrayList<Skill> skillModels = new ArrayList<>();
    ImageView goldImageView ;
    View goldButton ;
    Button exploreButton ;
    Button gearButton ;
    Button skillsButton ;
    Button settingsButton ;
    Button connectButton ;
    Button confirmCharacterGenerationButton ;
    Button cancelCharacterGenerationButton ;
    ConstraintLayout confirmCharacterGenerationConstraintLayout;
    ConstraintLayout loadingSpinnerConstraintLayout ;
    User user;
    VideoView videoView ;
    TextView usernameTextView ;
    Character mainCharacter ;
    int mainCharacterIndex ;
    ArrayList<Character> characters ;
    Map<String, Integer> dictionary = new HashMap<>();
    AutoCompleteTextView autoCompleteTextView ;
    String[] characterNames ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    ArrayAdapter<String> adapter ;
    String aspectRatioString ;
    TextView walletBalanceTextView ;
    TextView helpTextView ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }



        // Get display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Get closest aspect ratio
        int aspectRatio = (int)(((float) height / width)*100) ;
        int closest = findClosestAspectRatio(aspectRatio);

        aspectRatioString = Integer.toString(closest);

        startVideoView();

        setUpButtons();

        ImageView goldImageView = findViewById(R.id.goldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);

        ImageView badgeImageView = findViewById(R.id.badgeImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(badgeImageView);

        ImageView verifiedBadgeImageView = findViewById(R.id.verifiedBadgeImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.logo)
                .transform(new CircleCrop())
                .into(verifiedBadgeImageView);

        ImageView earlyAdopterBadgeImageView = findViewById(R.id.earlyAdopterBadgeImageView);

        Glide.with(getApplicationContext())
                .load(R.raw.early_adopter_badge)
                .transform(new CircleCrop())
                .into(earlyAdopterBadgeImageView);

        ImageView confirmGoldImageView = findViewById(R.id.confirmMenuGoldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(confirmGoldImageView);

        confirmCharacterGenerationConstraintLayout = findViewById(R.id.confirmCharacterGenerationConstraintLayout);
        confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);

        loadingSpinnerConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);
        loadingSpinnerConstraintLayout.setVisibility(View.GONE);

        walletBalanceTextView = findViewById(R.id.walletBalanceTextView);

        ImageView generateCharacterImageView = findViewById(R.id.generateCharacterImageView);
        generateCharacterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmCharacterGenerationConstraintLayout.setVisibility(View.VISIBLE);

            }
        });

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setText("zarar");

    }



    void startVideoView() {

        String characterClipFilename = "guy0" + "_workshop" + "_" + aspectRatioString + ".mp4" ;

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

        // Ensure audio focus for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start(); // Restart the video when it ends
            }
        });


        videoView.start();

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

    public void saveEntity(User user) {

        Gson gson = new Gson();
        String json = gson.toJson(user);

    }

    @Override
    protected void onResume() {

        super.onResume();
        videoView.start();

    }

    void setUpButtons() {

        goldButton = findViewById(R.id.goldButton);
        exploreButton = findViewById(R.id.exploreButton);
        //gearButton = findViewById(R.id.mainGearButton);
        settingsButton = findViewById(R.id.settingsButton);
        skillsButton = findViewById(R.id.skillsButton);
        connectButton = findViewById(R.id.connectButton);
        confirmCharacterGenerationButton = findViewById(R.id.confirmCharacterGenerationButton);
        cancelCharacterGenerationButton = findViewById(R.id.cancelCharacterGenerationButton);

        helpTextView = findViewById(R.id.helpImageView);

        helpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@napknbook"));
                startActivity(browserIntent);
            }
        });


        goldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoMainActivity.this, GoldActivity.class);
                startActivity(intent);
            }
        });

        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoMainActivity.this, DemoExploreActivity.class);
                startActivity(intent);
            }
        });
        /*

        gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GearActivity.class);
                startActivity(intent);
            }
        });

        */
        skillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(DemoMainActivity.this, DemoSkillsActivity.class);
                Intent intent = new Intent(DemoMainActivity.this, isUserActivity.class);
                //intent.putParcelableArrayListExtra("skills", skills) ;
                startActivity(intent);

            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoMainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://napknbook.com/products/day-breaker"));
                startActivity(browserIntent);
                //Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                //startActivity(intent);
            }
        });
        cancelCharacterGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);
            }
        });
        confirmCharacterGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(DemoMainActivity.this, isUserActivity.class);
                startActivity(intent);

                loadingSpinnerConstraintLayout.setVisibility(View.VISIBLE);
                confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);

            }
        });
    }

    String[] getCharacterNames(User user) {

        String[] characterNames  = new String[user.getCharacters().size()];
        String entityName = user.getName();
        characterNames[0] = entityName;

            for (int i = 1; i < user.getCharacters().size(); i++) {
                String characterName = entityName + "." + Integer.toString(i) ;
                characterNames[i] = characterName ;
            }

        return characterNames ;
    }


    private void getCsrfToken() {


        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        Call<ResponseBody> call = service.getCsrfToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        csrfToken[0] = jsonObject.optString("csrfToken");

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }

}