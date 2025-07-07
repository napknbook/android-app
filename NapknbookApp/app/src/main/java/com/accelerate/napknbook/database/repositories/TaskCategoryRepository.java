package com.accelerate.napknbook.database.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.AppDatabase;
import com.accelerate.napknbook.database.daos.TaskCategoryDao;
import com.accelerate.napknbook.models.TaskCategory;

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

public class TaskCategoryRepository {

    private static final String TAG = "TaskCategoryRepo";

    private final TaskCategoryDao categoryDao;
    private final NapknbookService api;
    private final Executor executor;

    public TaskCategoryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        categoryDao = db.taskCategoryDao();
        api = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        executor = Executors.newSingleThreadExecutor();
    }

    public void syncIfNeeded(String token, String characterPk, CategoriesCallback callback) {
        executor.execute(() -> {
            List<TaskCategory> localCategories = categoryDao.getAllTaskCategories(characterPk);

            if (localCategories.isEmpty()) {
                Log.d(TAG, "No categories found, syncing from server.");
                syncCategoriesFromServer(token, characterPk, callback);
                return;
            }

            TaskCategory category = localCategories.get(0);
            long now = System.currentTimeMillis();
            long createdTime = category.getLocalCreatedOn();
            long ageMillis = now - createdTime;

            if (ageMillis > 3600000) {
                Log.d(TAG, "Categories are stale, syncing from server.");
                syncCategoriesFromServer(token, characterPk, callback);
            } else {
                Log.d(TAG, "Categories are recent, no sync needed.");
                callback.onSuccess(localCategories);
            }
        });
    }


    // --- Sync categories from server ---
    public void syncCategoriesFromServer(String token, String characterPk, CategoriesCallback callback) {
        api.getTaskCategories("Bearer " + token, characterPk).enqueue(new Callback<List<TaskCategory>>() {
            @Override
            public void onResponse(Call<List<TaskCategory>> call, Response<List<TaskCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TaskCategory> categories = response.body();
                    executor.execute(() -> {
                        try {
                            categoryDao.deleteTaskCategoriesForCharacter(characterPk);
                            categoryDao.insertTaskCategories(categories);

                            // ✅ Fetch updated list from DB and return via callback
                            List<TaskCategory> updated = categoryDao.getAllTaskCategoriesNow(characterPk);
                            callback.onSuccess(updated);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to sync local DB with categories", e);
                            callback.onFailure("Local DB sync failed");
                        }
                    });
                } else {
                    callback.onFailure("Server returned: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaskCategory>> call, Throwable t) {
                Log.e(TAG, "Network error during category sync", t);
                callback.onFailure("Network error");
            }
        });
    }



    public void createTaskCategory(String token, Map<String, String> categoryData, CategoryCallback callback) {
        api.createTaskCategory("Bearer " + token, categoryData).enqueue(new Callback<TaskCategory>() {
            @Override
            public void onResponse(Call<TaskCategory> call, Response<TaskCategory> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        categoryDao.insertTaskCategory(response.body());
                        callback.onSuccess(response.body());
                    });
                } else {
                    callback.onFailure("Create category failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskCategory> call, Throwable t) {
                callback.onFailure("Failed to create category: " + t.getMessage());
            }
        });
    }

    // --- Update a category with a callback ---
    public void updateTaskCategory(String token, String categoryPk, Map<String, String> categoryData, CategoryCallback callback) {
        api.updateTaskCategory("Bearer " + token, categoryPk, categoryData).enqueue(new Callback<TaskCategory>() {
            @Override
            public void onResponse(Call<TaskCategory> call, Response<TaskCategory> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        categoryDao.updateTaskCategory(response.body());
                        callback.onSuccess(response.body());
                    });
                } else {
                    String error = "Update failed: " + response.code();
                    Log.w(TAG, error);
                    callback.onFailure(error);
                }
            }

            @Override
            public void onFailure(Call<TaskCategory> call, Throwable t) {
                String error = "Failed to update category: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onFailure(error);
            }
        });
    }



    // --- Delete a category ---
    public void deleteTaskCategory(TaskCategory category, String token, DeletionCallback callback) {
        if (category == null || category.getPk() == null || category.getName().equals("My Tasks") || category.getName().equals("HIGH_PRIORITY_KEY")) {
            Log.w(TAG, "Invalid category passed to delete");
            return;
        }

        api.deleteTaskCategory("Bearer " + token, category.getPk()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> categoryDao.deleteTaskCategory(category));
                    callback.onSuccess();
                } else {
                    Log.w(TAG, "Delete category failed: " + response.code());
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to delete category", t);
                callback.onFailure(t.getMessage());
            }
        });
    }

    public LiveData<TaskCategory> getCategoryByPkLive(String categoryPk) {
        return categoryDao.getCategoryByPkLive(categoryPk);
    }

    public interface DeletionCallback {
        void onSuccess();
        void onFailure(String error);
    }



    // --- Load from local DB (blocking) ---
    //public List<TaskCategory> getLocalTaskCategoriesNow() {
    //    try {
    //        return categoryDao.getAllTaskCategories();
    //    } catch (Exception e) {
    //        Log.e(TAG, "Failed to fetch categories", e);
    //        return Collections.emptyList();
    //    }
    //}

    public void getAllCategories(String token, String characterPk, CategoriesCallback callback) {
        executor.execute(() -> {
            List<TaskCategory> local = categoryDao.getAllTaskCategoriesNow(characterPk);

            if (local == null || local.isEmpty()) {
                Log.d(TAG, "No local categories, syncing from server");
                api.getTaskCategories("Bearer " + token, characterPk).enqueue(new Callback<List<TaskCategory>>() {
                    @Override
                    public void onResponse(Call<List<TaskCategory>> call, Response<List<TaskCategory>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            executor.execute(() -> {
                                categoryDao.deleteTaskCategoriesForCharacter(characterPk);
                                categoryDao.insertTaskCategories(response.body());
                                callback.onSuccess(response.body());
                            });
                        } else {
                            callback.onFailure("Failed to load from server: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TaskCategory>> call, Throwable t) {
                        callback.onFailure("Network error: " + t.getMessage());
                    }
                });
            } else {
                Log.d(TAG, "Loaded categories from local DB");
                callback.onSuccess(local);
            }
        });
    }
    public interface CategoriesCallback {

        void onSuccess(List<TaskCategory> categories);
        void onFailure(String error);
    }

    public interface CategoryCallback {
        void onSuccess(TaskCategory category);
        void onFailure(String error);
    }


}
