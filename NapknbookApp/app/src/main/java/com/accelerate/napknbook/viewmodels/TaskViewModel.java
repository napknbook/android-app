package com.accelerate.napknbook.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.models.CreateTaskRequestBody;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.utils.TaskUpdateDebouncer;

import java.util.List;

public class TaskViewModel extends ViewModel {

    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletionStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> creationStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> singleTaskDeletionStatus = new MutableLiveData<>();// ✅ NEW

    private final TaskRepository repository;
    private final TaskUpdateDebouncer debouncer ;

    public TaskViewModel(TaskRepository repository, TaskUpdateDebouncer debouncer) {
        this.repository = repository;
        this.debouncer = debouncer;
    }

    // --- LiveData Getters ---
    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<Boolean> getDeletionStatus() {
        return deletionStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getCreationStatus() {
        return creationStatus;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public LiveData<Boolean> getSingleTaskDeletionStatus() { // NEW
        return singleTaskDeletionStatus;
    }

    public LiveData<List<Task>> getTasksForCategory(String characterPk, String categoryPk) {
        return repository.getTasksForCategory(characterPk, categoryPk);
    }

    public void syncTasksFromServer(String token, String characterPk) {
        isLoading.setValue(true);
        repository.syncTasksFromServer(token, characterPk, new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> result) {
                tasks.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
                isLoading.postValue(false);
            }
        });
    }



    // --- Load Tasks from Backend ---
    public void loadTasks(String token, String characterPk) {
        isLoading.setValue(true);
        repository.syncIfNeeded(token, characterPk, new TaskRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> result) {
                tasks.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
                isLoading.postValue(false);
                Log.e("TaskViewModel", "Failed to load tasks: " + error);
            }
        });
    }

    // --- Create Task ---
    public void createTask(CreateTaskRequestBody requestBody, String token) {
        repository.createTask(requestBody, token, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task createdTask) {
                creationStatus.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
                creationStatus.postValue(false);
            }
        });
    }


    // --- Delete Completed Tasks ---
    public void deleteCompletedTasks(String categoryPk, String token) {
        repository.deleteCompletedTasksForCategory(categoryPk, token, new TaskRepository.DeletionCallback() {
            @Override
            public void onSuccess() {
                deletionStatus.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void deleteTask(String taskPk, String token) {
        repository.deleteTask(taskPk, token, new TaskRepository.DeletionCallback() {
            @Override
            public void onSuccess() {
                singleTaskDeletionStatus.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.postValue(error);
                singleTaskDeletionStatus.postValue(false);
            }
        });
    }


    // --- Update Task ---
    public void updateTask(Task task, String token) {
        repository.updateTask(task, token, new TaskRepository.TaskCallback() {
            @Override
            public void onSuccess(Task updatedTask) {
                updateStatus.postValue(true);
            }

            @Override
            public void onFailure(String error) {
                updateStatus.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void debounceTaskUpdate(Task task, String token) {
        debouncer.scheduleUpdate(task, token);
    }

    public LiveData<List<Task>> getLiveTasksForCharacter(String characterPk) {
        return repository.getLiveTasksForCharacter(characterPk);
    }

}
