package com.accelerate.napknbook.add;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.R;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.models.SkillRequestBody;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddInventoryActivity extends AppCompatActivity {

    Button addInventoryButton;
    EditText skillNameEditText ;
    TextInputLayout skillNameTextInputLayout ;


    Slider skillLevelSlider ;
    TextView skillLevelTextView ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    User user;
    int mainCharacterindex ;
    ImageView goldImageView ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_inventory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        skillNameEditText = findViewById(R.id.skillNameEditText);
        skillLevelSlider = findViewById(R.id.skillLevelSlider);

        skillLevelTextView = findViewById(R.id.skillLevelTextView);
        skillLevelTextView.setText(Integer.toString(Math.round(skillLevelSlider.getValue())));

        skillLevelSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                int skillLevel = Math.round(value);
                skillLevelTextView.setText(Integer.toString(skillLevel));
            }
        });

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);

        authToken[0] = sharedPreferencesHelper.getAuthToken();

        goldImageView = findViewById(R.id.goldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);



        addInventoryButton = findViewById(R.id.addSkillButton);
        addInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String skillName = String.valueOf(skillNameEditText.getText());
                int skillLevel = Math.round(skillLevelSlider.getValue()) ;
                String characterPk = user.getCharacters().get(mainCharacterindex).getPk();
                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                SkillRequestBody skillRequestBody = new SkillRequestBody(skillName, skillLevel, characterPk);

                Call<Skill> call = service.generateSkill("Basic " + authToken[0], csrfToken[0], skillRequestBody);

                call.enqueue(new Callback<Skill>() {
                    @Override
                    public void onResponse(Call<Skill> call, Response<Skill>response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Skill skill = response.body();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                            // Save to local storage or update UI
                        } else {
                            // Handle errors (e.g., insufficient funds, invalid input)
                            Log.e("Skill Generation", "Error: " + response.code() + " " + response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<Skill> call, Throwable t) {
                        Log.e("Skill Generation", "Failed to call API: " + t.getMessage());
                    }
                });

                //Intent intent = new Intent();
                //setResult(Activity.RESULT_OK, intent);
                //finish();



            }
        });








    }


}
