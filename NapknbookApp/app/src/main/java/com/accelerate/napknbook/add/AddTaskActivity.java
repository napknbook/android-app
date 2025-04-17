package com.accelerate.napknbook.add;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.R;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    TextView closeTextView ;
    TextView doneTextView ;

    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    User user;
    int mainCharacterindex ;
    ImageView goldImageView ;
    EditText categoryEditText ;
    TextView highPriorityTextView;

    boolean isHighPriority = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        final String[] csrfToken = new String[1];
        authToken[0] = sharedPreferencesHelper.getAuthToken();


        updateHighPriorityUI();

        highPriorityTextView = findViewById(R.id.highPriorityTextView2);
        highPriorityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHighPriority = !isHighPriority;
                updateHighPriorityUI();
            }
        });



        categoryEditText = findViewById(R.id.categoryEditText);
        closeTextView = findViewById(R.id.closeTextView);

        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneTextView = findViewById(R.id.doneTextView);
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send request to add category to database

                // Add category to local json


                String taskCategory = String.valueOf(categoryEditText.getText());
                String characterPk = user.getCharacters().get(mainCharacterindex).getPk();
                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);


                Call<Skill> call = service.generateSkill("Basic " + authToken[0], csrfToken[0], null);

                call.enqueue(new Callback<Skill>() {
                    @Override
                    public void onResponse(Call<Skill> call, Response<Skill> response) {
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

    private void updateHighPriorityUI() {
        if (isHighPriority) {
            highPriorityTextView.setText("❗");
            //.setTextColor(getResources().getColor(R.color.high_priority_red)); // use bold red
        } else {
            highPriorityTextView.setText("❕");
            highPriorityTextView.setTextColor(getResources().getColor(R.color.grey)); // use subtle grey
        }
    }
}