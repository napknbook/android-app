package com.accelerate.napknbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.accelerate.napknbook.models.Convo;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SkillActivity extends AppCompatActivity {

    ArrayList<Convo> convos = new ArrayList<>();

    Button homeButton ;
    Button convosButton ;
    Button shortsButton ;
    Button leaderboardsButton ;
    RecyclerView recyclerView ;
    ImageView mainSkillImageView ;
    TextView mainSkillTextView ;
    TextView mainSkillDescTextView ;
    ImageView goldImageView ;
    Button confirmSkillGenerationButton ;
    Button cancelSkillGenerationButton ;
    Slider skillLevelSlider ;
    TextView skillLevelTextView ;
    ConstraintLayout loadingSpinnerConstraintLayout ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    Skill skill ;
    ConvoRecyclerViewAdapter adapter ;
    ConstraintLayout confirmSkillGenerationConstraintLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken[0] = sharedPreferencesHelper.getAuthToken();

        Intent intent = getIntent()  ;
        skill = intent.getParcelableExtra("skill");

        updateSkill();


        mainSkillImageView = findViewById(R.id.mainSkillImageView);

        goldImageView = findViewById(R.id.confirmMenuGoldImageView);


        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);



        Glide.with(this)
                .load(skill.getImgUrl())
                .into(mainSkillImageView);

        mainSkillTextView = findViewById(R.id.mainSkillTextView);
        //mainSkillTextView.setText(StringUtils.capitalize(skill.getName()));

        mainSkillDescTextView = findViewById(R.id.mainSkillDescTextView);
        //mainSkillDescTextView.setText(skill.getDesc());

        recyclerView = findViewById(R.id.convosRecyclerView);

        loadingSpinnerConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);
        loadingSpinnerConstraintLayout.setVisibility(View.GONE);

        confirmSkillGenerationConstraintLayout = findViewById(R.id.confirmSkillGenerationConstraintLayout);
        confirmSkillGenerationConstraintLayout.setVisibility(View.GONE);


        setUpButtons();

        setUpConversations();

        //convos.add(new Convo("tae", "fadsfasf", "fadsf"));
        //convos.add(new Convo("tae2", "fadsfasf2", "dasfds"));


        //adapter = new ConvoRecyclerViewAdapter(this, convos);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        skillLevelSlider = findViewById(R.id.skillLevelSlider);
        skillLevelSlider.setValue(skill.getLevel());
        skillLevelTextView = findViewById(R.id.skillLevelTextView);
        skillLevelTextView.setText(Integer.toString(Math.round(skillLevelSlider.getValue())));

        skillLevelSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                int skillLevel = Math.round(value);
                skillLevelTextView.setText(Integer.toString(skillLevel));
                //confirmSkillGenerationConstraintLayout.setVisibility(View.VISIBLE);
                //TextView offerTextView = findViewById(R.id.textView40);
                //offerTextView.setText("Generate " + skill.getName() + " lvl " + Integer.toString(skillLevel) + "?");

                confirmSkillGenerationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });
    }


    void updateSkill() {

//        UserEntity userEntity = sharedPreferencesHelper.getEntity();
//        String characterPk = userEntity.getCharacters().get(sharedPreferencesHelper.getMainCharacterIndex()).getPk() ;
//        String skillPk = skill.getPk() ;
//
//        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
//        Call<Skill> call = service.getSkill("Bearer " + authToken[0], characterPk, skillPk);
//        call.enqueue(new Callback<Skill> () {
//            @Override
//            public void onResponse(Call<Skill>  call, Response<Skill>  response) {
//
//                if (response.isSuccessful() && response.body() != null) {
//                    skill = response.body();
//                    mainSkillTextView.setText(StringUtils.capitalize(skill.getName()));
//                    mainSkillDescTextView.setText(skill.getDesc());
//                    // Save to local storage or update UI
//                } else {
//                    // Handle errors (e.g., insufficient funds, invalid input)
//                    Log.e("Skill Generation", "Error: " + response.code() + " " + response.message());
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<Skill>  call, Throwable t) {
//                // Handle failure
//            }
//        });

    }

    public void setUpConversations() {

        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        Call<List<Convo>> call = service.getConvos("Bearer " + authToken[0], skill.getPk());
        call.enqueue(new Callback<List<Convo>>() {
            @Override
            public void onResponse(Call<List<Convo>> call, Response<List<Convo>> response) {


                //convos.addAll(new ArrayList<>(response.body()));
                convos = new ArrayList<>(response.body()) ;
                adapter = new ConvoRecyclerViewAdapter(getApplicationContext(), convos);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                //adapter.notify();


            }

            @Override
            public void onFailure(Call<List<Convo>> call, Throwable t) {
                // Handle failure
            }
        });



    }

    void setUpButtons() {

        confirmSkillGenerationButton = findViewById(R.id.confirmSkillGenerationButton);
        cancelSkillGenerationButton = findViewById(R.id.cancelSkillGenerationButton);


        confirmSkillGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        homeButton = findViewById(R.id.skillHomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else if(recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }

            }
        });
    }

    void clearButtons() {

    }
}