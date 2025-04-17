package com.accelerate.napknbook.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.TasksActivity;
import com.accelerate.napknbook.adapters.TaskRecyclerViewAdapter;
import com.accelerate.napknbook.database.repositories.TaskRepository;

import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.edit.EditTaskActivity;
import com.accelerate.napknbook.edit.EditTaskCategoryActivity;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModel;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModelFactory;
import com.accelerate.napknbook.viewmodels.TaskViewModel;
import com.accelerate.napknbook.viewmodels.TaskViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;



public class TaskCategoryFragment extends Fragment {



    private static final String ARG_CATEGORY_PK = "category_pk";
    private String categoryPk;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private TaskViewModel taskViewModel;
    String authToken ;

    private TaskRecyclerViewAdapter activeTasksAdapter;
    private TaskRecyclerViewAdapter completedTasksAdapter;
    private boolean isRotated = false;

    public static TaskCategoryFragment newInstance(String categoryPk) {
        TaskCategoryFragment fragment = new TaskCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_PK, categoryPk);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryPk = getArguments().getString(ARG_CATEGORY_PK);
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());
        authToken = sharedPreferencesHelper.getAuthToken() ;

        TaskRepository repository = new TaskRepository(requireContext());
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(repository)).get(TaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        setupUI(view);
        observeTasks();
        return view;
    }

    private void setupUI(View view) {
        RecyclerView activeRecycler = view.findViewById(R.id.tasksRecyclerView);
        RecyclerView completedRecycler = view.findViewById(R.id.completedTasksRecyclerView);

        activeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        completedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView categoryNameTextView = view.findViewById(R.id.textView13);

        TaskCategoryRepository categoryRepo = new TaskCategoryRepository(requireContext());
        TaskCategoryViewModel tempViewModel = new ViewModelProvider(this, new TaskCategoryViewModelFactory(categoryRepo))
                .get(TaskCategoryViewModel.class);

        tempViewModel.getCategoryByPkLive(categoryPk).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                categoryNameTextView.setText(category.getName());
            }
        });



        activeTasksAdapter = new TaskRecyclerViewAdapter(getContext(), new ArrayList<>() ,"active", authToken, taskViewModel, this::launchEditTaskIntent);
        completedTasksAdapter = new TaskRecyclerViewAdapter(getContext(), new ArrayList<>(), "completed", authToken, taskViewModel, this::launchEditTaskIntent);

        activeRecycler.setAdapter(activeTasksAdapter);
        completedRecycler.setAdapter(completedTasksAdapter);

        TextView expandCompletedTasksTextView = view.findViewById(R.id.expandCompletedTasksTextView);
        Button expandCompletedTasksButton = view.findViewById(R.id.expandCompletedTasksButton);
        expandCompletedTasksButton.setOnClickListener(v -> {
            boolean show = completedRecycler.getVisibility() != View.VISIBLE;
            completedRecycler.setVisibility(show ? View.VISIBLE : View.GONE);
            float rotation = isRotated ? 0f : 180f;
            expandCompletedTasksTextView.animate().rotation(rotation).setDuration(300).start();
            isRotated = !isRotated;
        });

        setupCategoryOptionsMenu(view);
    }

    private void observeTasks() {
        taskViewModel.getTasksForCategory(sharedPreferencesHelper.getMainCharacterPk(), categoryPk).observe(getViewLifecycleOwner(), tasks -> {
            List<Task> active = new ArrayList<>();
            List<Task> completed = new ArrayList<>();
            for (Task task : tasks) {
                if (task.isCompleted()) completed.add(task);
                else active.add(task);
            }
            activeTasksAdapter.setTasks(active);
            completedTasksAdapter.setTasks(completed);
        });
    }

    // Launcher to receive result from rename activity
    private final ActivityResultLauncher<Intent> editCategoryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && getActivity() instanceof TasksActivity) {
                    ((TasksActivity) getActivity()).reloadCategories();
                }
            });

    private void setupCategoryOptionsMenu(View view) {
        TextView optionsTextView = view.findViewById(R.id.optionsTextView);
        optionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                View taskCategoryMenu = LayoutInflater.from(requireContext()).inflate(R.layout.task_category_bottom_sheet_menu, null);
                bottomSheetDialog.setContentView(taskCategoryMenu);
                bottomSheetDialog.show();

                TextView renameCategoryTextView = taskCategoryMenu.findViewById(R.id.optionOne);
                TextView deleteCategoryTextView = taskCategoryMenu.findViewById(R.id.optionTwo);
                TextView deleteCompletedTextView = taskCategoryMenu.findViewById(R.id.optionThree);

                if (sharedPreferencesHelper.getActiveTaskCategoryName().equals("My Tasks")) {
                    renameCategoryTextView.setTextColor(Color.GRAY);
                    renameCategoryTextView.setText("Default category can't be renamed");

                    deleteCategoryTextView.setTextColor(Color.GRAY);
                    deleteCategoryTextView.setText("Default category can't be deleted");
                } else {
                    deleteCategoryTextView.setOnClickListener(v1 -> {
                        FrameLayout confirmCategoryDeleteFrameLayout = requireActivity().findViewById(R.id.confirmCategoryDeleteFrameLayout);
                        confirmCategoryDeleteFrameLayout.setVisibility(View.VISIBLE);
                        bottomSheetDialog.dismiss();
                    });

                    renameCategoryTextView.setOnClickListener(v1 -> {
                        Intent intent = new Intent(requireContext(), EditTaskCategoryActivity.class);
                        intent.putExtra("categoryPk", categoryPk);
                        intent.putExtra("categoryName", sharedPreferencesHelper.getActiveTaskCategoryName());
                        editCategoryLauncher.launch(intent);
                        bottomSheetDialog.dismiss();
                    });
                }

                deleteCompletedTextView.setOnClickListener(v1 -> {
                    FrameLayout confirmCompletedDeleteFrameLayout = requireActivity().findViewById(R.id.confirmCompletedDeleteFrameLayout);
                    confirmCompletedDeleteFrameLayout.setVisibility(View.VISIBLE);
                    bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.setOnDismissListener(dialog -> {
                    // Optional: handle cleanup
                });
            }
        });

    }

    // Launcher to receive result from editing a task
    private final ActivityResultLauncher<Intent> editTaskLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshTasks(); // 👈 reload your task list after editing
                }
            });

    public void launchEditTaskIntent(Task task) {
        Intent intent = new Intent(requireContext(), EditTaskActivity.class);
        intent.putExtra("taskPk", task.getPk());
        intent.putExtra("title", task.getTitle());
        intent.putExtra("description", task.getDescription());
        intent.putExtra("dueDate", task.getDueDate());
        intent.putExtra("priority", task.getPriority());
        intent.putExtra("status", task.getStatus());
        intent.putExtra("completed", task.isCompleted());
        intent.putExtra("characterPk", task.getCharacterPk());
        intent.putExtra("categoryPk", task.getCategoryPk());

        editTaskLauncher.launch(intent);
    }


    public void refreshTasks() {
    }
}
