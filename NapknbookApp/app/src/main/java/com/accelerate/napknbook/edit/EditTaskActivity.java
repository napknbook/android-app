package com.accelerate.napknbook.edit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.models.UpdateTaskRequestBody;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskViewModel;
import com.accelerate.napknbook.viewmodels.TaskViewModelFactory;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EditTaskActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private TextView dueDateTextView, highPriorityTextView, clearDueDateButton, doneTextView, closeTextView;
    private View dueDateContainer;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private TaskViewModel taskViewModel;

    private String taskPk, characterPk, categoryPk, authToken;
    private boolean isHighPriority = false;
    private Calendar selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken = sharedPreferencesHelper.getAuthToken();

        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(new TaskRepository(this))).get(TaskViewModel.class);

        observeViewModel();

        // Get old task data
        taskPk = getIntent().getStringExtra("taskPk");
        characterPk = getIntent().getStringExtra("characterPk");
        categoryPk = getIntent().getStringExtra("categoryPk");

        String oldTitle = getIntent().getStringExtra("title");
        String oldDescription = getIntent().getStringExtra("description");
        String oldDueDate = getIntent().getStringExtra("dueDate");
        String oldPriority = getIntent().getStringExtra("priority");

        // Bind UI
        titleEditText = findViewById(R.id.taskTitleEditText);
        descriptionEditText = findViewById(R.id.descEditText);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        clearDueDateButton = findViewById(R.id.clearDueDateButton);
        dueDateContainer = findViewById(R.id.dueDateContainer);
        highPriorityTextView = findViewById(R.id.highPriorityTextView2);
        doneTextView = findViewById(R.id.doneTextView);
        closeTextView = findViewById(R.id.closeTextView);

        // Prefill fields
        titleEditText.setText(oldTitle);
        descriptionEditText.setText(oldDescription);
        dueDateTextView.setText(oldDueDate != null ? oldDueDate : "Add date/time");

        isHighPriority = "high".equalsIgnoreCase(oldPriority);
        updateHighPriorityUI();

        highPriorityTextView.setOnClickListener(v -> {
            isHighPriority = !isHighPriority;
            updateHighPriorityUI();
        });

        dueDateTextView.setOnClickListener(v -> showDatePicker());
        clearDueDateButton.setOnClickListener(v -> clearDueDate());

        doneTextView.setOnClickListener(v -> submitEditedTask());
        closeTextView.setOnClickListener(v -> finish());

        updateDoneButtonState();
        titleEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                updateDoneButtonState();
            }
        });
    }

    private void observeViewModel() {
        taskViewModel.getUpdateStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                setResult(RESULT_OK);
                finish();
            }
        });

        taskViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        taskViewModel.getUpdateStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void submitEditedTask() {
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedDesc = descriptionEditText.getText().toString().trim();

        if (updatedTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        String dueDate = null;
        if (selectedDate != null) {
            dueDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH) + 1,
                    selectedDate.get(Calendar.DAY_OF_MONTH));
        }

        Task updatedTask = new Task();
        updatedTask.setPk(taskPk);
        updatedTask.setTitle(updatedTitle);
        updatedTask.setDescription(updatedDesc);
        updatedTask.setDueDate(dueDate);
        updatedTask.setPriority(isHighPriority ? "high" : "medium");
        updatedTask.setStatus("pending");
        updatedTask.setCompleted(false);
        updatedTask.setCharacterPk(characterPk);
        updatedTask.setCategoryPk(categoryPk);

        taskViewModel.updateTask(updatedTask, authToken);
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(y, m, d);

            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            dueDateTextView.setText(formattedDate);
            clearDueDateButton.setVisibility(View.VISIBLE);
            dueDateContainer.setAlpha(0f);
            dueDateContainer.setVisibility(View.VISIBLE);
            dueDateContainer.animate().alpha(1f).setDuration(300).start();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void clearDueDate() {
        selectedDate = null;
        dueDateTextView.setText("Add date/time");
        clearDueDateButton.setVisibility(View.GONE);
    }

    private void updateHighPriorityUI() {
        highPriorityTextView.setText(isHighPriority ? "❗" : "❕");
        highPriorityTextView.setTextColor(ContextCompat.getColor(this,
                isHighPriority ? R.color.high_priority_red : R.color.grey));
    }

    private void updateDoneButtonState() {
        boolean hasText = !titleEditText.getText().toString().trim().isEmpty();
        doneTextView.setEnabled(hasText);
        doneTextView.setAlpha(hasText ? 1f : 0.5f);
    }

    // Optionally, add a base class or interface for reusable text watcher
    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
