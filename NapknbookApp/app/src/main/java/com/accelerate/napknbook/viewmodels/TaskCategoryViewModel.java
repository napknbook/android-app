package com.accelerate.napknbook.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.models.TaskCategory;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCategoryViewModel extends ViewModel {

    private final MutableLiveData<List<TaskCategory>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> creationStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final TaskCategoryRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();
    private final MutableLiveData<String> deleteError = new MutableLiveData<>();


    private String lastCreatedCategoryName;

    public TaskCategoryViewModel(TaskCategoryRepository repository) {
        this.repository = repository;
    }

    // --- Getters for LiveData ---
    public LiveData<List<TaskCategory>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public LiveData<Boolean> getCreationStatus() {
        return creationStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public String getLastCreatedCategoryName() {
        return lastCreatedCategoryName;
    }


    public LiveData<Boolean> getDeleteStatus() {
        return deleteStatus;
    }

    public LiveData<String> getDeleteError() {
        return deleteError;
    }

    public void syncCategoriesFromServer(String token, String characterPk) {
        repository.syncCategoriesFromServer(token, characterPk, new TaskCategoryRepository.CategoriesCallback() {
            @Override
            public void onSuccess(List<TaskCategory> result) {
                List<TaskCategory> updated = ensureVirtualCategories(result, characterPk);
                categories.postValue(updated);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue("Failed to sync categories: " + error);
            }
        });
    }


    public void clearLastCreatedCategoryName() {
        lastCreatedCategoryName = null;
    }

    // --- Load categories from local DB or backend ---
    public void loadCategories(String token, String characterPk) {
        isLoading.postValue(true);

        repository.syncIfNeeded(token, characterPk, new TaskCategoryRepository.CategoriesCallback() {
            @Override
            public void onSuccess(List<TaskCategory> result) {
                List<TaskCategory> updated = ensureVirtualCategories(result, characterPk);
                categories.postValue(updated);
                isLoading.postValue(false);

            }

            @Override
            public void onFailure(String error) {
                Log.e("TaskCategoryViewModel", "Failed to load categories: " + error);
                errorMessage.postValue("Failed to load categories");
                isLoading.postValue(false);

            }
        });
    }

    // --- Create new category and reload list on success ---
    public void createTaskCategory(String token, String name, String characterPk) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("characterPk", characterPk);

        repository.createTaskCategory(token, body, new TaskCategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(TaskCategory result) {
                // ✅ Use backend-confirmed name
                lastCreatedCategoryName = result.getName();

                creationStatus.postValue(true);
                loadCategories(token, characterPk);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void updateTaskCategory(String token, String categoryPk, String name) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);

        repository.updateTaskCategory(token, categoryPk, body, new TaskCategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(TaskCategory category) {
                updateStatus.postValue(true);
                // 🔄 Reload categories so tabs refresh
                loadCategories(token, SharedPreferencesHelper.getInstance(null).getMainCharacterPk());
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
            }
        });
    }


    public void deleteTaskCategory(TaskCategory category, String token) {
        repository.deleteTaskCategory(category, token, new TaskCategoryRepository.DeletionCallback() {
            @Override
            public void onSuccess() {
                deleteStatus.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                deleteError.postValue(error);
            }
        });
    }

    // --- Inject special tabs if missing ---
    private List<TaskCategory> ensureVirtualCategories(List<TaskCategory> categories, String characterPk) {
        boolean hasMyTasks = false;
        boolean hasAddCategory = false;
        boolean hasHighPriority = false;


        for (TaskCategory cat : categories) {
            if ("My Tasks".equals(cat.getName()) && characterPk.equals(cat.getCharacterPk())) {
                hasMyTasks = true;
            }
            if ("ADD_CATEGORY_KEY".equals(cat.getName())) {
                hasAddCategory = true;
            }
            if ("HIGH_PRIORITY_KEY".equals(cat.getName())) {
                hasHighPriority = true;
            }
        }

        if (!hasMyTasks) {
            TaskCategory defaultCategory = new TaskCategory();
            defaultCategory.setPk("virtual_my_tasks_" + characterPk); // avoid collisions
            defaultCategory.setName("My Tasks");
            defaultCategory.setOrder(0);
            defaultCategory.setCharacterPk(characterPk);
            categories.add(0, defaultCategory); // Always first
        }

        if (!hasHighPriority) {
            categories.add(0, new TaskCategory("HIGH_PRIORITY_KEY", "HIGH_PRIORITY_KEY", new ArrayList<>()));
        }

        if (!hasAddCategory) {
            categories.add(new TaskCategory("ADD_CATEGORY_KEY", "ADD_CATEGORY_KEY", new ArrayList<>()));
        }

        return categories;
    }


    public LiveData<TaskCategory> getCategoryByPkLive(String categoryPk) {
        return repository.getCategoryByPkLive(categoryPk);
    }
}
