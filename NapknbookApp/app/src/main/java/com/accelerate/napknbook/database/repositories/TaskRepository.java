package com.accelerate.napknbook.database.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.AppDatabase;
import com.accelerate.napknbook.database.daos.TaskDao;
import com.accelerate.napknbook.models.CreateTaskRequestBody;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.models.UpdateTaskRequestBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskRepository {

    private static final String TAG = "TaskRepository";

    private final TaskDao taskDao;
    private final NapknbookService api;
    private final Executor executor;

    public TaskRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        this.taskDao = db.taskDao();
        this.api = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void syncIfNeeded(String token, String characterPk, TaskListCallback callback) {
        executor.execute(() -> {
            List<Task> localTasks = taskDao.getTasksForCharacterNow(characterPk);
            boolean shouldSync = localTasks.isEmpty() ||
                    (System.currentTimeMillis() - localTasks.get(0).getLocalCreatedOn() > 3600000);

            if (shouldSync) {
                Log.d(TAG, "Tasks are empty or stale, syncing from server.");
                syncTasksFromServer(token, characterPk, callback);
            } else {
                Log.d(TAG, "Tasks are fresh, using local copy.");
                if (callback != null) callback.onSuccess(localTasks);
            }
        });
    }


    public void syncTasksFromServer(String token, String characterPk) {
        syncTasksFromServer(token, characterPk, null);
    }

    public void syncTasksFromServer(String token, String characterPk, TaskListCallback callback) {
        api.getTasks("Bearer " + token, characterPk).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tasks = response.body();
                    long now = System.currentTimeMillis();

                    for (Task task : tasks) {
                        task.setLocalCreatedOn(now);
                    }

                    executor.execute(() -> {
                        try {
                            taskDao.clearTasksForCharacter(characterPk);
                            taskDao.insertTasks(tasks);
                            List<Task> updated = taskDao.getTasksForCharacterNow(characterPk);
                            if (callback != null) callback.onSuccess(updated);
                        } catch (Exception e) {
                            Log.e(TAG, "DB insert failed", e);
                            if (callback != null) callback.onFailure("DB error: " + e.getMessage());
                        }
                    });
                } else {
                    if (callback != null) callback.onFailure("Sync failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e(TAG, "Network sync failed", t);
                if (callback != null) callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public void createTask(CreateTaskRequestBody requestBody, String token, TaskCallback callback) {
        api.createTask("Bearer " + token, requestBody).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task created = response.body();
                    created.setLocalCreatedOn(System.currentTimeMillis());
                    executor.execute(() -> taskDao.insertTask(created));
                    if (callback != null) callback.onSuccess(created);
                } else {
                    Log.w(TAG, "Create task failed: " + response.code());
                    if (callback != null) callback.onFailure("API Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e(TAG, "Create task network failure", t);
                if (callback != null) callback.onFailure("Network Error: " + t.getMessage());
            }
        });
    }
    public void updateTask(Task task, String token, TaskCallback callback) {
        if (task == null || task.getPk() == null) {
            Log.w(TAG, "Invalid task for update");
            if (callback != null) callback.onFailure("Invalid task");
            return;
        }

        UpdateTaskRequestBody updateTaskRequestBody = new UpdateTaskRequestBody();
        updateTaskRequestBody.setTitle(task.getTitle());
        updateTaskRequestBody.setDescription(task.getDescription());
        updateTaskRequestBody.setDue_date(task.getDueDate());
        updateTaskRequestBody.setCompleted(task.isCompleted());
        updateTaskRequestBody.setPriority(task.getPriority());
        updateTaskRequestBody.setStatus(task.getStatus());

        api.updateTask("Bearer " + token, task.getPk(), updateTaskRequestBody).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task updated = response.body();
                    updated.setLocalCreatedOn(System.currentTimeMillis());
                    executor.execute(() -> {
                        taskDao.updateTask(updated);
                        if (callback != null) callback.onSuccess(updated);
                    });
                } else {
                    Log.w(TAG, "Update failed: " + response.code());
                    if (callback != null) callback.onFailure("API Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e(TAG, "Update task network failure", t);
                if (callback != null) callback.onFailure("Network Error: " + t.getMessage());
            }
        });
    }
    public void updateTaskPriorityOnly(Task task, String token) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("priority", task.getPriority());

        api.updateTaskPriority("Bearer " + token, task.getPk(), body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "Failed to update priority: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Priority update failed", t);
            }
        });
    }
    public void updateTaskCompletedOnly(Task task, String token) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("completed", task.isCompleted());

        api.updateTaskField("Bearer " + token, task.getPk(), body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "Failed to update completed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Completed update failed", t);
            }
        });
    }


    public LiveData<List<Task>> getTasksForCategory(String characterPk, String categoryPk) {
        return taskDao.getTasksByCharacterAndCategory(characterPk, categoryPk);
    }

    public void deleteTask(String taskPk, String token, DeletionCallback callback) {
        api.deleteTask("Bearer " + token, taskPk).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> {
                        taskDao.deleteTask(taskPk);
                        if (callback != null) callback.onSuccess();
                    });
                } else {
                    Log.w(TAG, "Delete task failed: " + response.code());
                    if (callback != null) callback.onFailure("API Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Delete task network failure", t);
                if (callback != null) callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }


    public void deleteCompletedTasksForCategory(String categoryPk, String token, DeletionCallback callback) {
        api.deleteCompletedTasks("Bearer " + token, categoryPk).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> {
                        taskDao.deleteCompletedTasks(categoryPk);
                        if (callback != null) callback.onSuccess();
                    });
                } else {
                    Log.w(TAG, "Delete completed failed: " + response.code());
                    if (callback != null) callback.onFailure("API Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Delete completed network failure", t);
                if (callback != null) callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Task>> getTasksForCategoryLive(String categoryPk) {
        return taskDao.getTasksByCategoryLive(categoryPk);
    }

    public List<Task> getLocalTasksForCharacterNow(String characterPk) {
        try {
            return taskDao.getTasksForCharacterNow(characterPk);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch local tasks", e);
            return Collections.emptyList();
        }
    }

    public LiveData<List<Task>> getLiveTasksForCharacter(String characterPk) {
        return taskDao.getLiveTasksForCharacter(characterPk);
    }

    public interface TaskCallback {
        void onSuccess(Task task);
        void onFailure(String error);
    }

    public interface TaskListCallback {
        void onSuccess(List<Task> tasks);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onComplete(boolean success);
    }

    public interface DeletionCallback {
        void onSuccess();
        void onFailure(String error);
    }
}