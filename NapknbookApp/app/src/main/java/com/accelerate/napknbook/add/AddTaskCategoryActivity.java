package com.accelerate.napknbook.add;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.R;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.models.TaskCategory;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModel;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModelFactory;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class AddTaskCategoryActivity extends AppCompatActivity {

    private TextView closeTextView, doneTextView;
    private EditText categoryEditText;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String authToken;
    private User user;

    private TaskCategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        // 🔐 Setup
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken = sharedPreferencesHelper.getAuthToken();
        user = sharedPreferencesHelper.getUser();

        initViews();
        setupViewModel();
        setupListeners();
    }


    private void initViews() {
        closeTextView = findViewById(R.id.closeTextView);
        doneTextView = findViewById(R.id.doneTextView);
        categoryEditText = findViewById(R.id.categoryEditText);
    }


    private void setupViewModel() {
        TaskCategoryRepository repository = new TaskCategoryRepository(this);
        TaskCategoryViewModelFactory factory = new TaskCategoryViewModelFactory(repository);
        categoryViewModel = new ViewModelProvider(this, factory).get(TaskCategoryViewModel.class);

        categoryViewModel.getCreationStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                String categoryName = categoryEditText.getText().toString().trim();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("newCategoryName", categoryName);

                setResult(RESULT_OK, resultIntent);
                finish();
            }

        });

        categoryViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                if (error.contains("409") || error.toLowerCase().contains("already exists")) {
                    categoryEditText.setError("A category with this name already exists");
                } else {
                    Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupListeners() {
        closeTextView.setOnClickListener(v -> finish());

        doneTextView.setOnClickListener(v -> {
            String name = categoryEditText.getText().toString().trim();

            if (name.isEmpty()) {
                categoryEditText.setError("Required");
                return;
            }

            if (user == null || user.getCharacters() == null || user.getCharacters().isEmpty()) {
                Toast.makeText(this, "User or character not loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            String characterPk = sharedPreferencesHelper.getMainCharacterPk();

            // 🚀 Trigger ViewModel to create
            categoryViewModel.createTaskCategory(authToken, name, characterPk);
        });
    }
}
