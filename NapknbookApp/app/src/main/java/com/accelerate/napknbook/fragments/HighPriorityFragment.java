package com.accelerate.napknbook.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.adapters.TaskRecyclerViewAdapter;
import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.edit.EditTaskActivity;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskViewModel;
import com.accelerate.napknbook.viewmodels.TaskViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class HighPriorityFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private TaskRecyclerViewAdapter activeTasksAdapter;

    public static HighPriorityFragment newInstance() {
        return new HighPriorityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());

        // Inject repository + debouncer
        TaskRepository taskRepository = new TaskRepository(requireContext());
        TaskViewModelFactory factory = new TaskViewModelFactory(taskRepository);
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_high_priority_task_list, container, false);

        RecyclerView activeRecycler = view.findViewById(R.id.tasksRecyclerView);
        activeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        String token = sharedPreferencesHelper.getAuthToken();
        String characterPk = sharedPreferencesHelper.getMainCharacterPk();

        // Adapter with taskViewModel + token for debouncing
        activeTasksAdapter = new TaskRecyclerViewAdapter(
                requireActivity(),
                new ArrayList<>(),
                TaskRecyclerViewAdapter.MODE_ACTIVE,
                token,
                taskViewModel,
                this::launchEditTaskIntent
        );
        activeRecycler.setAdapter(activeTasksAdapter);

        // Observe live tasks
        taskViewModel.getLiveTasksForCharacter(characterPk).observe(getViewLifecycleOwner(), tasks -> {
            Log.d("HighPriorityFragment", "Received tasks: " + tasks.size());
            List<Task> highPriority = new ArrayList<>();
            for (Task task : tasks) {
                if ("high".equals(task.getPriority()) && !task.isCompleted()) {
                    highPriority.add(task);
                }
            }
            activeTasksAdapter.setTasks(highPriority);
        });

        return view;
    }

    // Launcher to receive result from editing a task
    private final ActivityResultLauncher<Intent> editTaskLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshTasks(); // Reload from backend
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
        String token = sharedPreferencesHelper.getAuthToken();
        String characterPk = sharedPreferencesHelper.getMainCharacterPk();
        taskViewModel.loadTasks(token, characterPk);
    }
}
