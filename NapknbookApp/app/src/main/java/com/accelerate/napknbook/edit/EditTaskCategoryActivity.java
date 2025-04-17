package com.accelerate.napknbook.edit;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModel;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModelFactory;

public class EditTaskCategoryActivity extends AppCompatActivity {

    private EditText categoryEditText;
    private TextView doneTextView, closeTextView;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private TaskCategoryViewModel categoryViewModel;

    private String authToken;
    private String categoryPk;
    private String oldCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken = sharedPreferencesHelper.getAuthToken();
        categoryPk = getIntent().getStringExtra("categoryPk");
        oldCategoryName = getIntent().getStringExtra("categoryName"); // 👈 Get old name

        if (TextUtils.isEmpty(categoryPk)) {
            Toast.makeText(this, "Missing category identifier", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
    }

    private void initViews() {
        categoryEditText = findViewById(R.id.categoryEditText);
        doneTextView = findViewById(R.id.doneTextView);
        closeTextView = findViewById(R.id.closeTextView);

        if (!TextUtils.isEmpty(oldCategoryName)) {
            categoryEditText.setText(oldCategoryName); // 👈 Pre-fill EditText
        }

        closeTextView.setOnClickListener(v -> finish());

        doneTextView.setOnClickListener(v -> {
            String newName = categoryEditText.getText().toString().trim();

            if (newName.isEmpty()) {
                categoryEditText.setError("Name is required");
                return;
            }

            categoryViewModel.updateTaskCategory(authToken, categoryPk, newName);
        });
    }

    private void setupViewModel() {
        TaskCategoryRepository repository = new TaskCategoryRepository(this);
        TaskCategoryViewModelFactory factory = new TaskCategoryViewModelFactory(repository);
        categoryViewModel = new ViewModelProvider(this, factory).get(TaskCategoryViewModel.class);

        categoryViewModel.getUpdateStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        categoryViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
