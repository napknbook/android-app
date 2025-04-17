package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.accelerate.napknbook.models.Agent;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    Button homeButton;
    Button gearButton ;
    Button skillsButton ;

    View profileFeedView ;
    ImageView profilePicture ;
    ImageView profileBanner ;

    TextView profileNameTextView ;
    TextView profileBioTextView ;
    Agent agent ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }


        agent = getIntent().getParcelableExtra("agent") ;



        profileFeedView = findViewById(R.id.profileFeedView);
        profilePicture = findViewById(R.id.profilePicture);
        profileBanner = findViewById(R.id.profileBanner);

        profileNameTextView = findViewById(R.id.profileName);
        profileNameTextView.setText(agent.getName());

        profileBioTextView = findViewById(R.id.profileBio);
        profileBioTextView.setText(agent.getBio());

        String pp_filename = agent.getCharacterType() + "_pp.webp" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(pp_filename);

        Glide.with(this)
                .load(resourceId)
                .transform(new CircleCrop())
                .into(profilePicture);



        setupButtons();
        homeButton.callOnClick();



    }

    @Override
    protected void onResume() {
        super.onResume();
        clearButtons();
        homeButton.setTextColor(Color.RED);
    }

    void setupButtons() {


        homeButton = findViewById(R.id.skillHomeButton);
        gearButton = findViewById(R.id.skillLeaderboardsButton);
        skillsButton = findViewById(R.id.skillShortsButton);


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearButtons();
                homeButton.setTextColor(Color.RED);
                profileFeedView.setVisibility(View.VISIBLE);



            }
        });
        gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearButtons();
                gearButton.setTextColor(Color.RED);
                Intent intent = new Intent(UserActivity.this, GearActivity.class);
                startActivity(intent);


            }
        });

        skillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearButtons();
                //skillsButton.setTextColor(Color.RED);
                //Intent intent = new Intent(UserActivity.this, SkillsActivity.class);
                //startActivity(intent);



            }
        });
        clearButtons();


    }

    void clearButtons() {

        homeButton.setTextColor(Color.WHITE);
        gearButton.setTextColor(Color.WHITE);
        skillsButton.setTextColor(Color.WHITE);


    }

}