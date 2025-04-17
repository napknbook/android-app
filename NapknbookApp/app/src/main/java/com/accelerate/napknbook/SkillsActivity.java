package com.accelerate.napknbook;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.accelerate.napknbook.add.AddSkillActivity;
import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.Skill;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import androidx.activity.result.ActivityResultLauncher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SkillsActivity extends AppCompatActivity {

    FlexboxLayout flexboxLayout ;
    View skillCardView ;
    FloatingActionButton addSkillFAB;
    private static final int RC_ADD_SKILL = 9003;
    ArrayList<Skill> skills ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] authToken = new String[1];
    //Character mainCharacter ;
    int mainCharacterIndex ;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        Toolbar toolbar = findViewById(R.id.skillsToolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.getNavigationIcon().setTint(Color.WHITE);


        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken[0] = sharedPreferencesHelper.getAuthToken();
        flexboxLayout = findViewById(R.id.SkillsFlexbox);



        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);

        Call<List<Skill>> call = service.getSkills("Basic " + authToken[0], "");
        call.enqueue(new Callback<List<Skill>>() {
            @Override
            public void onResponse(Call<List<Skill>> call, Response<List<Skill>> response) {

                skills = new ArrayList<>(response.body()) ;





                for (Skill skill : skills) {

                    View skillCard = LayoutInflater.from(getApplicationContext()).inflate(R.layout.skill_card_layout_v2, flexboxLayout, false);
                    TextView skillTextView = skillCard.findViewById(R.id.skillNameTextView) ;
                    TextView levelTextView = skillCard.findViewById(R.id.skillLevelTextView);
                    ImageView skillImageView = skillCard.findViewById(R.id.skillImageView);

                    skillTextView.setText(StringUtils.capitalize(skill.getName()));
                    levelTextView.setText("Lvl " + String.valueOf(skill.getLevel()));


                    Glide.with(getApplicationContext())
                            .load(skill.getImgUrl())
                            //.transform(new CircleCrop())
                            .into(skillImageView);

                    skillCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(SkillsActivity.this, SkillActivity.class);
                            startActivity(intent);

                        }
                    });
                    flexboxLayout.addView(skillCard);
                }

            }

            @Override
            public void onFailure(Call<List<Skill>> call, Throwable t) {

                String test = "sas" ;
            }
        });








        /*
        for (int i=0; i<29; i++) {
            View skillCardView = LayoutInflater.from(this).inflate(R.layout.skill_card_layout, flexboxLayout, false);
            skillCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(SkillsActivity.this, SkillActivity.class);
                    intent.putExtra("skillName", "Cookiing");
                    startActivity(intent);
                }
            });
            flexboxLayout.addView(skillCardView);
        }


         */

        // Add Total Level View
        //View totalLevelView = LayoutInflater.from(this).inflate(R.layout.total_skill_level_card_layout, flexboxLayout, false);
        //flexboxLayout.addView(totalLevelView);

        // Step 1: Define the launcher for receiving results
        ActivityResultLauncher<Intent> addSkillLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Skill skill = data.getParcelableExtra("skill");

                        if (skill != null) {

                            // Add a view or update UI with the skill data
                            View loadingSkillCardView = LayoutInflater.from(this)
                                    .inflate(R.layout.loading_skill_card_layout, flexboxLayout, false);
                            TextView skillTextView = loadingSkillCardView.findViewById(R.id.skillNameTextView) ;
                            TextView levelTextView = loadingSkillCardView.findViewById(R.id.skillLevelTextView);
                            skillTextView.setText(StringUtils.capitalize(skill.getName()));
                            levelTextView.setText("Lvl " + String.valueOf(skill.getLevel()));


                            loadingSkillCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(SkillsActivity.this, SkillActivity.class);
                                    startActivity(intent);

                                }
                            });
                            flexboxLayout.addView(loadingSkillCardView);
                        }

                    }
                });


        addSkillFAB = findViewById(R.id.addSkillFAB);
        addSkillFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsActivity.this, AddSkillActivity.class);

                addSkillLauncher.launch(intent);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADD_SKILL && resultCode == RESULT_OK) {

            Skill skill = getIntent().getParcelableExtra("skill");
            skills.add(skill);

            View skillCardView = LayoutInflater.from(this).inflate(R.layout.skill_card_layout, flexboxLayout, false);
            skillCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(SkillsActivity.this, SkillActivity.class);
                    intent.putExtra("skillName", skill.getName());
                    startActivity(intent);
                }
            });
            flexboxLayout.addView(skillCardView);




        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back arrow click here
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}