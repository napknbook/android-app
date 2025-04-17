package com.accelerate.napknbook.utils;

import android.os.Handler;
import android.os.Looper;

import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.models.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskUpdateDebouncer {

    private final Map<String, Runnable> pendingUpdates = new HashMap<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TaskRepository taskRepository;

    public TaskUpdateDebouncer(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public void scheduleUpdate(Task task, String token) {
        String taskPk = task.getPk();

        if (pendingUpdates.containsKey(taskPk)) {
            handler.removeCallbacks(pendingUpdates.get(taskPk));
        }

        Runnable updateRunnable = () -> {
            if (task.isJustToggledPriority()) {
                taskRepository.updateTaskPriorityOnly(task, token);
            } else if (task.isJustToggledCompleted()) {
                taskRepository.updateTaskCompletedOnly(task, token);
            } else {
                taskRepository.updateTask(task, token, null); // fallback full update
            }
            pendingUpdates.remove(taskPk);
        };

        pendingUpdates.put(taskPk, updateRunnable);
        handler.postDelayed(updateRunnable, 200);
    }


    public void cancelPendingUpdate(String taskPk) {
        if (pendingUpdates.containsKey(taskPk)) {
            handler.removeCallbacks(pendingUpdates.get(taskPk));
            pendingUpdates.remove(taskPk);
        }
    }
}
