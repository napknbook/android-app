package com.accelerate.napknbook;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.accelerate.napknbook.adapters.TaskCategoryPagerAdapter;
import com.accelerate.napknbook.add.AddTaskCategoryActivity;
import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.fragments.TaskCategoryFragment;
import com.accelerate.napknbook.models.CreateTaskRequestBody;
import com.accelerate.napknbook.models.TaskCategory;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModel;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModelFactory;
import com.accelerate.napknbook.viewmodels.TaskViewModel;
import com.accelerate.napknbook.viewmodels.TaskViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;


import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


import com.accelerate.napknbook.database.repositories.TaskRepository;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    private static final int RC_ADD_INVENTORY_MANUAL = 9003;
    private static final int RC_ADD_INVENTORY_AUTO = 9004;
    private static final int MANUAL_TASK_BUTTON_ID = 57;
    private static final int AUTO_TASK_BUTTON_ID = 59;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private TaskCategoryPagerAdapter pagerAdapter;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SpeedDialView speedDialView;

    private final ArrayList<TaskCategory> taskCategories = new ArrayList<>();
    private TabLayoutMediator tabLayoutMediator;
    private boolean isAdjustingPage = false;

    private Button cancelCompletedDeleteButton;
    private Button confirmCategoryDeleteButton;

    private Button confirmCompletedDeleteButton;
    private Button cancelCategoryDeleteButton;

    private TaskCategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel ;

    private ActivityResultLauncher<Intent> addCategoryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher ;

    private boolean suppressTabSelect = false;
    private String recentlyCreatedCategoryName = null;

    private boolean skipNextAddCategoryClick = false;



    private int addCategoryTabIndex = -1;
    private int lastSelectedTabIndex = -1;

    String token ;
    String characterPk ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);

        initViews();
        setupToolbar();
        setupTabClickListener();
        setupSpeedDial();

        // 🧠 ViewModel setup
        TaskCategoryRepository taskCategoryRepository = new TaskCategoryRepository(this);
        TaskCategoryViewModelFactory taskCategoryViewModelFactory = new TaskCategoryViewModelFactory(taskCategoryRepository);
        categoryViewModel = new ViewModelProvider(this, taskCategoryViewModelFactory).get(TaskCategoryViewModel.class);

        TaskRepository taskRepository = new TaskRepository(this);
        TaskViewModelFactory taskViewModelFactory = new TaskViewModelFactory(taskRepository);
        taskViewModel = new ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel.class);
        observeViewModel();

        // ⏬ Initial data load
        token = sharedPreferencesHelper.getAuthToken();
        characterPk = sharedPreferencesHelper.getMainCharacterPk();

        categoryViewModel.syncCategoriesFromServer(token, characterPk);
        taskViewModel.syncTasksFromServer(token, characterPk);

        categoryViewModel.loadCategories(token, characterPk);
        taskViewModel.loadTasks(token, characterPk);

        addCategoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newCategoryName = result.getData().getStringExtra("newCategoryName");

                        if (newCategoryName != null) {
                            recentlyCreatedCategoryName = newCategoryName;
                            sharedPreferencesHelper.saveActiveTaskCategoryName(newCategoryName);
                        }

                        // Reload from backend so new category appears
                        categoryViewModel.syncCategoriesFromServer(token, characterPk);
                    }
                }
        );


        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // 🔁 Re-fetch tasks and categories after image-based creation
                        categoryViewModel.syncCategoriesFromServer(token, characterPk);
                        taskViewModel.syncTasksFromServer(token, characterPk);

                    }
                }
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TasksActivity", "onResume triggered");

        String token = sharedPreferencesHelper.getAuthToken();
        String characterPk = sharedPreferencesHelper.getMainCharacterPk();

        if (token != null && characterPk != null) {
            categoryViewModel.loadCategories(token, characterPk); // ⬅ Reloads categories
            taskViewModel.loadTasks(token, characterPk);          // ⬅ Reloads tasks
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.taskListToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setTint(Color.WHITE);
    }


    private void observeViewModel() {
        // 🧠 Category loading
        categoryViewModel.getCategories().observe(this, categories -> {
            taskCategories.clear();
            taskCategories.addAll(categories);
            setupViewPager();

            if (recentlyCreatedCategoryName != null) {
                // Create a final variable to hold the target index
                final int[] targetIndexHolder = {-1};

                // Find the index of the newly created category
                for (int i = 0; i < taskCategories.size(); i++) {
                    if (taskCategories.get(i).getName().equals(recentlyCreatedCategoryName)) {
                        targetIndexHolder[0] = i;
                        break;
                    }
                }

                if (targetIndexHolder[0] != -1) {
                    viewPager.post(() -> {
                        viewPager.setCurrentItem(targetIndexHolder[0], true);
                        recentlyCreatedCategoryName = null;
                    });
                }
            }
        });

        // ❌ Category delete error
        categoryViewModel.getDeleteError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Failed to delete: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Category deleted
        categoryViewModel.getDeleteStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                String token = sharedPreferencesHelper.getAuthToken();
                String characterPk = sharedPreferencesHelper.getMainCharacterPk();
                categoryViewModel.loadCategories(token, characterPk);
            }
        });

        // ✅ Category created
        categoryViewModel.getCreationStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                String token = sharedPreferencesHelper.getAuthToken();
                String characterPk = sharedPreferencesHelper.getMainCharacterPk();
                categoryViewModel.loadCategories(token, characterPk);
            }
        });

        // ❌ General category errors
        categoryViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Task created
        taskViewModel.getCreationStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Task created!", Toast.LENGTH_SHORT).show();
                reloadCurrentFragmentTasks();
            }
        });

        // ❌ Task errors
        taskViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        taskViewModel.getDeletionStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Completed tasks deleted", Toast.LENGTH_SHORT).show();
                findViewById(R.id.confirmCompletedDeleteFrameLayout).setVisibility(View.GONE);
                reloadCurrentFragmentTasks(); // ✅ Refresh UI
            }
        });

        taskViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Failed to delete completed tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void reloadCurrentFragmentTasks() {
        int currentItem = viewPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentItem);

        if (fragment instanceof TaskCategoryFragment) {
            ((TaskCategoryFragment) fragment).refreshTasks(); // You implement this in TaskListFragment
        }
    }



    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        speedDialView = findViewById(R.id.addTaskFAB);

        confirmCategoryDeleteButton = findViewById(R.id.confirmCategoryDeleteButton);
        cancelCompletedDeleteButton = findViewById(R.id.cancelCompletedDeleteButton);
        confirmCompletedDeleteButton = findViewById(R.id.confirmCompletedDeleteButton);
        cancelCategoryDeleteButton = findViewById(R.id.cancelCategoryDeleteButton);

        // ✅ DELETE category on confirm
        confirmCategoryDeleteButton.setOnClickListener(v -> {
            TaskCategory activeCategory = getActiveCategory();
            if (activeCategory != null && !"My Tasks".equals(activeCategory.getName())) {
                String token = sharedPreferencesHelper.getAuthToken();
                categoryViewModel.deleteTaskCategory(activeCategory, token);
                FrameLayout confirmFrame = findViewById(R.id.confirmCategoryDeleteFrameLayout);
                confirmFrame.setVisibility(View.GONE);
            }
        });

        // ❌ Cancel category delete
        cancelCategoryDeleteButton.setOnClickListener(v -> {
            FrameLayout confirmFrame = findViewById(R.id.confirmCategoryDeleteFrameLayout);
            confirmFrame.setVisibility(View.GONE);
        });

        // ❌ Cancel completed task delete
        cancelCompletedDeleteButton.setOnClickListener(v -> {
            FrameLayout confirmFrame = findViewById(R.id.confirmCompletedDeleteFrameLayout);
            confirmFrame.setVisibility(View.GONE);
        });

        // ✅ Delete all completed tasks in the current category
        confirmCompletedDeleteButton.setOnClickListener(v -> {
            TaskCategory activeCategory = getActiveCategory();
            if (activeCategory != null) {
                String token = sharedPreferencesHelper.getAuthToken();
                taskViewModel.deleteCompletedTasks(activeCategory.getPk(), token);
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new TaskCategoryPagerAdapter(this, taskCategories);
        viewPager.setAdapter(pagerAdapter);

        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TaskCategory cat = taskCategories.get(position);

            if ("ADD_CATEGORY_KEY".equals(cat.getName())) {
                View customView = LayoutInflater.from(this).inflate(R.layout.tab_add_category_button, null);
                tab.setCustomView(customView);
                addCategoryTabIndex = position;
            } else if ("HIGH_PRIORITY_KEY".equals(cat.getName())) {
                View customView = LayoutInflater.from(this).inflate(R.layout.tab_high_priority_button, null);
                tab.setCustomView(customView);
            } else {
                tab.setText(cat.getName());
            }
        });
        tabLayoutMediator.attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (isAdjustingPage) return;

                // 👇 Handle swipe into "Add Category" tab
                if (position == addCategoryTabIndex) {
                    isAdjustingPage = true;
                    viewPager.post(() -> {
                        viewPager.setCurrentItem(Math.max(0, position - 1), false);
                        isAdjustingPage = false;
                    });
                }

                super.onPageSelected(position);
            }
        });
    }

    private void setupTabClickListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                TaskCategory cat = taskCategories.get(position);

                if ("ADD_CATEGORY_KEY".equals(cat.getName())) {

                    // 👇 Skip if we just came back from creating a category
                    if (skipNextAddCategoryClick || position == lastSelectedTabIndex) {
                        skipNextAddCategoryClick = false;
                        return;
                    }

                    // 👇 Re-select the previous tab
                    new Handler(Looper.getMainLooper()).post(() -> {
                        tabLayout.selectTab(tabLayout.getTabAt(Math.max(0, position - 1)));
                    });

                    skipNextAddCategoryClick = true;
                    lastSelectedTabIndex = position;

                    // 👇 Launch activity
                    Intent intent = new Intent(TasksActivity.this, AddTaskCategoryActivity.class);
                    addCategoryLauncher.launch(intent);
                } else {

                    sharedPreferencesHelper.saveActiveTaskCategoryName(cat.getName());
                    lastSelectedTabIndex = position;
                }
            }


            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }



    public void reloadCategories() {
        String token = sharedPreferencesHelper.getAuthToken();
        String characterPk = sharedPreferencesHelper.getMainCharacterPk();
        categoryViewModel.loadCategories(token, characterPk);
    }


    private void setupSpeedDial() {



        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(AUTO_TASK_BUTTON_ID, R.drawable.photo_camera)
                        .setLabel("Auto")
                        .setLabelBackgroundColor(getResources().getColor(R.color.black))
                        .setLabelColor(getResources().getColor(R.color.white))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(MANUAL_TASK_BUTTON_ID, R.drawable.sheet_icon)
                        .setLabel("Manual")
                        .setLabelBackgroundColor(getResources().getColor(R.color.black))
                        .setLabelColor(getResources().getColor(R.color.white))
                        .create()
        );

        Drawable icon = AppCompatResources.getDrawable(this, R.drawable.ic_add);
        if (icon != null) {
            icon.setTint(ContextCompat.getColor(this, android.R.color.black));
            speedDialView.setMainFabClosedDrawable(icon);
        }

        speedDialView.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case MANUAL_TASK_BUTTON_ID:
                    showManualTaskBottomSheet();
                    speedDialView.close();
                    return true; // Close FAB
                case AUTO_TASK_BUTTON_ID:
                    Intent intent = new Intent(this, CameraActivity.class);
                    cameraLauncher.launch(intent);
                    speedDialView.close();
                    return true;

            }
            return false;
        });
    }

    private void showManualTaskBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.add_task_bottom_sheet, null);
        dialog.setContentView(sheetView);

        EditText taskNameEditText = sheetView.findViewById(R.id.taskTitleEditText);
        EditText taskNoteEditText = sheetView.findViewById(R.id.descEditText);
        TextView createTaskButton = sheetView.findViewById(R.id.doneTextView);
        TextView dueDateTextView = sheetView.findViewById(R.id.dueDateTextView);
        TextView clearDueDateButton = sheetView.findViewById(R.id.clearDueDateButton);
        TextView highPriorityTextView = sheetView.findViewById(R.id.highPriorityTextView2);
        View dueDateContainer = sheetView.findViewById(R.id.dueDateContainer);

        final boolean[] isHighPriority = {true};
        final Calendar[] selectedDate = {null};

        // 1️⃣ Disable "Done" by default
        createTaskButton.setEnabled(false);
        createTaskButton.setAlpha(0.5f);

        // 2️⃣ Enable "Done" only if title is not empty
        taskNameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                boolean hasText = !s.toString().trim().isEmpty();
                createTaskButton.setEnabled(hasText);
                createTaskButton.setAlpha(hasText ? 1.0f : 0.5f);
            }
        });

        // 3️⃣ High Priority Toggle
        highPriorityTextView.setOnClickListener(v1 -> {
            isHighPriority[0] = !isHighPriority[0];
            if (isHighPriority[0]) {
                highPriorityTextView.setText("❗");
                highPriorityTextView.setTextColor(ContextCompat.getColor(this, R.color.high_priority_red));
            } else {
                highPriorityTextView.setText("❕");
                highPriorityTextView.setTextColor(ContextCompat.getColor(this, R.color.grey));
            }
        });

        // 4️⃣ Date Picker + Fade-in animation
        dueDateTextView.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                selectedDate[0] = Calendar.getInstance();
                selectedDate[0].set(selectedYear, selectedMonth, selectedDay);

                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                        selectedDay, selectedMonth + 1, selectedYear);
                dueDateTextView.setText(formattedDate);
                clearDueDateButton.setVisibility(View.VISIBLE);

                // 👇 Animate the container (fade in)
                dueDateContainer.setAlpha(0f);
                dueDateContainer.setVisibility(View.VISIBLE);
                dueDateContainer.animate().alpha(1f).setDuration(300).start();
            }, year, month, day);

            datePickerDialog.show();
        });

        // 5️⃣ Clear date + animation
        clearDueDateButton.setOnClickListener(v -> {
            selectedDate[0] = null;
            dueDateTextView.setText("Add date/time");
            clearDueDateButton.setVisibility(View.GONE);
        });

        // 6️⃣ Done Button logic
        createTaskButton.setOnClickListener(v1 -> {
            String taskName = taskNameEditText.getText().toString().trim();
            String taskNote = taskNoteEditText.getText().toString().trim();

            if (!taskName.isEmpty()) {
                boolean highPriority = isHighPriority[0];
                String dueDateString = null;

                if (selectedDate[0] != null) {
                    dueDateString = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedDate[0].get(Calendar.YEAR),
                            selectedDate[0].get(Calendar.MONTH) + 1,
                            selectedDate[0].get(Calendar.DAY_OF_MONTH));
                }

                // 🔍 Get current category
                TaskCategory activeCategory = getActiveCategory();

                // 🧠 If on HIGH_PRIORITY_KEY tab, fallback to "My Tasks"
                if ("HIGH_PRIORITY_KEY".equals(activeCategory.getName())) {
                    for (TaskCategory cat : taskCategories) {
                        if ("My Tasks".equalsIgnoreCase(cat.getName())) {
                            activeCategory = cat;
                            break;
                        }
                    }
                }

                if (activeCategory == null) {
                    Toast.makeText(this, "Failed to assign category.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Create and send task

                CreateTaskRequestBody createTaskRequestBody = new CreateTaskRequestBody();

                createTaskRequestBody.setTitle(taskName);
                createTaskRequestBody.setDescription(taskNote);
                if (highPriority) {
                    createTaskRequestBody.setPriority("high");
                } else {
                    createTaskRequestBody.setPriority("medium");
                }

                createTaskRequestBody.setStatus("pending");
                createTaskRequestBody.setDue_date(dueDateString);
                createTaskRequestBody.setCategoryPk(activeCategory.getPk());
                createTaskRequestBody.setCharacterPk(sharedPreferencesHelper.getMainCharacterPk());

                String token = sharedPreferencesHelper.getAuthToken();

                taskViewModel.createTask(createTaskRequestBody, token);
                dialog.dismiss(); // UI closes immediately, result handled by observer

            }
        });



        dialog.show();
    }


    public TaskCategory getActiveCategory() {
        int position = viewPager.getCurrentItem();
        if (position >= 0 && position < taskCategories.size()) {
            return taskCategories.get(position);
        }
        return null; // Or throw an exception if appropriate
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
